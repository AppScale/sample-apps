import json
import httplib
import random
HOST = "128.111.55.206:8080"
expected_counter_value = 0
failures = 0
counter_name = "hi"
total_request = 10000
h1 = httplib.HTTPConnection(HOST)
response = h1.request("GET", "/createcounter?name=" + counter_name + "&amount=0")
response = h1.getresponse()
response = response.read() 
h1.close()

for ii in range(0,total_request):
  throw_exception = "false"
  if random.randint(0,100) > 50:
    throw_exception = "true"
 
  h1 = httplib.HTTPConnection(HOST)
  response = h1.request("GET", "/increment?name=" + counter_name + "&amount=1&throw_exception=" + throw_exception)
  response = h1.getresponse()
  response = response.read() 
  h1.close()
  response = json.loads(response) 
  if response['success'] == True:
    expected_counter_value += 1
  else:
    failures += 1

 
h1 = httplib.HTTPConnection(HOST)
response = h1.request("GET", "/getcounter?name=" + counter_name)
response = h1.getresponse()
response = json.loads(response.read())
h1.close()
print "Total request: " + str(total_request)
print "Counter value:" + str(response['count'])
counter = response['count']
print "Expected counter: " + str(expected_counter_value)
print "Failures: " + str(failures)
h1 = httplib.HTTPConnection(HOST)
response = h1.request("GET", "/deletecounter?name=" + counter_name)
response = h1.getresponse()
response = response.read()
h1.close()  

assert total_request == (failures + expected_counter_value)
assert total_request == (failures + counter)
assert counter == expected_counter_value
print "SUCCESS"
