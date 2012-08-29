SPIDA Software JSON Interfaces
==============================

General Overview of the Schema
--------------------------------

We will strive for flexibility and simplicity, which are usually the same.

There will be two types of services "remote procedures" and "stateful service".

To address these two types of services we will be implementing two different service types "JSON-RPC" and "REST" services.  These two types of services address different needs within the service environment.

### RPC

An example of the "RPC" type would be something that would in java have a service interface, a defined set of methods that can be replaced with any service that conforms to that interface.  This would be for example a "math" service with methods like "add" and "subtract", not that this example would have different implementations, a more complex math service certainly could.

* Request: RPC type methods will be done agains a url that ends with the method name.  
* Params: parameters for the procedure are included in the http params list after the method name i.e. add?n1=1,n2=4
* Response: the response will always be formated in the generic "method_response", this allows for passing error codes and the result. Example: {"result":5}

### REST

The second type of services are "REST" services.  These services differ from the first in that they need to retain and store the state of some objects in the server.  A user service would be an example of this type of service, where you can create, modify, update and delete these users on the server.  With this type of service you generally won't need to define the methods to the same degree, but we will still use defined object within this service.

* Request: REST type methods will be done agains a url that ends with the id or object name.  
* Params: parameters for the procedure are included in the http params list after the method name i.e. project/1?
* Response: the response will always be formated in the generic "method_response", this allows for passing error codes and the result. Example: {"error":"Object not found"}

### General Objects

#### Request and Response

Remote procedure calls should conform to the [JSON-RPC 2.0 standard](http://www.jsonrpc.org/specification).  

#### Geometry

Geometry objects should conform to the [geojson object spec](http://www.geojson.org/geojson-spec.html).  

### Tools

We have included jasmine tests using nodejs.  

To Install:

	npm install jasmine-node -g

Run you tests:

	jasmine-node --coffee spec

For Debugging: [JSON Tools](https://github.com/ddopson/underscore-cli)
	
	echo "{'some':'json'}" | underscore pretty --color


***

SPIDA® is a registered trademark of SPIDAWeb LLC. Copyright © 2012 SPIDAWeb LLC. All rights reserved. All other brands or product names are the property of their respective holders.
[spidasoftware.com](http://www.spidasoftware.com/)
