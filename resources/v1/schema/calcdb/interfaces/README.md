CalcDB API
===========

CalcDB is a web application that stores the complex pole data described by they SPIDACalc Project JSON schema. The aim of CalcDB is to provide a simple and robust API for storing and accessing pole data that can be used in a wide variety of integration and reporting scenarios. 

# General Concepts

1. You will need to have a basic understanding of how to make HTTP requests. If you are unfamiliar with how to send an HTTP request, you should familiarize yourself before continuing with this document. HTTP requests can be made using any programming language, tool, or platform, so there are plenty of options.
2. CalcDB primarily deals with data coming from SPIDACalc Project format. The JSON schema can be found [here.](https://github.com/spidasoftware/schema/blob/master/resources/v1/schema/spidacalc/calc/project.schema) This describes the overall scope of the data that can be stored in CalcDB.
3. CalcDB deals with this data on three different levels: Projects, Locations, and Designs. Just like in SPIDACalc, a Project can have many Locations, and a Location can have many Designs. These objects correspond exactly with the project components in SPIDACalc. 
4. We can deal with this data in slightly different formats. The most common way to save data would be to save a whole Project. But after the project is saved in CalcDB, the Project, Locations and Designs that it contains can be queried individually. CalcDB uses different formats to differentiate a Project that includes all of it's Locations and Designs from a project that only includes the basic project data.

## REST

The thing about REST is that it's really really simple. You can get a basic primer at the following two sites:
http://developer.infoconnect.com/get-vs-post-%E2%80%93-restful-primer
http://rest.elkstein.org/

### HTTP METHODS

The basic idea is that http verbs are used to indicate what the client wants to do. Here's a table showing the http request types and descriptions of what they're used for. Using these different types of http calls allows a client to Create, Read, Update, and Delete (CRUD). Notice how there are two rows for GET requests. This is because one is for requesting a list of projects, and the other is for showing a specific project by id. Any time we want to deal with a single specific resource, we always put the resource's id in the url. See the examples below for specifics.

| Method | Purpose                                                 | URL
|--------|---------------------------------------------------------|---------------------
| POST   | _Save_ a new resource (Project, Location, or Design)    | <base-url>/<resource-type>.<format>
| PUT    | _Update_ an existing resource                           | <base-url>/<resource-type>/<id>.<format>
| GET    | _Show_ a single resource. Can never modify data.        | <base-url>/<resource-type>/<id>.<format>
| GET    | _List_ resources matching a query. Cannot modify data.  | <base-url>/<resource-type>.<format>
| DELETE | _Delete_ It'll be gone forever, so you better be sure.  | <base-url>/<resource-type>/<id>

<base-url>      = The url to CalcDB. i.e.- https://www.spidamin.com/calcdb
<resource-type> = One of: "projects", "locations", "designs"
<id>            = The unique calcdb id of a specific resource
<format>        = The format of the data you are sending or requesting. This is not required.

### Resource Types
CalcDB deals with three main types of data, projects, locations and designs. These correspond to the same objects in a SPIDACalc project. The API has an endpoint (resource-type) for each type of component. For example, let's say we have CalcDB running at the following base url: "https://www.example.com/calcdb". In order to deal with projects, we will simply add "/projects" to the base url, resulting in: "https://www.example.com/calcdb/projects". If we instead with to work with Locations or Designs, then we would simply replace that url segment with... surprise, "/locations" or "/designs".

### id
Id's can be more confusing than you'd expect, so here's where we set the story straight. CalcDB assigns a unique id to every project component it stores. That's one per each Project, Location, or Design. This is stored in a field called "\_id" (that's underscore-i-d). SPIDACalc also keeps it's own unique id for each component. This is represented in the field called "id" (no underscore). SPIDACalc ids are not used for anything in CalcDB, and there is no guarantee that they will be unique in CalcDB. The CalcDB \_id is the primary key in CalcDB, and IS unique. To make matters more confusing, you can actually search calcdb based on the Calc (no underscore) id and return a _list_ of matching resources. 

### Format
Here's where things get interesting. Even though there is only one schema, there are several formats that the data can be in. When you save a project to CalcDB, it will typically contain several Locations, which in turn may contain several Designs. This is all sent to CalcDB via a POST or PUT request as a single JSON document. This is the default format, and it is called "__calc__" format. When you go to the project menu in SPIDACalc and export the project JSON, this is what you get: A single JSON document containing all the Locations and Designs in the Project. All requests will default to this format if none is specified.

This is not how CalcDB stores the data internally, however. The aim of CalcDB is to make it easy to separate individual Locations and Designs from their parent Project, so they are each stored separately. Then, each item will contain _references_ to it's parent and each of it's children. This is called "__referenced__" format. The .referenced format is read-only, meaning that a client cannot Save or Update using it.

Finally, there is a third format, called "__exchange__". This format corresponds to the Exchange File format [described here.](http://github.com/spidasoftware/schema/tree/master/resources/v1/schema/spidacalc#calc-exchange-file-format) Using this format allows a client to easily include the project JSON and all the photos in a single file upload. Currently, the .exchange format is ONLY supported for Save and Update requests for projects.

For most List requests, the referenced format should be preferred. This is because data in this format is much smaller. If a request is made for Projects in .calc format, then all of the Locations and Designs will be returned along with each project. Projects can be _quite_ large, so CalcDB severely limits the number that can be returned in .calc format. Using .referenced format makes the project (or Location) a tiny fraction of it's full size and makes it easy to return large lists of them. 

### Authentication

One last thing before we get to the good stuff, and that's the matter of authentication. REST stands for REpresentational State Transfer. This essentially means that the server is Stateless, and the request is expected to contain all of the information that the server needs in order to complete the request. This means that every request to CalcDB must contain an apiToken parameter, or else the server won't know which user is making the request. CalcDB ignores all Cookies and other session infomation sent with a request. For all the requests in this example, we'll use the api token, "abc123", but each users actual api token will be a unique alpha-numeric value that is somewhat longer.


# Examples

These examples will all use the command-line tool curl, just because of it's ubiquity. There are plenty of other good tools out there for testing, though, such as the Postman Chrome extension. If you need a curl reference to help follow along, check out [the curl man page](http://curl.haxx.se/docs/manpage.html)

## Saving a project - POST - <baseURL>/<resource-type>

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

## Update the Project - PUT - <baseURL>/<resource-type>/<id>

If we want to update an existing project instead of saving a new one, we issue a PUT request instead of a POST. Since we're doing this to a specific project, we also put the project's id in the url.

Send the request:
`curl -X PUT --data apiToken=abc123 \
 --data-urlencode "project@v1/examples/spidacalc/projects/modified_project.json" \
 https://www.example.com/calcdb/projects/53e13203e4b07e53be02f130`
 
The response will be essentially the save as for a POST (save) request. 

## Show the project - GET - <baseURL>/<resource-type>/<id>

Now that we've saved a project, let's read it out again. Any time we want to GET data from calcdb, we send a GET request (this should be easy to remember). Since we know the id of the project we want, we can simply request it by adding the id as another segment to the url:
"http://www.example.com/calcdb/projects/53e13203e4b07e53be02f130"

Send the request:
`curl http://www.example.com/calcdb/projects/53e13203e4b07e53be02f130?apiToken=abc123`

The response (truncated):
    {
        "status": "ok",
        "project": {
            "label": "full_project",
            //...
        }
    }

## List multiple resources - GET - <baseURL>/<resource-type>

If we want to return multiple projects (or Locations or Designs), we can do that with GET requests as well. Just send a GET to "<base url>/projects". By default, this will simply return the most recent project that was saved. You can narrow the search by adding various query parameters, too. For instance, if you want to list projects with a specific name, just make a GET to "<base url>/projects?label=MyProject&apiToken=abc123". When you list resources (GET, but not for a specific resource), CalcDB will always return an Array of resources. If no resources match your query, the array will just be empty. CalcDB always orders results by the date last modified, returning the most recent results first.

So what if your query returns many resources? You won't want to return a thousand designs with a single request because it would take forever. This is why the API allows for special 'projection' parameters. There are two of them, and they are 'skip' and 'limit'. "Limit" will simply limit the total number of results returned to the specified size. "Skip" will first skip that number of resources in the results set. So, by combining these two, you can paginate query results. Let's say we have 1000 Locations that have the label, "Pole1". We can request locations 0 - 25 just by sending a GET to: "<base url>/locations?limit=25&label=Pole1&apiToken=abc123". To get locations 26-50, just add the parameter, "skip=25". Also, remember that most clients will want to use the .referenced format for these requests, which will allow dealing with larger result sets.

Send the request (this one lists up to 50 projects with the label, "MyProject"):
`curl https://www.example.com/calcdb/projects.referenced?label=MyProject&limit=50&apiToken=abc123`

The response (truncated):
    {
        "status": "ok",
        "projects": \[
            {
                "label": "MyProject",
                //...
            },
            //...
        \]
    }

## Delete the project

This is an easy one.
Send the request:
`curl -X DELETE https://www.example.com/calcdb/projects/53e13203e4b07e53be02f130?apiToken=abc123`

The response: `{"status": "ok"}` 

## Photos

We've sort of avoided this topic until now because photos are a bit different than other resources. There is no way to directly save, update, or delete a photo. They are instead considered to be part of a Location for these purposes. However, when you request a Location, CalcDB does not send the photos along with it. Instead, if a location contains photos, each image will have a 'link' attribute that provides the id of that photo. You can then send a GET to "<base-url>/photos/<id>" to retrieve the photo.
 
 An example of an image 'link' (the rest of the location is not shown):
    //in a Location json
    "images": \[
        {
            "url": "photo123.jpg",
            "link": {
                "source": "filefortAssetService",
                "id": "2af54d11"
            }
        }
    \]
    
This shows an example of a location with one photo. The 'url' property will typically just be a filename. This will be resolved when a project in imported into SPIDACalc, but CalcDB simply ignores it. The part that CalcDB is interested in is the 'link'. It contains the id for that photo. 

To request the image from the example:
`curl https://www.example.com/calcdb/photos/2af54d11?apiToken=abc123"

The response:
    {
        "status": "ok",
        "photo": "<base-64 encoded string>..."
    }
    
The response will include the photo bytes as a base64 encoded string in the body of the JSON response. This would then be decoded by the client and written to a file. There is currently no support for any other request type or options for photos.


# REST API Descriptor

The available fields that can be added to requests as parameters are listed in [restAPI.json](https://github.com/spidasoftware/schema/blob/master/resources/v1/schema/calcdb/interfaces/restAPI.json). For each item in "resources" (project, location, design, photo), there is an object for each type of API call: 'list', 'show', 'save', 'update', 'delete'. Each of these objects has a 'parameters' array that lists the possible parameters for it. Some will be required, others are optional. List requests have the most parameters since all the searchable fields will be enumerated. All list parameters are implicitly combined with a logical AND. So, if you pass the params, "id=myCalcId&label=MyProject", then CalcDB will only return projects that match both of those.


# The Finer Points 

### Saving or Updating Projects 

There are several possible ways to save/update a project. 

- **Plain old Project JSON**  This is what we did in the example. This would normally just be sent with a ContentType of "multipart/form-data", but "application/X-www-form-urlencoded" is also acceptable (just less efficient). No photos are saved, because none are sent. The request would be sent to "<base-url>/projects.calc" but the '.calc' format is optional since that is the default.

- **Project JSON and Photos**  This is essentially the same as above, except that all the photos for the project must be sent with the request. This MUST be a 'multipart' request. Each photo file should be added to the request body as it's own 'part', and the 'name' of the part must match the photo name given in the 'url' property of the image. For example, given the image: `{"url":"myPhoto4.jpg"}`, the multipart request would need to have a part with it's name as "myPhoto4.jpg" and the ContentType as "image/jpeg".

- **Upload an Exchange File**  Once you have an exchange file, it can simply be sent as part of a multipart POST or PUT request to "<base-url>/projects.exchange". Notice the '.exchange' format - this is required if you're sending an exchange file. CalcDB will take care of unzipping the file and saving the project json and all the photos. This oftentimes tends to be the simplest way, since SPIDACalc can simply export projects directly to an exchange file.

### Saving or Updating Locations or Designs 

Technically, Locations and Designs can be saved individually, but in practice it's probably not what you want to do. Most of the time, you will want to save an entire Project, which will include all the Locations/Designs. The important thing to note if you do decide to save individual Locations or Designs is that the '.exchange' format is not supported. These requests will have to be sent using '.calc' format.

### Deleting or Updating Locations or Designs

For Locations and Designs that are part of a Project, they cannot be updated or deleted directly. You must update the parent Project instead. If you try to delete it directly, the API will return an error saying so. If the Location/Design was created on its own (not as part of a project), then it can be deleted or updated directly.


