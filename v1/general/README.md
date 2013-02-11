General Object Schema's
============

1. address.schema - General address holder
2. bearing.schema - Used to hold direction information
3. geometry.schema - Geometry objects should conform to the [geojson object spec](http://www.geojson.org/geojson-spec.html). 
4. measurable.schema - Schema for objects that contain a value and a unit, like "10 feet".
5. method_response.schema - We have wrapped all our responses in a basic object to allow for error codes.  All service methods should return this object, if a service descriptor does return a specific schema it would be set to the result inside this object.
6. owner.schema - Object to describe some basic information about a company that owns an object.
7. service_method.schema - Each of our services descriptor's methods will conform to this schema