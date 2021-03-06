Project API
==========

For working with projects and the associated members, codes, and referenced stations.

## Implementing Apps

1. projectmanager

Methods
========

Create or Update
-----------

Create or update a project

#### URL

`https://${HOST}/${APP}/projectAPI/createOrUpdate`

#### Allowed Methods

`POST`

#### Parameters

1. `project_json`: a required [project](../../resources/schema/spidamin/project/project.schema). An update is performed if the project.id is present, and create if not.

#### Returns

1. An [id object](../../resources/schema/general/id.schema)

#### Examples

Let assume I have some basic project information that I want to use to create a SPIDAMin project.  What you need at a minimum is a name, and a flowId.  The flowId is used to determine what work process this particular project is going to use.  You either will know this id, or you can use the _getFlows_ method in the spidamin/project/interfaces/pm.json interface to get all the available flow by name and get the id from there.  The project _createOrUpdate_ has a parameter that is a JSON object.  The very basic JSON object we will use is:

    curl -g -d 'project_json={%22name%22:%22Name%22,%20%22flowId%22:26988}' 'https://test.spidasoftware.com/projectmanager/projectAPI/createOrUpdate'

This would give me the following, if the flow was available to my user:

    {"result":{"id":55485}}

This result gives you the id of the newly created project and that can be used in the future to add/remove stations, members, files etc. to/from this project.

Let say we want to update a project from the id that was returned previously.  The first thing we need is a project JSON that validates against the schema.  There are a couple caveats that you would have to know outside the strict schema validation, but these considerations should be enumerated in the service .json method description. An example would be the project.id, it's inclusion would change how the createOrUpdate method works.  This means it isn't strictly required, but is required when updating the project.

From our previous call we know the project's id is 55485 and we can use this to update the project.  Lets say we want to add a station to this project.  We would construct an object that had an additonal station like below:

    {
      "id":55485,
      "draft": false,
      "stations": [
        {
          "source": "Asset_Service_A",
          "stationId": "ff8081814488add8014488f828fc0c54"
        }
      ]
    }

Create or Update with SPIDA DB
-----------

Create or update a project with a spida db project

#### URL

`https://${HOST}/${APP}/projectAPI/createOrUpdateWithDB`

#### Allowed Methods

`POST`

#### Parameters

1. `project_json`: a required [project](../../resources/schema/spidamin/project/project.schema). An update is performed if the project.id is present, and create if not.
1. `spidaFile`: an exchange.spida file (required if you want a linked db project)
1. `clientFile`: client file in json format (required if you want a linked db project)

#### Returns

1. An [id object](../../resources/schema/general/id.schema)

#### Examples

The following curl command creates a min project and links a spidadb project 

	curl -F 'spidaFile=@/calc-files/test-project.exchange.spida' \
	-F 'clientFile=@/client-files/clientFile.json' \
	-F 'project_json={"name":"api-test-6", "flowName":"test", "stations":[{"spotted":true, "display":"api-test-loc-6", "geometry": {"coordinates": [-82.86015272140503, 40.00846977551567], "type": "Point"}}]}' \
	http://localhost:8888/projectmanager/projectAPI/createOrUpdateWithDB?apiToken=abc123

Is Project Name Valid
-----------

Checks if a project name has valid characters and is unique within a company

#### URL

`https://${HOST}/${APP}/projectAPI/isProjectNameValid`

#### Allowed Methods

`GET`

#### Parameters

1. `name`: project name to check (required)
1. `company_id`: the company the project will be in (not required, defaults to current company)

#### Returns

1. a json object with valid as the key and a boolean value

#### Examples

The following curl command checks if a project name is valid

	curl 'http://localhost:8888/projectmanager/projectAPI/isProjectNameValid?name=test&company_id=2&apiToken=abc123'

Get DB Project by DB ID
-----------

Get a SPIDA DB Project with a SPIDA DB ID using projectmanager project permissions

