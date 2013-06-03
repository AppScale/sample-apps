""" A performance testing framework for AppScale. """
from google.appengine.api import namespace_manager
from google.appengine.ext import db

import math
import time
import webapp2

try:
  import json
except ImportError:
  import simplejson as json

class TestEntity(db.Model):
  """ An entity model used for puts, get, deletes, and queries with dummy data. 
  """
  prop1 = db.StringProperty(required=True)
  prop2 = db.StringProperty(required=True)
  prop3 = db.DateProperty(auto_now=True)

def get_summation(points):
  """ Gets the summation over a set of points.
  
  Args:
    points: A list of floats.
  Returns:
    A float which is the summation over the set of points.
  """ 
  total = 0.0
  for point in points:
    total += point 
  return total 

def get_stdev(points):
  """ Gets the standard deviation of a set of float data points.
  
  Args:
    points: A list of floats.
  Returns:
    A float, the standard deviation of a given set of points.
  """
  summation = get_summation(points)
  avg = get_average(points)
  mean2 = avg * avg
  return math.sqrt((summation * summation / len(points)) - mean2) 

def get_average(points):
  """ Gets the average of a set of float data points.
  
  Args:
    points: A list of floats.
  Returns:
    The average of the list of floats.
  """
  return get_summation(points)/len(points) 

class Puts(webapp2.RequestHandler):
  """ Handlers for puts. """

  def __init__(self, request, response):
    """ Constructor.
    
    Args:
      request: The webapp2.Request object that contains information about the
        current web request.
      response: The webapp2.Response object that contains the response to be
        sent back to the browser.
    """
    self.initialize(request, response)

  def post(self):
    """ Handler for POST requests. 

    Request handler takes in the following arguments:
      namespace: The namespace to write to.
      number: The number of entities to write.
    """
    namespace = self.request.get("namespace") 
    namespace_manager.set_namespace(namespace)

    number = int(self.request.get("number"))
    timings = []
    for index in range(0, number):
      test_ent = TestEntity(id=index+1, prop1="test1", prop2="test2")
      start = time.time()
      test_ent.put()
      time_taken = time.time() - start
      timings.append(time_taken)
    response = {"avg": get_average(timings), "stdev": get_stdev(timings),
                "total": get_summation(timings)}
    self.response.out.write(json.dumps(response))

class Gets(webapp2.RequestHandler):
  """ Handlers for gets. """

  def __init__(self, request, response):
    """ Constructor.
    
    Args:
      request: The webapp2.Request object that contains information about the
        current web request.
      response: The webapp2.Response object that contains the response to be
        sent back to the browser.
    """
    self.initialize(request, response)

  def post(self):
    """ Handler for POST requests. 

    Request handler takes in the following arguments:
      namespace: The namespace to read to.
      number: The number of entities to read.
    """
    namespace = self.request.get("namespace") 
    namespace_manager.set_namespace(namespace)
    number = int(self.request.get("number"))
    timings = []
    for index in range(0, number):
      start = time.time()
      _ = TestEntity.get_by_id(index+1)
      time_taken = time.time() - start
      timings.append(time_taken)
    response = {"avg": get_average(timings), "stdev": get_stdev(timings),
                "total": get_summation(timings)}
    self.response.out.write(json.dumps(response))

class Deletes(webapp2.RequestHandler):
  """ Handlers for deletes. """

  def __init__(self, request, response):
    """ Constructor.
    
    Args:
      request: The webapp2.Request object that contains information about the
        current web request.
      response: The webapp2.Response object that contains the response to be
        sent back to the browser.
    """
    self.initialize(request, response)

  def post(self):
    """ Handler for POST requests. 

    Request handler takes in the following arguments:
      namespace: The namespace to delete to.
      number: The number of entities to delete.
    """
    namespace = self.request.get("namespace") 
    namespace_manager.set_namespace(namespace)
    number = int(self.request.get("number"))
    timings = []
    for index in range(0, number):
      test_ent_key = db.Key.from_path('TestEntity', index+1)
      start = time.time()
      db.delete(test_ent_key)
      time_taken = time.time() - start
      timings.append(time_taken)
    response = {"avg": get_average(timings), "stdev": get_stdev(timings),
                "total": get_summation(timings)}
    self.response.out.write(json.dumps(response))

class Queries(webapp2.RequestHandler):
  """ Handlers for queries. """

  def __init__(self, request, response):
    """ Constructor.
    
    Args:
      request: The webapp2.Request object that contains information about the
        current web request.
      response: The webapp2.Response object that contains the response to be
        sent back to the browser.
    """
    self.initialize(request, response)

  def post(self):
    """ Handler for POST requests. 

    Request handler takes in the following arguments:
      namespace: The namespace to query from.
      number: The maximum number of entities to fetch per query.
    """
    namespace = self.request.get("namespace") 
    namespace_manager.set_namespace(namespace)
    number = int(self.request.get("number"))
    timings = []
    for _ in range(0, number):
      start = time.time()
      TestEntity.all().fetch(number)
      time_taken = time.time() - start
      timings.append(time_taken)
    response = {"avg": get_average(timings), "stdev": get_stdev(timings),
                "total": get_summation(timings)}
    self.response.out.write(json.dumps(response))


application = webapp2.WSGIApplication([('/puts', Puts),
                                       ('/gets', Gets),
                                       ('/deletes', Deletes),
                                       ('/queries', Queries)], debug=True)
