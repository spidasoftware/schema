CalcDB API
===========

CalcDB is a web application that stores the complex pole data described by they SPIDACalc Project JSON schema. The aim of CalcDB is to provide a simple and robust API for storing and accessing pole data that can be used in a wide variety of integration and reporting scenarios. 

# General Concepts

1. CalcDB primarily deals with data coming from SPIDACalc Project format. The JSON schema can be found [here.](https://github.com/spidasoftware/schema/blob/master/resources/v1/schema/spidacalc/calc/project.schema) This describes the overall scope of the data that can be stored in CalcDB.
2. CalcDB deals with this data on three different levels: Projects, Locations, and Designs. Just like in SPIDACalc, a Project can have many Locations, and a Location can have many Designs. These objects correspond exactly with the project components in SPIDACalc. 
3. We can deal with this data in slightly different formats. The most common way to save data would be to save a whole Project. But after the project is saved in CalcDB, the Project, Locations and Designs that it contains can be queried individually. CalcDB uses different formats to differentiate a Project that includes all of it's Locations and Designs from a project that only includes the basic project data.

## REST

The thing about REST is that it's really really simple. You can get a basic primer at the following two sites:
http://developer.infoconnect.com/get-vs-post-%E2%80%93-restful-primer
http://rest.elkstein.org/

### HTTP METHODS

The basic idea is that http verbs are used to indicate what the client wants to do. Here's a table showing the http request types and descriptions of what they're used for. Using these different types of http calls allows a client to easily Create, Read, Update, and Delete (CRUD).

| Method | Purpose                                                 | URL
|--------|---------------------------------------------------------|---------------------
| POST   | Saves a new resource (Project, Location, or Design)     | <base-url>/<resource-type>
| PUT    | Updates an existing resource                            | <base-url>/<resource-type>/<id>
| GET    | Retrieves a list of resources. Can never modify data.   | <base-url>/<resource-type>
| GET    | Show a single resource. Can never modify data.          | <base-url>/<resource-type>/<id>
| DELETE | You guessed it.                                         | <base-url>/<resource-type>/<id>


CalcDB deals with three main types of data, projects, locations and designs. These correspond to the same objects in a SPIDACalc project. The API has an endpoint (resource-type) for each type of component. For example, let's say we have CalcDB running at the following base url: "https://www.example.com/calcdb". In order to deal with projects, we will simply add "/projects" to the base url, resulting in: "https://www.example.com/calcdb/projects". If we instead with to work with Locations or Designs, then we would simply replace that url segment with... surprise, "/locations" or "/designs".

Notice how there are two rows for GET requests. This is because one is for requesting a list of projects, and the other is for showing a specific project by id. Any time we want to deal with a single specific resource, we always put the resource's id in the url. See the examples below for specifics.

### Authentication

REST stands for REpresentational State Transfer. This essentially means that the server is Stateless, and the request is expected to contain all of the information that the server needs in order to complete the request. This means that every request to CalcDB must contain an apiToken parameter, or else the server won't know which user is making the request. For all the requests in this example, we'll use the api token, "abc123", but each users actual api token will be a unique alpha-numeric value that is somewhat longer.

# Examples

These examples will all use the command-line tool curl, just because of it's ubiquity. There are plenty of other good tools out there for testing, though, such as the Postman Chrome extension. If you need a curl reference to help follow along, check out [the man page](http://curl.haxx.se/docs/manpage.html)

## Saving a project

Let's start off by saving a project, by sending a POST request to "<base url>/projects". We will need two parameters. The first is the api token, which is how CalcDB authenticates the user. The second is the project json. The api token parameter can be sent either in the url or the body, but the project json must be sent in the body, and the parameter name must be 'project'. 

For this example, we'll save the included example project at: v1/examples/spidacalc/projects/full_project.json

To send the request:
`curl -X POST --data apiToken=abc123 \
 --data-urlencode "project@v1/examples/spidacalc/projects/full_project.json" \
 https://www.example.com/calcdb/projects`

The response from CalcDB (formatted for readability):
	{
		"status":"ok",
		"project":"53e13203e4b07e53be02f130",
		"locations":[
			"53e13203e4b07e53be02f129",
			"53e13203e4b07e53be02f12b",
			"53e13203e4b07e53be02f12d",
			"53e13203e4b07e53be02f12f"
		],
		"designs":[
			"53e13203e4b07e53be02f128",
			"53e13203e4b07e53be02f12a",
			"53e13203e4b07e53be02f12c",
			"53e13203e4b07e53be02f12e"
		]
	}

The response 'status' just tells us that everything went ok. If there is ever a problem processing a request, the status will be "error". The 'project' field shows us the calcdb id for the newly saved project. The locations and designs fields are simply arrays of the ids for those objects. 

## Show the project

Now that we've saved a project, let's read it out again. Any time we want to GET data from calcdb, we send a GET request (this should be easy to remember). Since we know the id of the project we want, we can simply request it by adding the id as another segment to the url:
"http://www.example.com/calcdb/projects/53e13203e4b07e53be02f130"

Send the request:
`curl http://www.example.com/calcdb/projects/53e13203e4b07e53be02f130?apiToken=abc123`

The response should be the same project that you just saved.

## Update the Project

If we want to update an existing project instead of saving a new one, we issue a PUT request instead of a POST. Since we're doing this to a specific project, we also put the project's id in the url.

Send the request:
`curl -X PUT --data apiToken=abc123 \
 --data-urlencode "project@v1/examples/spidacalc/projects/modified_project.json" \
 https://www.example.com/calcdb/projects/53e13203e4b07e53be02f130`
 
The response will be essentially the save as for a POST (save) request. 

## Delete the project

This is an easy one.
Send the request:
`curl -X DELETE https://www.example.com/calcdb/projects/53e13203e4b07e53be02f130?apiToken=abc123`

The response: `{"status": "ok"}` 

## List multiple resources

If we want to return multiple projects, we can do that with GET requests as well. Just send a GET to "<base url>/projects". By default, this will simply return the most recent project that was saved. You can narrow the search by adding various query parameters, too. For instance, if you want to list projects with a specific name, just make a GET to "<base url>/projects?label=MyProject&apiToken=abc123". When you list resources (GET, but not for a specific resource), CalcDB will always return an Array of resources. If no resources match your query, the array will just be empty. CalcDB always orders results by the date last modified, returning the most recent results first.

So what if your query returns many resources? You won't want to return a thousand designs with a single request because it would take forever. This is why the API allows for special 'projection' parameters. There are two of them, and they are 'skip' and 'limit'. "Limit" will simply limit the total number of results returned to the specified size. "Skip" will first skip that number of resources in the results set. So, by combining these two, you can paginate query results. Let's say we have 1000 Locations that have the label, "Pole1". We can request locations 0 - 25 just by sending a GET to: "<base url>/locations?limit=25&label=Pole1&apiToken=abc123". To get locations 26-50, just add the parameter, "skip=25". 

# REST API Descriptor

The available fields that can be added to requests as parameters are listed in [restAPI.json](https://github.com/spidasoftware/schema/blob/master/resources/v1/schema/calcdb/interfaces/restAPI.json). For each item in "resources" (project, location, design, photo), there is an object for each type of API call: 'list', 'show', 'save', 'update', 'delete'. 

