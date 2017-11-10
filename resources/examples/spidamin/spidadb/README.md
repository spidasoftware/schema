SPIDAdb Examples
========================

There are two javascript files here that each contain an example of making an http request to the SPIDAdb api. The api is completely language-agnostic. These examples were written in javascript only because of it's ubiquity as a language. You will need [node.js](http://nodejs.org/) in order to run these examples.

### saveProject.js

This script will take one of the example SPIDACalc project json files and save it to SPIDAdb using a POST request. The response from this request has been written out to the file: 'exampleSaveResponse.txt' as an example.

### retrieveSavedProjects.js

This script will query SPIDAdb to retrieve the project saved by the first script. This script actually makes three requests. The first request is to retrieve the project. Once we have the project, it is parsed as JSON and we retrieve the 'id'. Then there is a second request to retrieve the locations from that project by using the query `projectId=<id>` (we limit the results to 1 just for the sake of brevity). The final request is for the designs (again, limit to 1). The results of all three of these queries have been written to the file: 'exampleListResponses.txt' as an example.

### List of Example responses

- exampleSaveResponse.txt - shows a response from saving a project
- exampleListResponses.txt - has a response for each type of resource
- exampleShowResponse.json - result of requesting a single project in referenced format