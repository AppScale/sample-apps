// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.appengine.api.xmpp;

import com.google.appengine.api.utils.HttpRequestParser;
import com.google.appengine.api.xmpp.ErrorBuilder;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.MessagingException;

/**
 * {@code InboundErrorParser} encapsulates the use of the {@code
 * javax.mail} package to parse an incoming {@code
 * multipart/form-data} HTTP request and to convert it to an {@link
 * Error} object.
 *
 *
 */
class InboundErrorParser extends HttpRequestParser {
  /**
   * Parse the POST data of the given request to create an {@link Error} object.
   *
   * @params req The {@link HttpServletRequest} whose POST data should be parsed.
   *
   * @return A {@link Error} object with fields retrieved from the POST data.
   *
   * @throws IOException if the POST data cannot be read.
   * @throws MessagingException if the POST data cannot be parsed.
   * @throws IllegalStateException if the request's POST data input stream has
   *     already been read (eg. by calling {@code HttpServletRequest.getReader()} or
   *     reading the stream from {@code HttpServletRequest.getInputStream()}).
   */
  static Error parseError(HttpServletRequest request) throws IOException {
    try {
      ErrorBuilder builder = new ErrorBuilder();
      MimeMultipart multipart = parseMultipartRequest(request);

      int parts = multipart.getCount();
      for (int i = 0; i < parts; i++) {
        BodyPart part = multipart.getBodyPart(i);
        String fieldName = getFieldName(part);
        if ("from".equals(fieldName)) {
          builder.withFromJid(new JID(getTextContent(part)));
        } else if ("stanza".equals(fieldName)) {
          builder.withStanza(getTextContent(part));
        }
      }

      return builder.build();
    } catch (MessagingException ex) {
      IOException ex2 = new IOException("Could not parse incoming request.");
      ex2.initCause(ex);
      throw ex2;
    }
  }
}
