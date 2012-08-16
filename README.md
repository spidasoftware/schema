SPIDA Software JSON Interfaces
==============================

General Overview of the Schema
--------------------------------

We will strive for flexibility and simplicity, which are usually the same.

There will be generally two types of services "remote procedures" and "stateful service".

To address these two types of services we will be implementing two different service types "JSON-RPC" and "REST" services.  These two types of service address different needs within the service environment.

### JSON-RPC

An example of the "JSON-RPC" type would be something that would in java have a service interface, a defined set of methods that can be replaced with any service that conforms to that interface.  This would be for example a "math" service with methods like "add" and "subtract", not that this example would have different implementations, a more complex math service certainly could.

### REST

The second type of services are "REST" services.  These services differ from the first in that they need to retain and store the state of some objects in the server.  A user service would be an example of this type of service, where you can create, modify, update and delete these users on the server.  With this type of service you generally won't need to define the methods to the same degree, but we will still use defined object within this service.

### General Objects

#### Request and Response

All remote procedure calls should conform to the JSON-RPC 2.0 standard.
http://www.jsonrpc.org/specification

#### Geometry

Our geometry object conform to the geojson object given at:

//http://www.geojson.org/geojson-spec.html

We have provided a basic schema here for testing.

