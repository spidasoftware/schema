Calc Integration API
====================

# SPIDACalc API Capabilities Overview

## There are three parts to the Calc APIs

- A data transfer format for moving project, structure, and results information in and out of SPIDACalc.
- A REST-like remote control interface for controlling a running instance of calc.
- A REST interface for querying calc client engineering data.

## Data Format

Calc defines an open, human readable format for importing pole and project information. It is in JSON, an industry standard that can be written from any source language. The data format is defined by the schemas available in this project, which can also be used to validate files before they are sent to Calc.

Calc supports a project structure and information about locations. It can import and export addresses, GPS points, remedy information, and other expected meta-data surrounding pole collection and analysis.

It supports detailed information at the level of a data collection program, allowing for the specification of every attachment to the pole at its exact height and direction, with appropriate material properties. This is the level that will give the most specific and reliable analysis results, and is best used by those looking to integrate Calc with their existing data collection programs. It is also how a structure created in calc will be exported, and can be used to generate custom reports or high-level analysis across multiple projects, or to import specific fields back into an accounting or work order system.

It also supports defining structures on the level of prebuilt standards through our Input Assemblies concept. This is usually the easiest way to get started for integrations with Staking platforms and GIS applications.

Finally, for export only it can include high level analysis results, including loading percentages or safety factors for all analyzed components on a pole.

In short, for pretty much anything you can do in calc, you get it in or out through this simple and readable data format.

#### Calc exchange file format ####

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


#### Supported Structure Fields

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
- Foundations
- Assemblies

## Web Services

When calc is running on a client machine, it also starts up a small web server that will only accept requests from the local computer. This is how we intend integrators to work directly with calc. It allows integration from any language on any platform - integrators just need to implement a few web service calls and they can be sending their data in and out of calc very easily.

Currently, even though it is done in a web service style, this is only available as a client integration. Calc will still be running on the local machine, and it does not require an internet connection -- integrations will work fine in the field! But it also means that you cannot set up a single calc server somewhere to handle all of your analysis.

