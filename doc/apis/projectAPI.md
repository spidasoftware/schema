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

#### Returns

1. A [calc location](../../resources/schema/spidacalc/calc/location.schema) or
1. A [referenced location](../../resources/schema/spidamin/spidadb/referenced_location.schema)

#### Examples

The following curl command gets a spida db location through projectmanager

	curl 'http://localhost:8888/projectmanager/projectAPI/getDBLocationByDBId?db_id=5ceec30c8cd8ac160e76f777&format=referenced&apiToken=abc123'

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

Get All Project In Company
----------

Return a list of projects with only the projectCodes and project ids.  Must be a PM_ADMINISTRATOR to get all projects.

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
