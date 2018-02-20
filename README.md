SPIDA Software JSON Interfaces
==============================

[![Build Status](https://travis-ci.org/spidasoftware/schema.svg?branch=master)](http://travis-ci.org/spidasoftware/schema)

* LAST RELEASED VERSION: 4.0.0
* CURRENT MASTER VERSION: 5.0.0-SNAPSHOT

General Overview
--------------------------------
The SPIDAschema project documents the available services and data formats for third parties wanting to integrate with SPIDA Software applications.

Versions
-------
The data formats, examples, and available web service interfaces will all change from version to version. Please make sure you are reading the correct documentation and validating against the correct schema for the version of the SPIDA product you are using.
- [v5.0.0-SNAPSHOT](https://github.com/spidasoftware/schema) SPIDAcalc 7.0.2, SPIDAstudio 18.0
- [v4.0.0](https://github.com/spidasoftware/schema/releases/tag/v4.0.0) SPIDAcalc 7.0.0-7.0.1
- [v3.0.1/v1.11](https://github.com/spidasoftware/schema/releases/tag/1.11) SPIDAmin 16.1
- [v3.0.0/v1.09](https://github.com/spidasoftware/schema/releases/tag/v1.09) SPIDAcalc 6.1.2-6.4.0
- [v2.0.0/v1.0.6](https://github.com/spidasoftware/schema/releases/tag/1.0.6) SPIDAcalc 6.0.0-6.1.1
- [v1.0.0](https://github.com/spidasoftware/schema/releases/tag/1.0.0) SPIDAcalc 5.3.0-5.3.4

License
-------
By accessing, downloading or using the APIs defined here you are agreeing to these terms of use [here.](https://github.com/spidasoftware/schema/raw/master/SPIDAWeb%20API%20Terms%20of%20Service%20(Updated%204%20October%202017).pdf)

Products
----------

### SPIDAcalc
Desktop pole modeling and loading analysis software.
- [Developer Guide](doc/calc.md)
- [Schema](resources/schema/spidacalc)
- [Examples](resources/examples/spidacalc)

### SPIDAmin
Web based utility project and asset management software.
- [Developer Guide](doc)
- [Schema](resources/schema/spidamin)
- [Examples](resources/examples/spidamin)

### SPIDAcee
Cloud based pole analysis service.
- [Developer Guide and cee-cli Tool](https://github.com/spidasoftware/cee-cli)
- [Schema](resources/schema/spidacalc/cee)
- [Examples](resources/examples/spidacalc/cee)

### SPIDAdb
Pole model database and asset service.
- [Developer Guide](doc/apis/spidadbAPI.md)
- [Schema](resources/schema/spidamin/spidadb)
- [Examples](resources/examples/spidamin/spidadb)

Project Structure
--------------------
- [doc](doc) - Documentation and overview of services that are available through all our APIs.
  - [resources](resources)
    - [schema](resources/schema) - All schema files. Schema files describe the payload data format for the SPIDA API services.
      - [spidacalc](resources/schema/spidacalc) - Schemas for communication with SPIDACalc
      - [spidamin](resources/schema/spidamin) - Schemas for communication with SPIDAMin
    - [examples](resources/examples) - Example data files and simple integrations.
-[src](src) - Java utilities for schema validation and service communication.

JSON
--------
Our services rely heavily on the [JSON](https://en.wikipedia.org/wiki/JSON) format and HTTP web services.

A JSON viewer plugin for you browser is highly recommended when working with these APIs.

Schema
--------
Our schemas currently conform to the [json-schema-draft-03](https://tools.ietf.org/html/draft-zyp-json-schema-03) standard

Schema files end in .schema. The .schema files are used to validate the objects passed into specific API endpoints.

Tools
-----
### Schema Validation
The jar built by this project contains a [Validator](src/main/groovy/com/spidasoftware/schema/validation/Validator.groovy) utility class that can be used to validate json against this schema. If you are not working on the jvm, however, there are quality json schema validators available for almost any language.

### Strict Mode
Strict mode is supported as of version 5.0.0. Previous versions of the schema always parsed "additionalProperties=false" strictly. This made new formats incompatible with older SPIDA products, because any unknown fields violated the schema. Going forward, data loss of new feature may occur when processing new formats in order versions, but the fields that are known should transfer properly. This should make integrations less fragile and version dependent.

By default, most SPIDA schemas are specified as "additionalProperties=false". However, the included Validator is configured to ignore this property by default. This is generally desirable in production, but means that in testing a simple mis-naming of a property can be easily missed. To solve this we have added 'strict' mode.

- You can add `"strict":true` to an object being validated, even if it does not appear in the schema.  This means that the validator won't allow any additional properties. 
- We recommend using this in dev and test to make sure you didn't misspell something or add the wrong key.
- We recommend removing the strict property or setting it to false in production so that we can be more flexible when handling different schema versions.
- The validator will not be strict by default.
- Third party schema validators may require additional configuration to handle this mode. By default, third party validators will interpret all schemas as "strict", and will throw an error on many of our json objects if `"strict":true` is included, as it is an unknown field per the schema.
- No data generated by SPIDA products will have strict mode enabled.

### Command line validator
We include a command line validator to validate against any of our included schemas. To run the command, use the validateJson gradle task

    gradlew :validateJson -Pschema=/path/to/schema -PjsonFile=/path/to/json
    schema - path to schema starting from resources. eg. /v1/schema/spidacalc/calc/structure.schema
    json - json file to be validated.

For example, to validate the "one of everything" structure example, from the schema directory you would type:

    gradlew :validateJson -Pschema=/v1/schema/spidacalc/calc/structure.schema -PjsonFile=resources/examples/spidacalc/designs/one_of_everything.json

The tool uses our included Validator java class.

### Changesets
[Changesets Package](src/main/groovy/com/spidasoftware/schema/conversion/changeset)

The included Validator tool will automatically convert some json  (primarily the Project exchange files used by SPIDAcalc and SPIDAdb) to the current version of the schema before validating. The same converter classes can be used to downgrade a json file to an earlier version of the schema for backwards compatibility purposes.

Unless an exception is noted, newer SPIDA products will continue to read older versions of these data formats, and may write them as a separate method. Older versions of SPIDA products may or may not be compatible with newer data formats. We make every attempt to change these formats no more than necessary to support new features in SPIDA products.

### Java Tools

In the src/* folders are some java classes that can be helpful when integrating our stuff into a java world.  This includes a classes that produces a JSON descriptor of a class and validate a service against a JSON descriptor.

The jar can be compiled with:

    gradlew install

***

SPIDA® is a registered trademark of SPIDAWeb LLC. Copyright © 2018 SPIDAWeb LLC. All rights reserved. All other brands or product names are the property of their respective holders.
[spidasoftware.com](http://www.spidasoftware.com/)