#### URL

`https://${HOST}/${APP}/projectAPI/getDBProjectByDBId`

#### Allowed Methods

`GET`

#### Parameters

1. `db_id`: a db id string (required)
1. `format`: a format string: calc, referenced, or exchange (not required, defaults to calc)

#### Returns

1. An exchange file or 
1. A [calc project](../../resources/schema/spidacalc/calc/project.schema) or
1. A [referenced project](../../resources/schema/spidamin/spidadb/referenced_project.schema)

#### Examples

The following curl command gets a spida db project through projectmanager

	curl 'http://localhost:8888/projectmanager/projectAPI/getDBProjectByDBId?db_id=5c8a3f358cd8ac70a42aec58&format=referenced&apiToken=abc123'

Get DB Location by DB ID
-----------

Get a SPIDA DB Location with a SPIDA DB ID using projectmanager project permissions

#### URL

`https://${HOST}/${APP}/projectAPI/getDBLocationByDBId`

#### Allowed Methods

`GET`

#### Parameters

1. `db_id`: a db id string (required)
1. `format`: a format string: calc or referenced (not required, defaults to calc)
1. `detailed_results`: a boolean string: if true will retrieve and embed all detailed results 

#### Returns

* An object with a `location` and `clientData` 
  * the location may be a [calc location](../../resources/schema/spidacalc/calc/location.schema) or
  * the location may be a [referenced location](../../resources/schema/spidamin/spidadb/referenced_location.schema)
  * the clientData is a [client data object](../../resources/schema/spidacalc/client/data.schema)

#### Examples

The following curl command gets a spida db location through projectmanager

	curl 'http://localhost:8888/projectmanager/projectAPI/getDBLocationByDBId?db_id=5ceec30c8cd8ac160e76f777&format=referenced&apiToken=abc123'

Get Linked DB Locations
-----------

Get all SPIDAdb locations linked to a SPIDAdb location ID

#### URL

`https://${HOST}/${APP}/projectAPI/getLinkedDBLocations`

#### Allowed Methods

`GET`

#### Parameters

1. `station_id`: a station id string (required)
1. `source`: the source asset service to lookup links, if not provided the we use SPIDAdb and SPIDAdb Active (optional)
1. `version`: the schema version (optional)

#### Returns

An array of [referenced location](../../resources/schema/spidamin/spidadb/referenced_location.schema)

#### Examples

The following curl command gets all referenced locations linked to the id passed in

	curl 'http://localhost:8888/projectmanager/projectAPI/getLinkedDBLocations?station_id=5d934ae88cd8ac812e38b9e1&apiToken=abc123'


Get Location Thumbnails by DB ID
-----------

Get location thumbnail photos by the location id using projectmanager project permissions

#### URL

`https://${HOST}/${APP}/projectAPI/getLocationThumbnailsByDBId`

#### Allowed Methods

`GET`

#### Parameters

1. `db_id`: a db id string (required)

#### Returns

a zip file with photos

#### Examples

The following curl command gets a zip of photos

	curl 'http://localhost:8888/projectmanager/projectAPI/getLocationThumbnailsByDBId?db_id=5d31b8e78cd8ac33b51dceab&apiToken=abc123' --output file.zip

Get Location Photos by DB ID
-----------

Get location photos by the location id using projectmanager project permissions

#### URL

`https://${HOST}/${APP}/projectAPI/getLocationPhotosByDBId`

#### Allowed Methods

`GET`

#### Parameters

1. `db_id`: a db id string (required)

#### Returns

a zip file with photos

#### Examples

The following curl command gets a zip of photos

	curl 'http://localhost:8888/projectmanager/projectAPI/getLocationPhotosByDBId?db_id=5d31b8e78cd8ac33b51dceab&apiToken=abc123' --output file.zip

Find DB Projects in DB
-----------

Find SPIDA DB Projects with a label containing the string passed in using projectmanager project permissions

