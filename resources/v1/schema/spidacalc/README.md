Calc Integration API
====================

#SPIDACalc API Capabilities Overview

##There are three parts to the Calc APIs

- A data transfer format for moving project, structure, and results information in and out of spidacalc.
- A REST-like remote control interface for controlling a running instance of calc.
- A REST interface for querying calc client engineering data.

##Data Format

Calc defines an open, human readable format for importing pole and project information. It is in JSON, an industry standard that can be written from any source language. The data format is defined by the schemas available in this project, which can also be used to validate files before they are sent to Calc.

Calc supports a project structure and information about locations. It can import and export addresses, GPS points, remedy information, and other expected meta-data surrounding pole collection and analysis.

It also supports the import of what we call framing plans. Framing plans build assemblies on the pole from predefined standards using our best guess as to how the standard would be applied. This is often the best way to integrate with staking and design programs, or to do a quick first pass based on information about pole heads in a GIS or other accounting system where field specific measurements are not available.

It supports detailed information at the level of a data collection program, allowing for the specification of every attachment to the pole at its exact height and direction, with appropriate material properties. This is the level that will give the most specific and reliable analysis results, and is best used by those looking to integrate Calc with their existing data collection programs. It is also how a structure created in calc will be exported, and can be used to generate custom reports or high-level analysis across multiple projects, or to import specific fields back into an accounting or work order system.

Finally, for export only it can include high level analysis results, including loading percentages or safety factors for all analyzed components on a pole.

In short, for pretty much anything you can do in calc, you get it in or out through this simple and readable data format.


### Calc exchange file format ###

The exchange file format allows any valid project JSON to be put into a portable file that includes project photos and can be easily imported by an end user using the normal Calc UI. Creating an exchange file simply involves putting the project JSON and all the photos into a zip file with the extension '.exchange.spida'. The structure of this file, is shown below, relative to the root of the archive:

<pre>
	my-spida-exchange-project.exchange.spida (archive root)
	|
	+--- project.json
	|
	+--- Photos
	    \
	    +--- photo1.jpg
	    +--- photo2.jpg
	    ...
</pre>

Note that while the file name can be any valid filename (with the .exchange.spida extension), the project json it contains MUST be named 'project.json'. The photos inside the photos directly can be directly referenced by setting the image 'url' property for each image in a location. Image urls will be resolved relative to the 'Photos' directory.

A user can easily import this file into Calc by selecting it in the normal open dialog or by double-clicking it on their desktop. When the project is subsequently saved, it will be saved as a normal .spida file, not in the exchange format.

One thing to note is that the exchange file format is not entirely portable, because it does not itself contain any client data. So, if the project json includes:
	"clientFile": "Demo.client"
then the user must have Demo.client in their clients directory in order to properly open the file.


###Supported Structure Fields

The calc import API supports the following attributes of a structure.

- Pole
- Wires
- Cross Arms
- Insulators
- Anchors
- Guys
- Sidewalk Guys
- Span Guys
- Equipment
- Push Braces
- Damages
- Note Points
- Point Loads
- Span Points

##Web Services

When calc is running on a client machine, it also starts up a small web server that will only accept requests from the local computer. This is how we intend integrators to work directly with calc. It allows integration from any language on any platform - integrators just need to implement a few web service calls and they can be sending their data in and out of calc very easily.

Currently, even though it is done in a web service style, this is only available as a client integration. Calc will still be running on the local machine, and it does not require an internet connection -- integrations will work fine in the field! But it also means that you cannot set up a single calc server somewhere to handle all of your analysis.

##Calc Service

The Calc service is best thought of as a remote control for a running copy of calc. Once Calc is started, an integrating program can make basic commands that can do the same thing as a user would do in the UI. These options include:

- Opening a pole or project.
- Saving the project.
- Analyzing that project.
- Generating reports.
- Running custom scripts that SPIDA has provided to the client.

##Client Data Service

The client data service allows querying of our client-specific materials libraries. This should allow data-collection type integrations to show the user the available attachments in their own interface and to select them when building a design to send to calc for analysis.

#Developer Guide

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

####client_data.json

Located at http://localhost:4560/clientData/<method name>. This interface provides basic querying methods for what client items are available in a client file.

####calc.json

located at http://localhost:4560/calc/<method name>. Provides stateful control methods to a running instance of SPIDACalc. Includes methods to open a file, run analysis, run a report, etc.

###Schema Public access

The schemas are published and available online at http://github.com/spidasoftware/schema

###Command Line Interface

Currently the command line interface only allows for launching calc when it is not already running with a project to open. It will take both .spida and calc_project json files as arguments, though. To open a json file run:

