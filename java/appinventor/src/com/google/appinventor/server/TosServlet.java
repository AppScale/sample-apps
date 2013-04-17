// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.server;

import com.google.appinventor.server.flags.Flag;
import com.google.appinventor.server.storage.StorageIo;
import com.google.appinventor.server.storage.StorageIoInstanceHolder;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to deal with displaying the Terms of Service and making sure that is
 * displayed just once for any user
 *
 * TODO(user): do we still need this capability?
 *
 */
public class TosServlet extends OdeServlet {
  // Logging support
  private static final Logger LOG = Logger.getLogger(TosServlet.class.getName());

  private final StorageIo storageIo = StorageIoInstanceHolder.INSTANCE;
  private static final long serialVersionUID = 8099788377554316289L;

  // An URL to redirect requests to the first time they access App Inventor.
  // TODO(user): need to figure out what this URL will be and update the comment in doPost.
  private static final Flag<String> initialRedirectionUrl =
      Flag.createFlag("initial.redirection.url", "/");

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

    storageIo.setTosAccepted(LocalUser.getInstance().getUserId());

    // Redirect the user to the initialRedirectionUrl (the 'About' page, by default).
    resp.sendRedirect(initialRedirectionUrl.get());
  }
}
