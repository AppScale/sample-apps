// Copyright 2011 Google Inc. All rights reserved.

package com.google.appengine.api.files;

import static com.google.appengine.api.files.RecordConstants.BLOCK_SIZE;
import static com.google.appengine.api.files.RecordConstants.HEADER_LENGTH;
import static com.google.appengine.api.files.RecordConstants.maskCrc;

import com.google.appengine.api.files.RecordConstants.RecordType;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * An implementation of a {@link RecordWriteChannel}.
 *
 */
final class RecordWriteChannelImpl implements RecordWriteChannel {

  /**
   * A class that holds information needed to write a physical record.
   */
  private static final class Record {
    private final RecordType type;
    private final int bytes;

    private Record() {
      type = RecordType.NONE;
      bytes = 0;
    }

    private Record(RecordType type, int bytes) {
      Preconditions.checkArgument(type != RecordType.UNKNOWN);
      Preconditions.checkArgument(bytes >= 0);
      this.type = type;
      this.bytes = bytes;
    }

    /**
     * Returns the number of bytes that needs to be written.
     *
     * @return the number of bytes.
     */
    int getBytes() {
      return bytes;
    }

    /**
     * Returns the type of record that needs to be written.
     *
     * @return the type.
     */
    RecordType getType() {
      return type;
    }

  }

  private final Object lock = new Object();
  private final FileWriteChannel output;
  private ByteBuffer writeBuffer;
  private String nextSequenceKey;

  /**
   * @param output a {@link FileWriteChannel} to write the record to.
   */
  public RecordWriteChannelImpl(FileWriteChannel output) {
    this.output = output;
    writeBuffer = ByteBuffer.allocate(BLOCK_SIZE);
    writeBuffer.order(ByteOrder.LITTLE_ENDIAN);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int write(ByteBuffer data) throws IOException {
    return write(data, null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isOpen() {
    synchronized (lock) {
      return output.isOpen();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int write(ByteBuffer data, String sequenceKey) throws IOException {
    synchronized (lock) {
      int bytesWritten = 0;
      Record lastRecord = new Record();

      do {
        int bytesToBlockEnd = writeBuffer.remaining();
        Record currentRecord = createRecord(data, bytesToBlockEnd, lastRecord);
        writePhysicalRecord(data, currentRecord);
        bytesWritten += currentRecord.getBytes() + HEADER_LENGTH;
        lastRecord = currentRecord;

        if ((lastRecord.getType() == RecordType.FULL)
            || (lastRecord.getType() == RecordType.LAST)) {
          nextSequenceKey = sequenceKey;
        }

        bytesToBlockEnd = writeBuffer.remaining();
        if ((bytesToBlockEnd < HEADER_LENGTH) && (bytesToBlockEnd > 0)) {
          writeBlanks(bytesToBlockEnd);
          bytesWritten += bytesToBlockEnd;
          bytesToBlockEnd = 0;
        }

        if (bytesToBlockEnd == 0) {
          writeBuffer.flip();
          output.write(writeBuffer, nextSequenceKey);
          writeBuffer.clear();
          nextSequenceKey = null;
        }
      } while (data.hasRemaining());
      return bytesWritten;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void closeFinally() throws IllegalStateException, IOException {
    synchronized (lock) {
      closeStream(false);
      output.closeFinally();
    }
  }

  /**
   * Closes and finalizes the {@link RecordWriteChannel}.
   */
  @Override
  public void close() throws IOException {
    synchronized (lock) {
      closeStream(true);
      output.close();
    }
  }

  /**
   * Fills a {@link Record} object with data about the physical record to write.
   *
   * @param data the users data.
   * @param bytesToBlockEnd remaining bytes in the current block.
   * @param lastRecord a {@link Record} representing the last physical record written.
   * @return the {@link Record} with new write data.
   **/
  private static Record createRecord(ByteBuffer data, int bytesToBlockEnd, Record lastRecord) {
    int bytesToDataEnd = data.remaining();
    RecordType type = RecordType.UNKNOWN;
    int bytes = -1;
    if ((lastRecord.getType() == RecordType.NONE)
        && ((bytesToDataEnd + HEADER_LENGTH) <= bytesToBlockEnd)) {
      type = RecordType.FULL;
      bytes = bytesToDataEnd;
    } else if (lastRecord.getType() == RecordType.NONE) {
      type = RecordType.FIRST;
      bytes = bytesToBlockEnd - HEADER_LENGTH;
    } else if (bytesToDataEnd <= bytesToBlockEnd) {
      type = RecordType.LAST;
      bytes = bytesToDataEnd;
    } else {
      type = RecordType.MIDDLE;
      bytes = bytesToBlockEnd - HEADER_LENGTH;
    }
    return new Record(type, bytes);
  }

  /**
   * This method creates a record inside of a {@link ByteBuffer}
   *
   * @param data The data to output.
   * @param record A {@link RecordWriteChannelImpl.Record} object that describes
   *        which data to write.
   */
  private void writePhysicalRecord(ByteBuffer data, Record record) {
    writeBuffer.putInt(generateCrc(data.array(), data.position(), record.getBytes(),
        record.getType()));
    writeBuffer.putShort((short) record.getBytes());
    writeBuffer.put(record.getType().value());
    writeBuffer.put(data.array(), data.position(), record.getBytes());
    data.position(data.position() + record.getBytes());
  }

  /**
   * Fills the {@link ByteBuffer} with 0x00;
   *
   * @param numBlanks the number of bytes to pad.
   */
  private void writeBlanks(int numBlanks) {
    for (int i = 0; i < numBlanks; i++) {
      writeBuffer.put((byte) 0x00);
    }
  }

  /**
   * Generates a CRC32C checksum using {@link Crc32c} for a specific record.
   *
   * @param data The user data over which the checksum will be generated.
   * @param off The offset into the user data at which to begin the computation.
   * @param len The length of user data to use in the computation.
   * @param type The {@link RecordType} of the record, which is included in the
   *        checksum.
   * @return the masked checksum.
   */
  private int generateCrc(byte[] data, int off, int len, RecordType type) {
    Crc32c crc = new Crc32c();
    crc.update(type.value());
    crc.update(data, off, len);
    return (int) maskCrc(crc.getValue());
  }

  /**
   * Closes the stream and adds padding to the end of the block without closing
   * the underlying {@link AppEngineFile}.
   *
   * @throws IOException
   */
  private void closeStream(boolean pad) throws IOException {
    int bytesToBlockEnd = writeBuffer.remaining();
    if (bytesToBlockEnd < BLOCK_SIZE) {
      if (pad) {
        writeBlanks(bytesToBlockEnd);
      }
      writeBuffer.flip();
      output.write(writeBuffer);
      writeBuffer.clear();
    }
  }

}