#### URL

`https://${HOST}/${APP}/projectAPI/findDBProjectsInDB`

#### Allowed Methods

`GET`

#### Parameters

1. `label`: the string to search for (required)
1. `limit`: max number of projects to return (not required, defaults to 100)
1. `format`: a format string: calc or referenced (not required, defaults to referenced)

#### Returns

1. An array of [calc projects](../../resources/schema/spidacalc/calc/project.schema) or
1. An arrary of [referenced projects](../../resources/schema/spidamin/spidadb/referenced_project.schema)

#### Examples

The following curl command finds spida db projects containing pole

	curl 'http://localhost:8888/projectmanager/projectAPI/findDBProjectsInDB?label=pole&limit=100&format=referenced&apiToken=abc123'


Find Stations to Match
-----------

Find the best match stations for a given project. This is generally use as part of uploading a SPIDAcalc file. The return payload can be used to help construct a call to createOrUpdateWithDB 

This call walks through logic to find a best guess of which stations in the available asset services are equivalent to the stations in passed in to this call. The primary purpose of this method is to ensure that uploaded SPIDAcalc project files have all of their poles mapped to the correct poles in the SPIDAstudio system (for example, in the customer's GIS system, or previous versions of the same pole stored in SPIDAdb). This matching follows roughly the following logic:

The `project` returned will include a `station` entry for every station entry passed in.

If a "good" match is found for a station, it will have the `stationId` and `source` fields filled in. A match is considered good if it is a match by `stationId`, if `display` matches and is within 50ft of `geometry`, or if `display` matches and there is only one result returned.

If `projectId` is passed, then the stations in that project will be searched first. The general search will only be performed for stations that are not found in that project.

For each remaining station: 

If `stationId` is passed, all SPIDAdb asset services will be searched for that `stationId` and a station returned from those will be prioritized.

If `stationId` is not passed, or no station is found under that `stationId`, all asset services will be searched based on `display`. All results from this search will be returned in the `availableStations` field.

If `geometry` is passed, all matching stations will be returned in `availableStations`, but only a station close to that `geometry` may be selected. Note -- pushing a SPIDA file to DB requires that all designs have a geographiccoordinate. If the file you are going to push does not have `geometry` to put into this request, you will need to copy the coordinate information from the station returned here to the SPIDA file before calling createOrUpdateWithDB

If no matches are found, the `station` will contain only the station information was the passed into the request.

#### URL

`https://${HOST}/${APP}/projectAPI/findStationsToMatch`

#### Allowed Methods

`POST`

#### Parameters

1. `stations`: a required list of stations [stations](../../resources/schema/spidamin/asset/station.schema). Only the `display` field is required to be filled in, but additional matching will be performed if `geometry` and `stationId` are included.

2. `projectId`: an optional SPIDAmin project ID to limit searching

#### Returns

1. A [project](../../resources/schema/spidamin/project/project.schema) . If the project's `id` is present, this is an existing project in the system. Otherwise it is the stations portion of the project payload needed to create a new project with the stations specified in the request, with some additional information in the `availableStations` field if there was more than one good match for a given station. To actually create a project from this payload you will need to additionally specify the workflow.

Get Projects by DB ID
-----------

Get projectmanager projects that have the spida db ids passed in and filter based on projectmanager permissions

#### URL

`https://${HOST}/${APP}/projectAPI/getProjectsByDBId`

#### Allowed Methods

`GET`

#### Parameters

1. `db_ids`: the spida db id string in a json array (required)
1. `details`: a string value of true or false (not required, defaults to false)

#### Returns

1. An array of [min projects](../../resources/schema/spidamin/project/project.schema)

#### Examples

The following curl command gets pm project by spida db ids

	curl -g 'http://localhost:8888/projectmanager/projectAPI/getProjectsByDBId?db_ids=[%225d30ae9a8cd8ac09ab3b2a45%22]&details=true&apiToken=abc123'

Get Projects
----------

Return a list of projects

#### URL

`https://${HOST}/${APP}/projectAPI/getProjects`

#### Allowed Methods

`GET, POST`

#### Parameters

`project_ids` or `project_code_values` or `associated_station_ids` are required

1. `project_ids`:A `number` array or project ids to retrieve
1. `project_code_values`: Get project containing this `string` array of the project codes.
1. `associated_station_ids`: Get projects containing stations with this `string` array of ids.
1. `details`: a boolean on whether to fetch all stations.

#### Returns

1. A [projects object](../../resources/schema/spidamin/project/projects.schema)

#### Examples

Get My Projects
----------

Return a list of projects for the current user

#### URL

`https://${HOST}/${APP}/projectAPI/getMyProjects`

#### Allowed Methods

`GET`

#### Parameters

No parameters are required.

1. `company_id`: A `number`, finds current user projects in this company id.  If not set uses the company_id that the user is currently logged in under.
1. `details`: A `boolean`, if true includes all stations
1. `db_only`: A `boolean`, if true includes only projects with a spidaDBId

#### Returns

1. If details true, a [projects object](../../resources/schema/spidamin/project/projects.schema)
1. If details false, a minimal object with id, spidaDBId, name, projectCodes, and status set

Get All Project In Company
----------

Return a list of projects with only the projectCodes and project ids.

#### URL

`https://${HOST}/${APP}/projectAPI/getAllProjectsInCompany`

#### Allowed Methods

`GET`

#### Parameters

No parameters are required.

1. `company_id`: A `number`, finds all projects in this company id.  If not set uses the company_id that the user is currently logged in under.
1. `include_finished`: A `boolean`, if true finds all projects including projects that have been finished in the company. If false finds all projects that have not been finished in that company. If not set defaults to false.

#### Returns

1. A [projects object](../../resources/schema/spidamin/project/projects.schema) (Note: only projectCodes and id will be in the project object)

Get Project Log Messages
----------

Get a list of all log messages on a project

#### URL

`https://${HOST}/${APP}/projectAPI/getProjectLogs`

#### Allowed Methods

`GET`

#### Parameters

`project_id` is required

1. `project_id`: A `number`, the project id of the project that the log message should be added to.

#### Returns

1. a json array of [logMessage](../../resources/schema/spidamin/project/log_message.schema)

#### Examples

`curl 'http://${HOST}/projectmanager/projectAPI/getProjectLogs?project_id=20183&apiToken=abc'`

Add Log Message
----------

Add a log message to a project.

#### URL

`https://${HOST}/${APP}/projectAPI/addLogMessage`

#### Allowed Methods

`POST`

#### Parameters

`project_id` and `log_message_json` are required

1. `project_id`: A `number`, the project id of the project that the log message should be added to.
1. `log_message_json`: [logMessage](../../resources/schema/spidamin/project/log_message.schema).

#### Returns

1. An [id object](../../resources/schema/general/id.schema)

#### Examples

Delete
-------

deletes Members, ProjectCodes, Stations, Projects

#### URL

`https://${HOST}/${APP}/projectAPI/delete`

#### Allowed Methods

`POST`

#### Parameters

1. `project_ids`:A `number` array of project ids to delete
1. `project_code_ids`:A `number` array of project code ids to delete
1. `member_ids`:A `number` array of project member ids to delete
1. `station_ids`:A `number` array of project station ids to delete

#### Returns

A [general response object](../../resources/schema/general/method_response.schema)

Get Flows
-------

Returns available flows for project creation

#### URL

`https://${HOST}/${APP}/projectAPI/getFlows`

#### Allowed Methods

`GET`

#### Parameters

1. `include_viewable`: a `boolean` value. If false, only flows usable for project creation will be returned.  If true, all viewable flows will also be

#### Returns

1. A [flows object](../../resources/schema/spidamin/project/flows.schema)
