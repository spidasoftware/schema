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

var errorOut = function(msg) {
    console.log(msg);
    console.log("usage: saveProject.js [base-url] [apiToken]");
    process.exit(1);
};
var args = process.argv.slice(2);

if (args.length < 2) {
    errorOut("must supply both a base URL and an apiToken for for SPIDA DB")
}

var projectPath = path.join(__dirname, "../spidacalc/projects/full_project.json");
var projectText = fs.readFileSync(projectPath, {encoding: 'utf8'});

var baseUrl = args[0];
var opts = URL.parse(baseUrl);

var body = queryString.stringify({
    apiToken: args[1],
    project: projectText
});

opts.path += "/projects";
//opts.pathname += "/projects/exampleProject1";
opts.method = 'POST';
opts.headers = {
    'Content-Type': 'application/x-www-form-urlencoded',
    'Accept': 'application/json',
    'transfer-encoding': 'chunked',
    'Content-Length': body.length
};

var req = http.request(opts, function(response){
    if (response.statusCode !== 200) {
        console.log("Response Error: " + response.statusCode);
        errorOut("Response: " + JSON.stringify(response.headers, undefined, 2));
    }

    response.setEncoding('utf8');
    response.on('data', function(data){
        console.log(data);
    });
});
req.on('error', function(err){
    console.log("Error making http request: " + err.message)
});
req.write(body);
req.end();









