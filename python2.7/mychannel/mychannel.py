#!/usr/bin/python2.4
#
# Copyright 2010 Google Inc. All Rights Reserved.

# pylint: disable-msg=C6310

"""Channel Tic Tac Toe

This module demonstrates the App Engine Channel API by implementing a
simple tic-tac-toe game.
"""

import datetime
import logging
import os
import random
import re
import webapp2

from google.appengine.api import channel
from google.appengine.api import users
from google.appengine.ext import db
from google.appengine.ext.webapp import template
import hashlib

receiverid = "receiverappid" 
senderid = "senderappid"

def randomString(length):
  s = hashlib.sha256()
  ret = "a"
  while len(ret) < length:
    s.update(str(random.random()))
    ret += s.hexdigest()
  return ret[0:length]


class MainPage(webapp2.RequestHandler):
  """The main UI page, renders the 'index.html' template."""

  def get(self):
    """Renders the main page. When this page is shown, we create a new
    channel to push asynchronous updates to the client."""
    path = os.path.join(os.path.dirname(__file__), 'index.html')
    template_values = {'senderid': senderid, 'receiverid': receiverid}
    self.response.out.write(template.render(path, template_values))

class CreateChannel(webapp2.RequestHandler):
  def get(self):
    randomness = randomString(5)
    token = channel.create_channel(randomness)
    self.response.out.write('Appid created: ' + randomness)
    self.response.out.write('<div></div>')
    self.response.out.write('Token created: ' + token)
    self.response.out.write('<div></div>')
    self.response.out.write('<div style="float:left;"><a href="/">Home</a></div>')

class SendMessage(webapp2.RequestHandler):
  def get(self):
    token = channel.create_channel("senderappid")
    template_values = {'token': token,
                      'appid': 'receiverappid',
                      'message': "Hello World! - The Channel API is working!"}

    path = os.path.join(os.path.dirname(__file__), 'sendMessage.html')
    self.response.out.write(template.render(path, template_values))
    #self.response.out.write('AppID used to send: ' + randomness)
    #self.response.out.write('Token used to send: ' + token)

class ReceiveMessage(webapp2.RequestHandler):
  def get(self):
    token = channel.create_channel("receiverappid")
    template_values = {'token': token,
                      'appid': 'receiverappid'}

    path = os.path.join(os.path.dirname(__file__), 'receiveMessage.html')
    self.response.out.write(template.render(path, template_values))
    #self.response.out.write('AppID used to send: ' + randomness)
    #self.response.out.write('Token used to send: ' + token)


class PostMessage(webapp2.RequestHandler):
  def post(self):
    message = "Hello World! - The Channel API is working!"
    receiverid = "receiverappid"
    channel.send_message(receiverid, message)
    self.response.out.write("message sent")

app = webapp2.WSGIApplication([
    ('/', MainPage),
    ('/createchannel', CreateChannel),
    ('/sendmessage', SendMessage),
    ('/postmessage', PostMessage),
    ('/receivemessage', ReceiveMessage)], debug=True)