`calc.exe myProject.json`

###Using the examples

The example data files are json files that can be opened in any text editor. They can be found in  The RPC example script is a coffeescript file that can be run using the node-js coffeescript package. http://coffeescript.org/

The JSON examples are in [resources/v1/examples/spidacalc](../../../v1/examples/spidacalc)

###More definition of terms

Some of the schemas use terms that are specific to spidacalc or the utility industry. A basic description of the values is included in the schema itself. For a more complete definition of those terms, please see the help menu in SpidaCalc.

###UUIDs

Calc stores UUIDs for all components on the pole. They aren't used as identifiers by the program - they are for interfacing with other applications. You may include them if you have track them, but the id field is the one that is important for building the pole.

###Useful JSON Development tools:

- [jsonlint.com] - validates that your json is correctly formed with more useful errors.
- node_modules/JSV/examples/index.html - provides an easy to use interface for schema validation and viewing errors


###Limitations and known issues:

- All ID on the structure must conform to the Calc naming conventions. All wires must be named with something starting with "Wire#", all equipment with "Equip#". This will be fixed in a later version to allow generic labeling. Correct ID Form is CASE SENSITIVE. EQUIP#1 is not a correct ID. Equip#1 is.
- UUIDs must be actual UUIDs and in the canonical form xxxxxxxx-xxxx-Mxxx-Nxxx-xxxxxxxxxxxx  http://en.wikipedia.org/wiki/Universally_unique_identifier In future versions this will be more generic.
- parameters sent to RPC interface must be in the order specified in the interface description.
- Load case names are case-sensitive, to what is in the client file. This can be different from what is shown in calc.

##Calc Project Structure

Calc uses a single pole model of analysis. It offers the ability to organize locations and to pull collected data from other locations, but it is not a connected model. All spans on a pole are defined in terms of that pole alone.

A project has leads, which have locations. Leads can represent a physical line of connected poles, but they do not have to. They can also just be a unit of organization.

A location is a single point on a map where a pole structure is located. It will have the GIS type information for that station - GPS coordinate and address, and any other metadata. A lead has many locations.

A location has many designs. A design is a specific version of the structure at a location. This allows for comparison between multiple design for quality control or remediation work. A design has a single structure.

##Calc Pole Structure

The calc structure is a model of a single pole under analysis and everything directly attached to it.

###Attachments

Components attaching directly to the pole have structure in common

- attachHeight: The height above ground level of the highest bolt attaching this to the structure.
- direction: The bearing of the object relative to the pole.
- owner: The company who owns the attachment
- clientItem: The item in our client file describing the material properties of this attachment.

Components at a distance from the pole have structure in common

- distance: The distance from the pole to the item.
- direction: The bearing of the object relative to the pole.

###Directions

Directions are in degrees. 0 is North, 90 is East, 180 is South, 270 is west. They are the bearing from the main pole to that item. This matches the display in Calc.

*Note* in the 4.4.2 release, there is a bug in the direction handling. The rotation is reversed. 0 is North, 270 is East, 180 is South, 90 is West.

###Wire End Points

Calc uses the concept of wire end points to describe spans. A Wire End Point is something that the spans on your pole are going to. It could be another pole, it could be a building. It holds the distance and direction and the list of wires going to it.

Each wire has its own information on tension, material properties, attach height, and midspan.

Each span from the main pole is its own wire object, even if they are connected. So on normal line construction, with a single line, you will have two wire end points, and two wires - one running to each wire end point.

Calc has different wire end point types. The generic types are OTHER_POLE and BUILDING. There are also two special types, NEXT_POLE and PREVIOUS_POLE. These indicate the main run in the line of poles. There can only be one of each of them. Anchors whose support type is set to BISECTOR will automatically track the bisector angle between the NEXT_POLE and the PREVIOUS_POLE when edited in Calc.

##FAQ

###I send my request to calc, but it says that I'm missing required parameters?

The individual parameters are passed as URL or POST FORM encoded parameters, even if the parameters themselves are JSON strings. make sure you set the following on your question:

- contenttype = application/x-www-form-urlencoded
- charset=UTF-8

This design decision was made to make it easy to test basic requests, but it seems to cause some confusion.

###Can I use this to run my own analysis server?

No, it is not supported by the terms of use of calc or the schema.

###What reports are available?

The report ID is any report named in your client file, as well as two of the reports available in the calc menu: "Project Summary Report" and "Project Details Report"


###Questions/Support

For questions about the SPIDACalc API, please contact SPIDA support at support@spidasoftware.com.




