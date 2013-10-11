Calc Integration API
====================

These are the Integration API descriptions for SPIDACalc. There are two types of schemas.

###Data schemas

This describes the data format  supported by calc. It is available broken into logical units in v1/calc or as a single file for simpler validation in public/v1/calc. Example data files are in the examples directory. The format is best approached after learning basic calc functionality. All properties mirror the calc user interface pretty closely.

####structure.schema

Schema for an individual detailed pole structure. It will define individual attachments, end points, and other physical components. This is for import from another data collection or pole design tool.

####framing_plan.schema

Schema for a simplified framing plan. It defines a pole design in very broad terms in code units. This tends to be a more useful way to import from a staking tool or GIS database.

####calc_project.schema

Schema for a calc project. Includes information on GPS positions, street addresses, photos, remedies, and design structure defined by either design.schema or framing_plan.schema. This is the format that be opened and exported directly by SpidaCalc.

###RPC Interfaces

RPC interfaces are exposed at http://localhost:4560/ while SPIDACalc is running. They allow control over core operations of SPIDAcalc from another programming running locally via basic HTTP POST requests. There is an example script using these methods in examples/scripts/example_RPC_client.coffee.

####client_data_interface.schema

Located at http://localhost:4560/clientData/<method name>. This interface provides basic querying methods for what client items are available in a client file.

####calc_interface.schema

located at http://localhost:4560/calc/<method name>. Provides stateful control methods to a running instance of SPIDACalc. Includes methods to open a file, run analysis, run a report, etc.

###Schema Public access

The schemas are published and available online at http://github.com/spidasoftware/schema

###Command Line Interface

Currently the command line interface only allows for launching calc when it is not already running with a project to open. It will take both .spida and calc_project json files as arguments, though. To open a json file run:

`calc.exe myProject.json`

###Using the examples

The example data files are json files that can be opened in any text editor. The example script is a coffeescript file that can be run using the node-js coffeescript package. http://coffeescript.org/

###UUIDs

Calc stores UUIDs for all components on the pole. They aren't used as identifiers by the program - they are for interfacing with other applications. You may include them if you have track them, but the id field is the one that is important for building the pole.

###Validating JSON

There are several libraries available for validating your output against the schema. JSV is included with this distribution. The CalcSpec tests show how to use it, or there is a validation web page in node_modules/JSV/examples/index.html.

###Useful JSON Development tools:

- [jsonlint.com] - validates that your json is correctly formed with more useful errors.
- node_modules/JSV/examples/index.html - provides an easy to use interface for schema validation and viewing errors

###Limitations in this beta version:

Custom form import/export is not yet supported. Support will be added by the release version.
Photo import/export is not yet support. Support will be added by the release version using the schema provided.

###Questions/Support

For questions about the SPIDACalc API, please contact Mike Ford at mike.ford@spidasoftware.com.
