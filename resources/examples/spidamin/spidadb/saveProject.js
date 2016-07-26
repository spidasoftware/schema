#!/usr/bin/env node

/*
This example node script will take a SPIDACalc example project and save it to SPIDA DB.
 This is just to give an idea of how to save a project using the api.
The project saved is located at: v1/examples/spidacalc/projects/full_project.json
The response will be printed to stdout.

This example requires node.js in order to run. The SPIDA DB api can be used with any language, though.
 */

var http = require('http');
var queryString = require('querystring');
var fs = require('fs');
var path = require('path');
var URL = require('url');

//just in case...
var errorOut = function(msg) {
    console.log(msg);
    console.log("usage: saveProject.js [base-url] [apiToken]");
    process.exit(1);
};

//first two arguments are always 'node' and the path to the script
var args = process.argv.slice(2);

//make sure we at least have the baseUrl and the apiToken
if (args.length < 2) {
    errorOut("must supply both a base URL and an apiToken for for SPIDA DB")
}

//the path to the project json file to be uploaded
var projectPath = path.join(__dirname, "../spidacalc/projects/full_project.json");

//the text of the project json to be saved to spida db
var projectText = fs.readFileSync(projectPath, {encoding: 'utf8'});

//the base url. i.e.- http://www.spidamin.com/calcdb
var baseUrl = args[0];
var opts = URL.parse(baseUrl);

//this will be the body of the POST request.
var body = queryString.stringify({
    apiToken: args[1],
    project: projectText
});

opts.path += "/projects";
opts.method = 'POST';
opts.headers = {
    //content-type header is required. if this were a file upload, as in the case of an exchange format, we
    //would use the multipart/form-data type instead
    'Content-Type': 'application/x-www-form-urlencoded',
    'Accept': 'application/json', //spida db isn't really particular about this
    'transfer-encoding': 'chunked',
    'Content-Length': body.length
};

var req = http.request(opts, function(response){
    if (response.statusCode !== 200) {
        //if the response status indicates an error, then print it to stdout
        console.log("Response Error: " + response.statusCode);
        errorOut("Response: " + JSON.stringify(response.headers, undefined, 2));
    }

    response.setEncoding('utf8'); //this is probably the default unless you're running windows, but just in case
    response.on('data', function(data){
        console.log(data); //once we receive the response body, print it to stdout
    });
});

//not that we're expecting any problems...
req.on('error', function(err){
    errorOut("Error making http request: " + err.message);
});

//write the request body to the stream
req.write(body);
req.end(); //make sure we close the stream and release any native resources
