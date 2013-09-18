/**
 *  * User: Edwin Zhang (ehzhang@mit.edu)
 */

var fs = require('fs'); //Always required to read from files
var path = fs.absolute('.');


//Read files from filesystem
var expected = fs.read(path + '/blocklyeditor/tests/com/google/appinventor/blocklyeditor/data/makeQuiz/makeQuizExpected.yail');
var formJson = fs.read(path + '/blocklyeditor/tests/com/google/appinventor/blocklyeditor/data/makeQuiz/Screen1.scm');
formJson = formJson.substring(9, formJson.length-2);
var blocks = fs.read(path + '/blocklyeditor/tests/com/google/appinventor/blocklyeditor/data/makeQuiz/Screen1.bky');

// PhantomJS page object to open and load an URL
var page = require('webpage').create();
// Some debugging from PhantomJS
page.onConsoleMessage = function (msg) { console.log(msg); };
page.onError = function (msg, trace) {
  console.log(msg);
  trace.forEach(function(item) {
    console.log('  ', item.file, ':', item.line);
  })
}

// Open the actual page and load all the JavaScript in it
// if success is true, all went well
page.open('blocklyeditor/src/demos/yail/yail_testing_index.html', function(status) {
  // The evaluate function has arguments passed after the callback
  // in this case, we are passing in the yail files.

  if (status != 'success') {
    console.log('load unsuccessful');
  }

  // Evaluate the following:
  var passed = page.evaluate(function(){

    // Get the expected Yail from Classic
    var expected = arguments[0];

    // Functions in yail_testing_index.html
    processForm(arguments[1]);
    processBlocks(arguments[2]);

    var newblocks = toAppYail();

    return doTheyMatch(expected, newblocks);


  }, expected, formJson, blocks);

  //This is the actual result of the test
  console.log(passed);
  //Exit the phantom process
  phantom.exit();
});


