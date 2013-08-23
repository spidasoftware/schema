SPIDAMin API
============

Overview
---------

The sub-folders included in SPIDAMin are:

1. asset - These objects deal with the assets and asset services.  An example is an asset is a pole.
1. geo - These are objects related to geo-coding, and the service definitions.
1. project - SPIDAMin projects and services are defined here. This contains the primary means to interact with SPIDAMin.
1. user - The definitions of users and user services are in this folder.  


Example Processes 
----------------

We will go through a two very simple examples.  The first we will fetch asset information. The second we will fetch and then update a project.  For these examples we have used a command line tool curl, but any system would work that can make the needed HTTP requests.  All of these requests are GET requests, but a POST request might be needed depending on how long the parameters are.

### Asset Retrieval 

#### API Token

These examples assume there is a complete SPIDAMin deployed to the url of "https://test.spidasoftware.com" and have an API token with a value of "1a17405f-52ca-4392-b5cf-89df8cc160be"

For most of the calls against a SPIDAMIN service you will need to include your apiToken parameter, this is in addition to any parameters required by the method.  This would be for the service interface if it is implemented on a server environment.  There are times when we implement the same service in the local environments, and then the apiToken would not be needed, but in most cases it will be required.  If you make a service call but get redirect to a security login, then your apiToken was not included or was invalid. 

#### Error Messages

From the spidamin/asset/interfaces/asset.json we will just the _getStations_ method.

    curl -g 'https://test.spidasoftware.com/assetmaster/assetAPI/getStations?apiToken=1a17405f-52ca-4392-b5cf-89df8cc160be'

The response from the server would be:

    {"error":  { "code": "MISSING_REQUIRED_PARAM", "message": "station_ids or bounding_box not provided"} }

Notice this is an error message and it is pretty informative.  The different response codes are found in the response code [schema](../general/method_response.schema).

#### Successful Response

_NOTE: in the following examples we will leave off the apiToken parameter to make the examples simpler._

As you can see, and error object was returned, and it told us we are missing an required parameter.  _station_ids_ or _bounding_box_ was not included.  One of these is needed because you are either getting assets by location or id.  Lets add the required _stations_id_ parameter and try again:

    curl -g 'https://test.spidasoftware.com/assetmaster/assetAPI/getStations?station_ids=["1"]'

The reponse from the server this time is:

    {"result":{"stations":[]}}

This is a successful message, it just didn't find any stations with that id. 

#### Successful Response with Object

A response that found something would look more like:

    {
      "result": {
        "stations": [
          {
            "assetTypes": [
              "POLE"
            ],
            "stationId": "ff8081813dfe983f013dfea557060000",
            "primaryAssetOwnerId": 43,
            "geometry": {
              "type": "Point",
              "coordinates": [
                -81.90434265136719,
                37.48816680908203
              ]
            },
            "primaryAssetType": "POLE",
            "dataProviderId": 43
          }
        ],
        "geometry": {
          "type": "Point",
          "coordinates": [
            -81.90434265136719,
            37.48816680908203
          ]
        }
      }
    }

### Project Creating 

The project interface works on the same basic principals as the asset interface, but we will show you how to use the createOrUpdate method here.

Let assume I have some basic project information that I want to use to create a SPIDAMin project.  What you need at a minimum is a name, and a flowId.  The flowId is used to determine what work process this particular project is going to use.  You either will know this id, or you can use the _getFlows_ method in the spidamin/project/interfaces/pm.json interface to get all the available flow by name and get the id from there.  The project _createOrUpdate_ has a parameter that is a JSON object.  The very basic JSON object we will use is:

    curl -g -d 'project_json={%22name%22:%22Name%22,%20%22flowId%22:26988}' 'https://test.spidasoftware.com/projectmanager/projectAPI/createOrUpdate'

This would give me the following, if the flow was available to my user:

    {"result":{"id":55485}}

This result gives you the id of the newly created project and that can be used in the future to add/remove stations, members, files etc. to/from this project.

### Project Updating

Let say we want to update a project from the id that was returned previously.  The first thing we need is a project JSON that validates against the schema.  There are a couple caveats that you would have to know outside the strict schema validation, but these considerations should be enumerated in the service .json method description. An example would be the project.id, it's inclusion would change how the createOrUpdate method works.

Once you have the JSON object, you would submit that JSON to the createOrUpdate method.


