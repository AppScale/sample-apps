#!/usr/bin/env python
#
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import cgi
import wsgiref.handlers
import logging
from google.appengine.ext import db
from google.appengine.api import users
from google.appengine.ext import webapp
import simplejson as json

class Counter(db.Model):
  count = db.IntegerProperty(required=True, default=0)

class CreateCounter(webapp.RequestHandler):
  def post(self):
    counter_key = self.request.get('name')
    a = Counter(key_name=counter_key, count=0)
    try:
      db.put(a)
      json_response = {"success":True, "reason": "you are awesome"}
      self.response.out.write(json.dumps(json_response))

    except Exception, e: 
      json_response = {"success":False, "reason": str(e)}
      self.response.out.write(json.dumps(json_response))

  def get(self):
    counter_key = self.request.get('name')
    a = Counter(key_name=counter_key, count=0)
    try:
      db.put(a)
      json_response = {"success":True, "reason": str("you are awesome")}
      self.response.out.write(json.dumps(json_response))

    except Exception, e: 
      json_response = {"success":False, "reason": str(e)}
      self.response.out.write(json.dumps(json_response))


class GetCounter(webapp.RequestHandler):
  def post(self):
    counter_key = self.request.get('name')
    try:
      a = Counter.get_by_key_name(counter_key)
      json_response = {"success":True, "count": a.count}
      self.response.out.write(json.dumps(json_response))
    except Exception, e: 
      json_response = {"success":False, "reason": str(e)}
      self.response.out.write(json.dumps(json_response))
  def get(self):
    counter_key = self.request.get('name')
    try:
      a = Counter.get_by_key_name(counter_key)
      json_response = {"success":True, "count": a.count}
      self.response.out.write(json.dumps(json_response))
    except Exception, e: 
      json_response = {"success":False, "reason": str(e)}
      self.response.out.write(json.dumps(json_response))


class DeleteCounter(webapp.RequestHandler):
  def post(self):
    key = self.request.get('name')
    try:
      a = Counter.get_by_key_name(key)
      db.delete(a)
      json_response = {"success":True}
      self.response.out.write(json.dumps(json_response))
    except Exception, e: 
      json_response = {"success":False, "reason": str(e)}
      self.response.out.write(json.dumps(json_response))
  def get(self):
    key = self.request.get('name')
    try:
      a = Counter.get_by_key_name(key)
      db.delete(a)
      json_response = {"success":True}
      self.response.out.write(json.dumps(json_response))
    except Exception, e: 
      json_response = {"success":False, "reason": str(e)}
      self.response.out.write(json.dumps(json_response))


class Increment(webapp.RequestHandler):
  def post(self):
    throw_exception = self.request.get("throw_exception")
    if throw_exception == "true":
      throw_exception = True
    else:
      throw_exception = False

    def increment(name, amount, throw_exception):
      counter = Counter.get_by_key_name(name)
      counter.count = counter.count + amount  
      db.put(counter)
      if throw_exception:
        raise ValueError("Forced exception")
      return True

    name = self.request.get('name')
    amount = int(self.request.get('amount'))
    reason = ""
    try:
      db.run_in_transaction(increment, name, amount, throw_exception)
      success = True
    except Exception, e:
      success = False
      reason = str(e)
    json_response = {"success":success, "reason":reason}
    self.response.out.write(json.dumps(json_response))

  def get(self):
    throw_exception = self.request.get("throw_exception")
    if throw_exception == "true":
      throw_exception = True
    else:
      throw_exception = False

    def increment(name, amount, throw_exception):
      counter = Counter.get_by_key_name(name)
      counter.count = counter.count + amount  
      db.put(counter)
      if throw_exception:
        raise ValueError("Forced exception")
      return True

    name = self.request.get('name')
    amount = int(self.request.get('amount'))
    reason = ""
    try:
      db.run_in_transaction(increment, name, amount, throw_exception)
      success = True
    except Exception, e:
      success = False
      reason = str(e)
    json_response = {"success":success, "reason":reason}
    self.response.out.write(json.dumps(json_response))


class MainIndex(webapp.RequestHandler):
  def get(self):
    self.response.out.write("Hello from REST Counter App</br>")
    self.response.out.write("Paths include: </br>/createcounter w/ name</br>\n")
    self.response.out.write("/increment w/ name and amount</br>\n")
    self.response.out.write("/deletecounter w/ name</br>\n")
    self.response.out.write("/getcounter w/ name</br>\n")

application = webapp.WSGIApplication([
  ('/', MainIndex),
  ('/createcounter', CreateCounter),
  ('/increment', Increment),
  ('/deletecounter', DeleteCounter),
  ('/getcounter', GetCounter)
], debug=True)


def main():
  wsgiref.handlers.CGIHandler().run(application)


if __name__ == '__main__':
  main()
