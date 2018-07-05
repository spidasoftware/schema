SPIDAdb API
===========

SPIDAdb is a web application that stores the complex pole data described by the SPIDACalc Project JSON schema. The aim of SPIDAdb is to provide a simple and robust API for storing and accessing pole data that can be used in a wide variety of integration and reporting scenarios.

# General Concepts

1. You will need to have a basic understanding of how to make HTTP requests. If you are unfamiliar with how to send an HTTP request, you should familiarize yourself before continuing with this document. HTTP requests can be made using any programming language, tool, or platform, so there are plenty of options.
2. SPIDAdb primarily deals with data coming from SPIDACalc Project format. The JSON schema can be found [here.](https://github.com/spidasoftware/schema/blob/master/resources/schema/spidacalc/calc/project.schema) This describes the overall scope of the data that can be stored in SPIDAdb.
3. SPIDAdb deals with this data on three different levels: Projects, Locations, and Designs. Just like in SPIDACalc, a Project can have many Locations, and a Location can have many Designs. These objects correspond exactly with the project components in SPIDACalc.
4. Only Projects can be directly created, updated, or deleted. Locations, Designs, and Photos are effectively read-only. They can only be deleted or modified by deleting or updating thier parent project.

## REST

The thing about REST is that it's really really simple. You can get a basic primer at the following two sites:

- http://developer.infoconnect.com/get-vs-post-%E2%80%93-restful-primer
- http://rest.elkstein.org/

#### HTTP METHODS

The basic idea is that http verbs are used to indicate what the client wants to do. Here's a table showing the http request types and descriptions of what they're used for. Using these different types of http calls allows a client to Create, Read, Update, and Delete (CRUD). Notice how there are two rows for GET requests. This is because one is for requesting a list of projects, and the other is for showing a specific project by id. Any time we want to deal with a single specific resource, we always put the resource's id in the url. See the examples below for specifics.

| Method | Purpose                                                 | URL
|--------|---------------------------------------------------------|---------------------
| POST   | _Save_ a new Project (projects only)                    | `https://${HOST}/spidadb/projects.<format>`
| PUT    | _Update_ an existing Project (projects only)            | `https://${HOST}/spidadb/projects/<id>.<format>`
| GET    | _Show_ a single resource. Can never modify data.        | `https://${HOST}/spidadb/<resource-type>/<id>.<format>`
| GET    | _List_ resources matching a query. Cannot modify data.  | `https://${HOST}/spidadb/<resource-type>.<format>`
| DELETE | _Delete_ It'll be gone forever, so you better be sure.  | `https://${HOST}/spidadb/<resource-type>/<id>`

- `<base-url>`      = The url to SPIDAdb. i.e.- `https://www.spidamin.com/SPIDAdb`
- `<resource-type>` = One of: "projects", "locations", "designs"
- `<id>`            = The unique SPIDAdb id of a specific resource
- `<format>`        = The format of the data you are sending or requesting. This is not required.

#### Resource Types
SPIDAdb deals with three main types of data, projects, locations and designs. These correspond to the same objects in a SPIDACalc project. The API has an endpoint (resource-type) for each type of component. For example, let's say we have SPIDAdb running at the following base url: `https://www.example.com/SPIDAdb`. In order to deal with projects, we will simply add `/projects` to the base url, resulting in: `https://www.example.com/SPIDAdb/projects`. If we instead with to work with Locations or Designs, then we would simply replace that url segment with... surprise, "/locations" or "/designs".

#### id
Each resource in SPIDAdb has its own unique id. When requesting a resource, the id is just a part of the url. The URL `/projects/123` refers to the project with id '123'. Id's can be specified when creating a project, but every resource's id must be unique.

#### Format
Here's where things get interesting. Even though there is only one schema, there are several formats that the data can be in. When you save a project to SPIDAdb, it will typically contain several Locations, which in turn may contain several Designs. This is all sent to SPIDAdb via a POST or PUT request as a single JSON document. This is the default format, and it is called "__calc__" format. When you go to the project menu in SPIDACalc and export the project JSON, this is what you get: A single JSON document containing all the Locations and Designs in the Project. All requests will default to this format if none is specified.

This is not how SPIDAdb stores the data internally, however. The aim of SPIDAdb is to make it easy to separate individual Locations and Designs from their parent Project, so they are each stored separately. Then, each item will contain _references_ to it's parent and each of it's children. This is called "__referenced__" format. The .referenced format is read-only, meaning that a client cannot Save or Update using it, but it is very useful for querying the data.

Finally, there is a third format, called "__exchange__". This format corresponds to the Exchange File format [described here.](http://github.com/spidasoftware/schema/tree/master/resources/schema/spidacalc#calc-exchange-file-format) Using this format allows a client to easily include the project JSON and all the photos in a single file upload. Currently, the .exchange format is ONLY supported for Save and Update requests for projects.

Formats are specified using a .suffix attached to the url, similar to a file extension. Content-Types for responses from SPIDAdb will always be 'application/json', and will not indicate a specific format. For most List requests, the referenced format should be preferred. This is because data in this format is much smaller. If a request is made for Projects in .calc format, then all of the Locations and Designs will be returned along with each project. Projects can be _quite_ large, so SPIDAdb severely limits the number that can be returned in .calc format. Using .referenced format makes the project (or Location) a tiny fraction of it's full size and makes it easy to return large lists of them.

#### Authentication

One last thing before we get to the good stuff, and that's the matter of authentication. REST stands for REpresentational State Transfer. This essentially means that the server is Stateless, and the request is expected to contain all of the information that the server needs in order to complete the request. This means that every request to SPIDAdb must contain an apiToken parameter, or else the server won't know which user is making the request. SPIDAdb ignores all Cookies and other session information sent with a request. For all the requests in this example, we'll use the api token, "abc123", but each users actual api token will be a unique alpha-numeric value that is somewhat longer.


# Examples

These examples will all use the command-line tool curl, just because of it's ubiquity. There are plenty of other good tools out there for testing, though, such as the Postman Chrome extension. If you need a curl reference to help follow along, check out [the curl man page](http://curl.haxx.se/docs/manpage.html)

## Saving a project - POST - `<baseURL>/<resource-type>`

Let's start off by saving a project, by sending a POST request to `<base url>/projects`. We will need two parameters. The first is the api token, which is how SPIDAdb authenticates the user. The second is the project json. The api token parameter can be sent either in the url or the body, but the project json must be sent in the body, and the parameter name must be 'project'.

For this example, we'll save the included example project at: v1/examples/spidacalc/projects/full_project.json

To send the request:

`curl -X POST --data apiToken=abc123 \
 --data-urlencode "project@v1/examples/spidacalc/projects/full_project.json" \
 https://www.example.com/spidadb/projects`

The response from SPIDAdb (formatted for readability):

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

The response 'status' just tells us that everything went ok. If there is ever a problem processing a request, the status will be "error". The 'project' field shows us the SPIDAdb id for the newly saved project. The locations and designs fields are simply arrays of the ids for those objects.

## Update the Project - PUT - `<baseURL>/projects/<id>`

If we want to update an existing project instead of saving a new one, we issue a PUT request instead of a POST. Since we're doing this to a specific project, we put the project's id in the url.

Send the request:

	curl -X PUT --data apiToken=abc123 \
	 --data-urlencode "project@v1/examples/spidacalc/projects/modified_project.json" \
	 https://www.example.com/spidadb/projects/53e13203e4b07e53be02f130

The response will be essentially the save as for a POST (save) request.

## Show the project - GET - `<baseURL>/<resource-type>/<id>`

Now that we've saved a project, let's read it out again. Any time we want to GET data from SPIDAdb, we send a GET request (this should be easy to remember). Since we know the id of the project we want, we can simply request it by adding the id as another segment to the url:
"http://www.example.com/spidadb/projects/53e13203e4b07e53be02f130"

Send the request:

`curl http://www.example.com/spidadb/projects/53e13203e4b07e53be02f130?apiToken=abc123`

The response (truncated):

    {
        "status": "ok",
        "project": {
            "label": "full_project",
            //...
        }
    }

## List multiple resources - GET - `<baseURL>/<resource-type>`

If we want to return multiple projects (or Locations or Designs), we can do that with GET requests as well. Just send a GET to "<base url>/projects". By default, this will simply return the most recent project that was saved. You can narrow the search by adding various query parameters, too. For instance, if you want to list projects with a specific name, just make a GET to "<base url>/projects?label=MyProject&apiToken=abc123". When you list resources (GET, but not for a specific resource), SPIDAdb will always return an Array of resources. If no resources match your query, the array will just be empty. SPIDAdb always orders results by the date last modified, returning the most recent results first.

So what if your query returns many resources? You won't want to return a thousand designs with a single request because it would take forever. This is why the API allows for special 'projection' parameters. There are two of them, and they are 'skip' and 'limit'. "Limit" will simply limit the total number of results returned to the specified size. "Skip" will first skip that number of resources in the results set. So, by combining these two, you can paginate query results. Let's say we have 1000 Locations that have the label, "Pole1". We can request locations 0 - 25 just by sending a GET to: "<base url>/locations?limit=25&label=Pole1&apiToken=abc123". To get locations 26-50, just add the parameter, "skip=25". Also, remember that most clients will want to use the .referenced format for these requests, which will allow dealing with larger result sets.

Send the request (this one lists up to 50 projects with the label, "MyProject"):

`curl https://www.example.com/spidadb/projects.referenced?label=MyProject&limit=50&apiToken=abc123`

The response (truncated):

    {
        "status": "ok",
        "projects": [
            {
                "label": "MyProject",
                //...
            },
            //...
        ]
    }

## Delete the project - DELETE - `<base-url>/projects/<id>`

This is an easy one.
Send the request:

`curl -X DELETE https://www.example.com/spidadb/projects/53e13203e4b07e53be02f130?apiToken=abc123`

The response:

`{"status": "ok"}`

## Photos

We've sort of avoided this topic until now because photos are a bit different than other resources. There is no way to directly save, update, or delete a photo. They are instead considered to be part of a Location for these purposes. However, when you request a Location, SPIDAdb does not send the photos along with it. Instead, if a location contains photos, each image will have a 'link' attribute that provides the id of that photo. You can then send a GET to "<base-url>/photos/<id>" to retrieve the photo.

 An example of an image 'link' (the rest of the location is not shown):

    //in a Location json
    "images": [
        {
            "url": "photo123.jpg",
            "link": {
                "source": "filefortAssetService",
                "id": "2af54d11"
            }
        }
    ]

This shows an example of a location with one photo. The 'url' property will typically just be a filename. This will be resolved when a project in imported into SPIDACalc, but SPIDAdb simply ignores it. The part that SPIDAdb is interested in is the 'link'. It contains the id for that photo.

To request the image from the example:

`curl https://www.example.com/spidadb/photos/2af54d11?apiToken=abc123`

The response:

    {
        "status": "ok",
        "photo": "<base-64 encoded string>..."
    }

The response will include the photo bytes as a base64 encoded string in the body of the JSON response. This would then be decoded by the client and written to a file. There is currently no support for any other request type or options for photos.

## Detailed Results

Starting with the version 6 schema detailed results will be pushed from SPIDAcalc to SPIDAdb.  The detailed results are very large json objects so SPIDAdb will not send the detailed results nested in the objects returned from the API.  Instead a resultId will be returned, you will have to make an extra request to retrieve the detailed results.

An example of a design with a resultId:

    {
    	"analysis": [
		{
        		"resultId": "588a58e17d84ad3bd41c4562"
		}
	]
    }
    
To retrieve the results for 588a58e17d84ad3bd41c4562:

`curl https://www.example.com/spidadb/results/588a58e17d84ad3bd41c4562?apiToken=abc123`

# The Finer Points

#### Saving or Updating Projects

There are several possible ways to save/update a project.

- **Plain old Project JSON**  This is what we did in the example. This would normally just be sent with a ContentType of "multipart/form-data", but "application/X-www-form-urlencoded" is also acceptable (just less efficient). No photos are saved, because none are sent. The request would be sent to `<base-url>/projects.calc` but the `.calc` format is optional since that is the default.

- **Project JSON and Photos**  This is essentially the same as above, except that all the photos for the project must be sent with the request. This MUST be a 'multipart' request. Each photo file should be added to the request body as it's own 'part', and the 'name' of the part must match the photo name given in the 'url' property of the image. For example, given the image: `{"url":"myPhoto4.jpg"}`, the multipart request would need to have a part with it's name as "myPhoto4.jpg" and the ContentType as "image/jpeg".

- **Project JSON and Detailed Results**  This is essentially the same as above, except that all the results for the project must be sent with the request. This MUST be a 'multipart' request.  Each result should be added to the request as its own 'part' with a Content-Type of "text/plain" and the 'name' of the parameter must match the results id given in the 'resultId' property of the analysis array. For example, given the result: `{"resultId":"588a58e17d84ad3bd41c4562"}`, the request would need to have a part with its name as "588a58e17d84ad3bd41c4562".

- **Upload an Exchange File**  Once you have an exchange file, it can simply be sent as part of a multipart POST request to `<base-url>/projects.exchange`. Notice the '.exchange' format - this is required if you're sending an exchange file. SPIDAdb will take care of unzipping the file and saving the project json and all the photos. This oftentimes tends to be the simplest way, since SPIDACalc can simply export projects directly to an exchange file.

- **Filefort uuid**  If you have the uuid of an exchange file in Filefort, then you can simply provide the uuid in the `filefortUuid` parameter. SPIDAdb will get the exchange file from filefort and use it to create the project.

#### PUT or POST

When creating a project, either one will do. A project does not need to already exist in order to PUT it. In fact, creating a project using a PUT is intended to allow the client to specify the id as it's created. Just about any string value is acceptable as an id, as long as it doesn't contain a '.' (period).

#### Deleting or Updating Locations or Designs

Locations and Designs are simply parts of a Project. The cannot be deleted or modified individually. Instead, update or delete the parent Project.

#### Notes on . and $
  The mongo database used by {calc,spida}db does not allow periods or dollar signs in document keys.  To work around this, both periods and dollar signs are transformed into their full-width versions.  So, . becomes \uff0e and $ becomes \uff04.  This is handled automatically on data being inserted into or retrieved from the mongoDataBaseService.  This should for the most part be transparent.  Except, when using a custom query, you must use the "escaped" key in the query.  Data returned from the query will be automatically unescaped, but parameters in the query itself must be escaped.  