We based our services on the json-rpc that can be found [here](http://www.simple-is-better.org/json-rpc/jsonrpc20-schema-service-descriptor.html).  

## Calc Service

The Calc service is best thought of as a remote control for a running copy of calc. Once Calc is started, an integrating program can make basic commands that can do the same thing as a user would do in the UI. These options include:

- Opening a project.
- Saving the project.
- Generating reports.
- Running custom scripts that SPIDA has provided to the client.

## Client Data Service

The client data service allows querying of our client-specific materials libraries. This should allow data-collection type integrations to show the user the available attachments in their own interface and to select them when building a design to send to calc for analysis.

# Developer Guide

These are the Integration API descriptions for SPIDACalc. There are two types of schemas.

#### Data schemas

This describes the data format  supported by calc. It is available broken into logical units in v1/calc or as a single file for simpler validation in public/v1/calc. Example data files are in the examples directory. The format is best approached after learning basic calc functionality. All properties mirror the calc user interface pretty closely.

##### structure.schema

Schema for an individual detailed pole structure. It will define individual attachments, end points, and other physical components. This is for import from another data collection or pole design tool.

##### ~~framing_plan.schema~~

Framing Plans are no longer supported in 7.0. See input assemblies.

~~Schema for a simplified framing plan. It defines a pole design in very broad terms in code units. This tends to be a more useful way to import from a staking tool or GIS database.~~

#### input_assembly.schema

Schema for input assemblies, which allow stacking of assemblies like dropping in the graphic view.

See the [Input Assembly Guide](input_assemblies.md)

##### calc_project.schema

Schema for a calc project. Includes information on GPS positions, street addresses, photos, remedies, and design structure defined by either design.schema or framing_plan.schema. This is the format that be opened and exported directly by SPIDACalc.

#### RPC Interfaces

RPC interfaces are exposed at http://localhost:4560/ while SPIDACalc is running. They allow control over core operations of SPIDACalc from another programming running locally via basic HTTP POST requests. There is an example script using these methods in examples/scripts/example_RPC_client.coffee.

##### [client_data](/doc/apis/clientAPI.md)


Located at http://localhost:4560/clientData/<method name>. This interface provides basic querying methods for what client items are available in a client file.

##### [calc](/doc/apis/calcAPI.md)

located at http://localhost:4560/calc/<method name>. Provides stateful control methods to a running instance of SPIDACalc. Includes methods to open a file, run analysis, run a report, etc.

#### Using the examples

The example data files are json files that can be opened in any text editor.

[Example data files](/resources/examples/spidacalc)

[Example Integration showing data export to KML](/resources/examples/spidacalc/demos/kml-demo)

[Example RPC integration using coffescript](/resources/examples/spidacalc/example_RPC_client.coffee)

#### Looking around

An easy way to start playing with what is available in the SPIDACalc API is to open Calc, then open a web browser to

    http://localhost:4560/calc/getProject
    
Your web browser will show you the JSON version of the currently open project (this is where a browser extension that formats JSON is very useful for development.)

If you change something in SPIDACalc and refresh your browser, the changes will be reflected in the browser window.

#### More definition of terms

Some of the schemas use terms that are specific to SPIDACalc or the utility industry. A basic description of the values is included in the schema itself. For a more complete definition of those terms, please see the help menu in SPIDACalc.

#### External IDs

Calc stores external ids for all components on the pole. They aren't used as identifiers by the program - they are for interfacing with other applications. You may include them if you have track them, but the id field is the one that is important for building the pole.

#### Useful JSON Development tools:

- [jsonlint.com] - validates that your json is correctly formed with more useful errors.
- node_modules/JSV/examples/index.html - provides an easy to use interface for schema validation and viewing errors
- [https://chrome.google.com/webstore/detail/chklaanhfefbnpoihckbnefhakgolnmc] JSONView or similar - a browser extension that will format JSON output cleanly.


#### Limitations and known issues:

- parameters sent to RPC interface must be in the order specified in the interface description.
- ~~All ID on the structure must conform to the Calc naming conventions. All wires must be named with something starting with "Wire#", all equipment with "Equip#". This will be fixed in a later version to allow generic labeling. Correct ID Form is CASE SENSITIVE. EQUIP#1 is not a correct ID. Equip#1 is.~~ This has been fixed in Calc 5.3 ID's may be any alphanumeric string.
- ~~UUIDs must be actual UUIDs and in the canonical form xxxxxxxx-xxxx-Mxxx-Nxxx-xxxxxxxxxxxx  http://en.wikipedia.org/wiki/Universally_unique_identifier In future versions this will be more generic.~~ As of version 5.3 UUIDs have been removed. externalIds can be in any form.
- ~~Load case names are case-sensitive, to what is in the client file. This can be different from what is shown in calc.~~ This has been fixed in calc 5.3.

## Calc Project Structure

Calc uses a single pole model of analysis. It offers the ability to organize locations and to pull collected data from other locations, but it is not a connected model. All spans on a pole are defined in terms of that pole alone.

A project has leads, which have locations. Leads can represent a physical line of connected poles, but they do not have to. They can also just be a unit of organization.

A location is a single point on a map where a pole structure is located. It will have the GIS type information for that station - GPS coordinate and address, and any other metadata. A lead has many locations.

A location has many designs. A design is a specific version of the structure at a location. This allows for comparison between multiple design for quality control or remediation work. A design has a single structure.

### Load Cases and results

Calc uses load cases to handle NESC, GO95, and CSA loading standards. These are referenced by the name in the client file. To specify the load cases to be applied to a design, send analysis objects using only the ID and 
<pre>
"analysis": [
    {
        "id": "CSA - Heavy One"
    }, 
    {
        "id": "Csa Severe Two"
    }
]
</pre>
    
If Results are included, they will be listed per component. When loaded into calc they will show the summary of the result, and that the results are out of date and need to be re-analyzed to get full results.


## Calc Pole Structure

The calc structure is a model of a single pole under analysis and everything directly attached to it.

#### Attachments

Components attaching directly to the pole have structure in common

- attachHeight: The height above ground level of the highest bolt attaching this to the structure.
- direction: The bearing of the object relative to the pole.
- owner: The company who owns the attachment
- clientItem: The item in our client file describing the material properties of this attachment.

Components at a distance from the pole have structure in common

- distance: The distance from the pole to the item.
- direction: The bearing of the object relative to the pole.

#### Directions

Directions are in degrees. 0 is North, 90 is East, 180 is South, 270 is west. They are the bearing from the main pole to that item. This matches the display in Calc.

*Note* in the 4.4.2 release, there is a bug in the direction handling. The rotation is reversed. 0 is North, 270 is East, 180 is South, 90 is West.

#### Wire End Points

Calc uses the concept of wire end points to describe spans. A Wire End Point is something that the spans on your pole are going to. It could be another pole, it could be a building. It holds the distance and direction and the list of wires going to it.

Each wire has its own information on tension, material properties, attach height, and midspan.

Each span from the main pole is its own wire object, even if they are connected. So on normal line construction, with a single line, you will have two wire end points, and two wires - one running to each wire end point.

Calc has different wire end point types. The generic types are OTHER_POLE and BUILDING. There are also two special types, NEXT_POLE and PREVIOUS_POLE. These indicate the main run in the line of poles. There can only be one of each of them. Anchors whose support type is set to BISECTOR will automatically track the bisector angle between the NEXT_POLE and the PREVIOUS_POLE when edited in Calc.

#### Equipment Types

Custom equipment types are supported. However, the following types are recognized by calc as built-in:

- CAPACITOR
- CUTOUT_ARRESTOR
- DRIP_LOOP
- PRIMARY_METERING
- RECLOSER
- REGULATOR
- RISER
- STREET_LIGHT
- SWITCH
- TERMINATION_BRACKET
- TRANSFORMER
- JOINT_USE_BOX
- APPARATUS_CASE
- CROSS_CONNEC T
- LOAD_COIL_CASE
- POWER_SUPPLY
- SPLICE_CASE

## Connectivity

There are two types of connectivity in calc - connectivity between wires in the structure (Wire#1 and Wire#2, each of which describe a different span, are parts of the same physical wire) and connectivity between different designs in a design layer (NEXT in one design is PREVIOUS in another design.)

Both types of connectivity will cause changes in one Wire to cascade down all of its connections. This lets the user change a tension group or a client wire across an entire pole lead.

### Connected Wire

```
"structure": {
    "wires": [
        {
            "id": "Wire#1",
            ...
            "connectedWire": "Wire#3"
            ...
        },
        {
             "id": "Wire#3",
            ...
            "connectedWire": "Wire#1"
            ...
        }
    ]
}
```

Wire#1 and Wire#3 will now synchronize properties between the two -- if the user changes the owner, tension group, client wire, attachHeight, etc. on Wire#1, the same change will be reflected on Wire#3. Both wires must have a reference to the other or Calc will throw an error and not connect them.

### Design Connectivity

Design connectivity is maintained by a connectionId between each two *items* that are connected between designs *in the same design layer*. The connectionId must be unique per pair of items per design layer. Properties of those two connected items should match -- if they do not, behavior is undefined (one or the other may be overridden). The connectionIds generated by calc are MongoDB ObjectIDs, but it will accept any 24-character hex value.

```
"locations": [{
    "label": "Location1",
    "designs": [{
        "label": "Measured Design",
        "structure": {
        ...
        "wireEndPoints": [{
            "connectionId": "59fca1083acabca3f37689d9"
            ...
        }]
        "wires": [{
            "connectionId": "59fca1083acabca3f37689d0"
            ...
        }]
    }]
},
{
    "label": "Location2",
    "designs": [{
        "label": "Measured Design",
        "structure": {
        ...
        "wireEndPoints": [{
            "connectionId": "59fca1083acabca3f37689d9"
            ...
        }]
        "wires": [{
            "connectionId": "59fca1083acabca3f37689d0"
            ...
        }]
    }]
}]
```
If a wire end point is connected, all of its children must be connected (wires, span guys, span points). Moreover, all of the properties must match between connected items. For example, two wires representing the same span should have the same midspan, tension group, and client wire. Connected wire end points must have the same distance and opposite directions. If connectivity is incomplete or incorrect, the wire end point will be disconnected and all item properties preserved on the two disconnected designs.

[Connectivity Example](/resources/examples/spidacalc/projects/connectivity_two_locations.json)

## FAQ

#### I send my request to calc, but it says that I'm missing required parameters?

The individual parameters are passed as URL or POST FORM encoded parameters, even if the parameters themselves are JSON strings. make sure you set the following on your request:

- contenttype = application/x-www-form-urlencoded
- charset=UTF-8

This design decision was made to make it easy to test/experiment with basic requests, but it seems to cause some confusion.

#### Can I use this to run my own analysis server?

No, it is not supported by the terms of use of calc or the schema.

#### What reports are available?

The report ID is any report named in your client file, as well as two of the reports available in the calc menu: "Project Summary Report" and "Project Details Report"


#### Questions/Support

For questions about the SPIDACalc API, please submit a ticket at <https://spidasoftware.zendesk.com/hc/en-us>
