// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.appengine.api.images;

import java.util.Map;

import com.google.appengine.api.images.ImagesServicePb.ImagesCompositeRequest;
import com.google.appengine.api.images.ImagesServicePb.CompositeImageOptions;

/**
 * Implementation of Composite using alpha blending.
 *
 */
final class CompositeImpl extends Composite {

  private final Image image;
  private final Anchor anchor;
  private final int xOffset;
  private final int yOffset;
  private final float opacity;

  /**
   * Creates a compositing operation with the supplied parameters.
   * @param image Image to be placed onto the canvas.
   * @param xOffset X offset from anchor position.
   * @param yOffset X offset from anchor position.
   * @param opacity Opacity of the image as a float in range [0.0, 1.0].
   * @param anchor Anchor position of the image on the canvas.
   */
  CompositeImpl(Image image, int xOffset, int yOffset, float opacity,
                Anchor anchor) {
    if (image == null) {
      throw new IllegalArgumentException("The image must not be null");
    }
    if (xOffset > ImagesService.MAX_RESIZE_DIMENSIONS
        || xOffset < -ImagesService.MAX_RESIZE_DIMENSIONS
        || yOffset > ImagesService.MAX_RESIZE_DIMENSIONS
        || yOffset < -ImagesService.MAX_RESIZE_DIMENSIONS) {
      throw new IllegalArgumentException("Images must fit on the canvas");
    }
    if (opacity < 0.0f || opacity > 1.0f) {
      throw new IllegalArgumentException("Opacity must be in range [0, 1]");
    }
    if (anchor == null) {
      throw new IllegalArgumentException("Anchor must not be null");
    }
    this.image = image;
    this.anchor = anchor;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
    this.opacity = opacity;
  }

  /** {@inheritDoc} */
  @Override
  void apply(ImagesCompositeRequest.Builder request, Map<Image, Integer> imageIndexMap) {
    if (!imageIndexMap.containsKey(image)) {
      imageIndexMap.put(image, request.build().getImageCount());
      request.addImage(ImagesServiceImpl.convertImageData(image));
    }
    CompositeImageOptions.Builder options = CompositeImageOptions.newBuilder();
    int sourceId = imageIndexMap.get(image);
    options.setSourceIndex(sourceId);
    options.setXOffset(xOffset);
    options.setYOffset(yOffset);
    options.setOpacity(opacity);
    options.setAnchor(convertAnchor(anchor));
    request.addOptions(options);
  }

  static CompositeImageOptions.ANCHOR convertAnchor(Anchor anchor) {
    switch (anchor) {
      case TOP_LEFT:
        return CompositeImageOptions.ANCHOR.TOP_LEFT;
      case TOP_CENTER:
        return CompositeImageOptions.ANCHOR.TOP;
      case TOP_RIGHT:
        return CompositeImageOptions.ANCHOR.TOP_RIGHT;
      case CENTER_LEFT:
        return CompositeImageOptions.ANCHOR.LEFT;
      case CENTER_CENTER:
        return CompositeImageOptions.ANCHOR.CENTER;
      case CENTER_RIGHT:
        return CompositeImageOptions.ANCHOR.RIGHT;
      case BOTTOM_LEFT:
        return CompositeImageOptions.ANCHOR.BOTTOM_LEFT;
      case BOTTOM_CENTER:
        return CompositeImageOptions.ANCHOR.BOTTOM;
      case BOTTOM_RIGHT:
        return CompositeImageOptions.ANCHOR.BOTTOM_RIGHT;
    }
    throw new IllegalArgumentException("Unexpected anchor value: " + anchor);
  }
}
