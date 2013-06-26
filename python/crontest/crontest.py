#!/usr/bin/evn python


# Google App Engine-specific imports
import webapp2
from google.appengine.ext import ndb


class LastUpdated(ndb.Model):
  """ This model holds a single field that indicates when it was created.
  
  We use this model with cron to make sure that cron isn't firing more often
  than it should be (see DetectMultipleCrons below).
  """
  update_time = ndb.DateTimeProperty(auto_now_add=True)


class DetectMultipleCrons(webapp2.RequestHandler):
  """ Queries LastUpdated models to see if cron scheduling is properly set up.
  """


  # The message to display to the user if we did see cron firing more often
  # than it should be.
  ERROR_MESSAGE = "BAD"


  # The message to display to the user if we did not see cron firing more often
  # than it should be.
  NO_ERROR_MESSAGE = "GOOD"

  
  # The number of seconds that need to separate each LastUpdated model's
  # creation time.
  UPDATE_WINDOW = 60

  
  def get(self):
    """ Searches through the Datastore to see if two LastUpdated models have
    been created in the last UPDATE_WINDOW seconds.
    
    As our cron.yaml indicates that one model should be created every
    UPDATE_WINDOW seconds, finding two within that time period is a sign that
    cron is misconfigured on the underlying cloud platform.
    """
    query = ndb.gql('SELECT * FROM LastUpdated ORDER BY update_time DESC')
    last_time_seen = None
    for entity in query:
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
  """ Generates a new LastUpdated model and stores it in the Datastore. """


  def get(self):
    """ Receives GET requests and creates a LastUpdated model accordingly. """
    LastUpdated().put()
    self.response.out.write("added a new entry!")


  def post(self):
    """ Receives POST requests and creates a LastUpdated model accordingly. """
    LastUpdated().put()
    self.response.out.write("added a new entry!")


app = webapp2.WSGIApplication([
  ('/', DetectMultipleCrons),
  ('/create', AddNewEntry)
], debug=True)
