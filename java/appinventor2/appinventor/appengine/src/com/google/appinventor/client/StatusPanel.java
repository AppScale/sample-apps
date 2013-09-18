// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.client;

import static com.google.appinventor.client.Ode.MESSAGES;
import com.google.appinventor.common.version.GitBuildId;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;


/**
 * The status panel contains various links.
 *
 */
public class StatusPanel extends Composite {
  private String AppInventorFooter =
    "<a href='" + Ode.APP_INVENTOR_DOCS_URL + "/about/termsofservice.html'" +
    " target=_blank>" + MESSAGES.privacyTermsLink() + "</a>";

  /**
   * Initializes and assembles all UI elements shown in the status panel.
   */
  public StatusPanel() {
    HorizontalPanel hpanel = new HorizontalPanel();
    hpanel.setWidth("100%");
    hpanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
    hpanel.add(new HTML(AppInventorFooter));

    // This shows the git version and the date of the build
    String version = GitBuildId.getVersion();
    String date = GitBuildId.getDate();
    if (version != null && date != null) {
      Label buildId = new Label(MESSAGES.gitBuildId(date, version));
      hpanel.add(buildId);
      hpanel.setCellHorizontalAlignment(buildId, HorizontalPanel.ALIGN_RIGHT);
    }

    initWidget(hpanel);
    setStyleName("ode-StatusPanel");
  }
}
