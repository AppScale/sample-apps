""" A client which makes requests to the performance testing application. """
import random
import sys
import urllib

# The different operations to perform against the performance application.
# The order matters (do puts before gets and deletes).
OPERATIONS = ["puts", "gets", "queries", "deletes"]

# The number of times operations are done. Results have the average of these.
NUMBER_OF_SAMPLES = 100

def get_random_string():
  """ Generates a random string.
  
  Returns:
    A str of random characters.
  """
  newstr = ""
  for _ in range(10):
    newstr += str(random.choice('abcdefghijklmnopqrstuvwxyz'))
  return newstr

def fetch_operation(server, port, operation, namespace, number):
  """ Fetches results of doing puts server side.

  Args:
    server: A str of the server we are fetching from.
    port: An int of the port we are fetching from.
    operation: A str of the operation to perform (puts, gets, etc).
    namespace: A str of t he namespace to perform puts on.
    number: An int of the number of the put operations to perform.
  Returns:
    A str of the result from the server.
  """
  data = urllib.urlencode({"namespace": namespace, "number": str(number)})
  url = "http://{0}:{1}/{2}".format(server, port, operation)
  return urllib.urlopen(url, data).read()

if __name__ == "__main__":
  if len(sys.argv) < 3:
    print "Example usage: python client.py 192.168.10.33 8080"
    exit(1) 

  for opcode in OPERATIONS:
    print "{0}: {1}".format(opcode, fetch_operation(sys.argv[1], 
      sys.argv[2], opcode, get_random_string(), NUMBER_OF_SAMPLES))

  exit(0)
