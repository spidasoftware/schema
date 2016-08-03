RPC
===

For each of the RPC type services there is a file that gives all the methods available for that endpoint.

Each file will tell you what applications implement that endpoint and what methods are involved.  For each method we tell you a set of basic information that are described below.

#### URLs

Methods will be done against a url that ends with the method name.  The method names are defined in the interfaces.  Example:

    .../projectmanager/projectAPI/getProjects

would be the _getProjects_ method from the spidamin/project/interfaces/pm.json.  The url before the _getProjects_ would be server specific.

#### Allowed Methods

HTTP says that GET calls should not modify data.  So if you are making a call with our API and it is a modification or creation of the object stored in the application, you will need to do a POST.

#### Parameters

Parameters for the procedure are included in the http params list after the method name i.e.

    .../getProjects?projectCodeValues=["value"]

would contain the parameter of projectCodeValues.

If you are using POST, the parameters must still be in the POST params if they are too large for the params list in the request string. You will need to set the request content type to:

     application/x-www-form-urlencoded

And the charset to `UTF-8`

Each method in the documentation has a parameter section and it will give you a set of basic information

1. type: tell you if it is a basic primitive (number, string etc), array of primitives (array) or a pointer to a more complex object (object.schema).  If the type is a pointer to an object, you can use that referenced relative schema to build and validate your input param.
2. name: the name of the parameter to pass, the name _must_ match the formatting shown here.
3. required: if the parameter is required on every call
4. description: give you more information about this specific parameter.

#### Responses

The response body will always be formatted in the generic [_method\_response_](resources/general/method_response.schema), this allows for passing error codes and the result.

    {"result":5}

Would be a valid response object, that might be returned. An example of an error would be:

    {"error":  { "code": "MISSING_REQUIRED_PARAM", "message": "station_ids or bounding_box not provided"} }

Notice this is an error message and it is pretty informative.  The different response codes are found in the response code [schema](../general/method_response.schema).

Again each method in the documentation will tell you what it returns and a link to a schema if applicable.

## Example Service Implementation

The other main way that you may be looking to use our services is to implement one of them with internal data to increase the functionality of SPIDAMin.  An example for this would be if you had additional asset information that you wanted to have show up in SPIDAMin.  The process of implementing a custom service is probably going to require varying amounts of support and integration work with us to achieve the desired results, but this section will give you an idea of the work required and allow you to scope the level of your own internal work.  

For this example, lets assume you have identified the need to show your own asset information from some internal service, and you would like that asset information to show up on the SPIDAMin map so that you can create projects from those assets.  The first step is to identify the needed service interfaces you would have to implement, this is where SPIDA can assist.  In this case, the service interface that you need to implement it is the [asset.json](asset/interfaces/asset.json).  If you examine that file, it would indicate what methods your service would need to respond to and what the method signatures would be.  Once you have the service methods and have identified what internal data and services you will use to respond to those methods you have to decide how you are going to expose this internal service.

There are two main ways expose your service, Direct JSON URL End Point, and a Service Adapter.

#### Direct JSON URL End Point

The first way is to implement a service that responds to the JSON interface exactly as defined in the _.json_ file at a specific URL.  This would mirror the approach that we showed in the above "Service Consumption" section.  It would require you to have a URL endpoint that responded exactly in the same manner as above with HTTP GETs and POSTs.  Once you did that then, it would be a configuration setting to add that service to SPIDAMin.  This would require zero to minimal development outside your organization, and we think is a very usable format.

#### Service Adapter

Sometimes the above method isn't possible or desirable.  This could be for various reasons, your organization doesn't use JSON, you have specific naming requirements, etc.  Given this, your service will still basically need to implement the same methods, but they might have a different name and/or be in a different service format.  A common example would be an organization to mandate the use of WSDL for service interfaces.  What you would need to do is have a service with a 1-to-1 method signature with our defined interfaces.  Once this was done, you could provide us with that mapping and we could write an adapter to allow SPIDAMin to consume your internal service.  This method does require some development and integration work with us, but does allow for you to remain in control of the specific service and be able to replace/update in the future without required assistance from us.

As an additional note, we have used this technique internally to make use of a variety of sources.  A very good example of this is the [ESRI ArcGIS REST service](http://resources.esri.com/help/9.3/arcgisserver/apis/rest/).  That service is able to provide a 1-to-1 method call for our internal asset service.
