SPIDAMin API
============

Overview
---------

The sub-folders included in SPIDAMin are:

1. asset - These objects deal with the assets and asset services.  An example is an asset is a pole.
1. geo - These are objects related to geo-coding, and the service definitions.
1. project - SPIDAMin projects and services are defined here. This contains the primary means to interact with SPIDAMin.
1. user - The definitions of users and user services are in this folder.  


Example Service Consumption
----------------

The first main way that you would might use our services, is by consuming them.  This would happen if yo have an external system that needs to interact with SPIDAMin.  From this point of view, we will go through a two very simple examples.  The first we will fetch asset information. The second we will fetch and then update a project.  For these examples we have used a command line tool curl, but any system would work that can make the needed HTTP requests.  All of these requests are GET requests, but a POST request might be needed depending on how long the parameters are.

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

_NOTE: in the following examples we will leave off the apiToken parameter to make the examples easier to read but don't forget that you will still need to append the token to any other parameter with `&apiToken=yourtoken`._

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

Example Service Implementation
---------------------------

The other main way that you may be looking to use our services is to implement one of them with internal data to increase the functionality of SPIDAMin.  An example for this would be if you had additional asset information that you wanted to have show up in SPIDAMin.  The process of implementing a custom service is probably going to require varying amounts of support and integration work with us to achieve the desired results, but this section will give you an idea of the work required and allow you to scope the level of your own internal work.  

For this example, lets assume you have identified the need to show your own asset information from some internal service, and you would like that asset information to show up on the SPIDAMin map so that you can create projects from those assets.  The first step is to identify the needed service interfaces you would have to implement, this is where SPIDA can assist.  In this case, the service interface that you need to implement it is the [asset.json](asset/interfaces/asset.json).  If you examine that file, it would indicate what methods your service would need to respond to and what the method signatures would be.  Once you have the service methods and have identified what internal data and services you will use to respond to those methods you have to decide how you are going to expose this internal service.

There are two main ways expose your service, Direct JSON URL End Point, and a Service Adapter.

### Direct JSON URL End Point

The first way is to implement a service that responds to the JSON interface exactly as defined in the _.json_ file at a specific URL.  This would mirror the approach that we showed in the above "Service Consumption" section.  It would require you to have a URL endpoint that responded exactly in the same manner as above with HTTP GETs and POSTs.  Once you did that then, it would be a configuration setting to add that service to SPIDAMin.  This would require zero to minimal development outside your organization, and we think is a very usable format.

### Service Adapter

Sometimes the above method isn't possible or desirable.  This could be for various reasons, your organization doesn't use JSON, you have specific naming requirements, etc.  Given this, your service will still basically need to implement the same methods, but they might have a different name and/or be in a different service format.  A common example would be an organization to mandate the use of WSDL for service interfaces.  What you would need to do is have a service with a 1-to-1 method signature with our defined interfaces.  Once this was done, you could provide us with that mapping and we could write an adapter to allow SPIDAMin to consume your internal service.  This method does require some development and integration work with us, but does allow for you to remain in control of the specific service and be able to replace/update in the future without required assistance from us.

As an additional note, we have used this technique internally to make use of a variety of sources.  A very good example of this is the [ESRI ArcGIS REST service](http://resources.esri.com/help/9.3/arcgisserver/apis/rest/).  That service is able to provide a 1-to-1 method call for our internal asset service.

### Request and Response Objects

In the [asset.json](asset/interfaces/asset.json) there are several methods signatures.  When ever you are implementing a method for use in SPIDAMin you will need to understand the important features of this description. Each method will  have "params" and a "returns" value.  

#### params

Each param will give you a set of basic information 
1. type: tell you if it is a basic primitive (number, string etc), array of primitives (array) or a pointer to a more complex object (object.schema).  If the type is a pointer to an object, you can use that referenced relative schema to build and validate your input param.
2. name: the name of the parameter to pass
3. required: if the parameter is required on every call
4. description: give you more information about this specific parameter.

#### returns

The return value is type of primitive, array of primitives or object that will be return from the service call.  


