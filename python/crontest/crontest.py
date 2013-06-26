#!/usr/bin/evn python


import webapp2


from google.appengine.ext import ndb


class LastUpdated(ndb.Model):
  update_time = ndb.DateTimeProperty(auto_now_add=True)


class DetectMultipleCrons(webapp2.RequestHandler):


  ERROR_MESSAGE = "BAD"


  NO_ERROR_MESSAGE = "GOOD"

  
  UPDATE_WINDOW = 8

  
  def get(self):
    query = ndb.gql('SELECT * FROM LastUpdated ORDER BY update_time DESC')
    last_time_seen = None
    for entity in query:
      self.response.out.write("last updated time is {0}<br />".format(entity.update_time))
      if last_time_seen is None:
        last_time_seen = entity.update_time
        continue

      if (last_time_seen - entity.update_time).seconds < self.UPDATE_WINDOW:
        self.response.out.write(self.ERROR_MESSAGE)
        return
      else:
        last_time_seen = entity.update_time

    self.response.out.write(self.NO_ERROR_MESSAGE)
    return


class AddNewEntry(webapp2.RequestHandler):


  def get(self):
    LastUpdated().put()
    self.response.out.write("added a new entry!")


app = webapp2.WSGIApplication([
  ('/', DetectMultipleCrons),
  ('/create', AddNewEntry)
], debug=True)
