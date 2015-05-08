#!/usr/bin/env node

/*
This example node script is intended to retrieve a project and its locations and designs from SPIDA DB.
It will retrieve the project in referenced format and write it to stdout. It will also make additional
requests to retrieve the locations and designs, again in referenced format.

This requires node.js be installed in order to run. This is just an example, though. The SPIDA DB api can be
used by any type of client written in any language.
 */

var http = require('http');
var queryString = require('querystring');

//not that we're expecting any problems...
var errorOut = function(msg) {
    console.log(msg);
    console.log("usage: retrieveSavedProjects.js [base-url] [apiToken] [project-name (optional)]");
    process.exit(1);
};

if (process.argv.length < 4) {
    errorOut("must supply a base-url and an apiToken")
}

//the 'label' of the project we wish to retrieve
var projectName = "full_project";

//the base url where spidadb is running
var baseUrl = process.argv[2];

//the apiToken used for authentication
var token = process.argv[3];

//if there's an extra arg, then it's for the project label
if (process.argv.length > 4){
    projectName = process.argv[4];
}

//to pretty-print a json string
var printJson = function(buffer){
    var json = JSON.parse(buffer);
    console.log(JSON.stringify(json, undefined, 4));
};

/*
Basic request function. just sends a GET request for the specified resource type and executes the
'doWithData' callback with the response body
 */
var requestResources = function(baseUrl, type, params, doWithData){
    var finalUrl = baseUrl + "/" + type + '.referenced?' + queryString.stringify(params);
    console.log("\n//Example response for GET request to /%s.referenced", type);
    console.log("//Each item in the %s array is a referenced %s", type, type.substr(0, type.length - 1));

    http.get(finalUrl, function(response){
        if (response.statusCode !== 200) {
            console.log("Response Error: " + response.statusCode);
            errorOut(JSON.stringify(response.headers, undefined, 4));
        }
        response.on('data', doWithData);

    }).on('error', function(err){
        errorOut(err.message);
    });

};

//First request the project. We pass in a callback function that will request the locations and designs
requestResources(baseUrl, 'projects', {apiToken: token, label: projectName, limit: 1}, function(data){
    printJson(data);

    //parse the response body as JSON
    var projects = JSON.parse(data).projects;
    if (projects.length == 0) {
        errorOut("No projects in response");
    }

    //get the project id from the response
    var projectId = projects[0].id;

    //now request the locations belonging to this project by using the query: projectId=<id>
    //notice here we set the 'limit=1' parameter. This is just to keep the amount of returned data
    //at a reasonable level.
    var query = {apiToken: token, projectId: projectId, limit: 1};
    requestResources(baseUrl, 'locations', query, function(locationData){
        printJson(locationData);

        //now request the designs using the same query and print the result
        requestResources(baseUrl, 'designs', query, function(data){
            printJson(data);
        });
    });
});


