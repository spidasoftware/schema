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

1. `project_json`: a required [project](../../resources/v1/schema/spidamin/project/project.schema). An update is performed if the project.id is present, and create if not.

#### Returns

1. An [id object](../../resources/v1/schema/general/id.schema)

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


Get Project
----------

Return a list of projects

#### URL

`https://${HOST}/${APP}/projectAPI/getProjects`

#### Allowed Methods

`GET`

#### Parameters

`project_ids` or `project_code_values` or `associated_station_ids` are required

1. `project_ids`:A `number` array or project ids to retrieve
1. `project_code_values`: Get project containing this `string` array of the project codes.
1. `associated_station_ids`: Get projects containing stations with this `string` array of ids.
1. `details`: a boolean on whether to fetch all stations.

#### Returns

1. A [projects object](../../resources/v1/schema/spidamin/project/projects.schema)

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

A [general response object](../../resources/v1/schema/general/method_response.schema)

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

1. A [flows object](../../resources/v1/schema/spidamin/project/flows.schema)
