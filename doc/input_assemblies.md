Input Assembly / Staker Style Integration Guide
===========================================
## Introduction

**Assemblies** are SPIDAcalc's mechanism for handling Construction Standards/Common Units. Like almost all SPIDAcalc components, they are divided into a **Client Assembly**, which defines the standard, and an **Assembly**, which is the instance of the standard on a structure. **Client Assemblies** can be further divided into two types - a **Framing Assembly** is a pole-head type standard, and can be anything from a crossarm assembly with wires to a single piece of equipment. a **Support Assembly** is a configuration of support elements - guys, anchors, push braces, etc - which are applied as a single unit.

**Client Assemblies** are configured ahead of time by SPIDA or by the client's standards department along with the rest of the SPIDAcalc **Client Data**. For an integration to function properly, this configuration will need to be kept in sync with the integrating programs database of available Common Units. That process is usually manual and should be part of the planning for any deployment of an integrated solution.

The general model SPIDAcalc uses to describe structures is one of many individual components (Crossarms, insulators, guys, etc). From SPIDAcalc's perspective, an **assembly** is a collection of those components.

### On Aliases
All SPIDAcalc **Client Items** have their primary key that is internal to SPIDA, which may in some cases be a compound key. For **client assemblies**, the primary key is a simple "code" string, which can be matched the integrator's Common Units. However, all **Client Items** also support multiple **Aliases**, which are simple string keys. These can allow any **client item**, including **client assemblies** to be matched to a number of names used in the integrator's system.


## Standard Integration Process and Development Flow
### 1 - Calc Project Creation
SPIDA recommends that all integrations first develop the ability to write to a SPIDAcalc Exchange project. This project will contain all of the structures generated from the integrating application. The file can be opened directly in SPIDAcalc by the user, by Windows file associations, or using the SPIDAcalc REST API. This will not only allow the integrating test team to view, validate, and troubleshoot their output, but it will also allow users to do the same and learn to trust the quality of the integration.

### 2 - Pulling Results from Calc
The integrating tool opens an Exchange project that a user has analyzed in SPIDAcalc, or pulls it from the REST API, and displays some summary information from the results that SPIDAcalc has saved in that project. [Results Guide](results.md)

### 3 (optional)- Direct CEE Integration using CEE-CLI
Once the user is convinced of the quality of the data produced from the integration, many integrators want to be able to launch analysis directly from with their application. [cee-cli] (cee-cli) is a command line utility provided by SPIDA to assist with the process of using SPIDA's cloud analysis service. This is a separate code path from integrating with SPIDAcalc, but it uses the same Exchange format for the structures analyzed. The tool will return the same results included in the analyzed Exchange file.

### 4 (optional) - Updating Staking Tool Model from Exchange
The Exchange file will include any changes made by the user once the file was opened in SPIDAcalc - for example, adding guying or changing the pole class. The level of changes that should be supported by the integration/ that the user should be encouraged to do will depend on the specific needs of the client deployment, but through the use of the **externalId** field, integrators can match SPIDAcalc components to the components in their own model and make any necessary changes to keep the two systems synchronized. Deployments can require that all changes be made inside of the staking application, but for some users that may result in an unpleasantly slow or manual iteration loop when a design fails analysis.

## Input Assemblies vs Final Assemblies

Both of these go into the **assemblies** field in a structure. Each one describes an instance of an **Assembly** on that structure. There can be multiple assemblies on a single structure.

**Input Assemblies** are produced by integrators, and mean "Add this Client Assembly to the structure." They are equivalent to dragging and dropping an assembly to one of the views within SPIDAcalc. The fields available in **Input Assemblies** map directly to the choices made by the user when dragging and dropping. Loading an **input assembly** into SPIDAcalc will result in all of the components that make up that assembly (Crossarms, Insulators, etc) being added to the structure.

[Input Assembly Schema](../resources/schema/spidacalc/calc/input_assembly.schema)

**Final Assemblies** are produced by SPIDAcalc. These are the only **Assemblies** that will ever be found in an Exchange file from SPIDAcalc. They mean "An Assembly exists on this structure and these are the components that it describes." Loading a **final assembly** into SPIDAcalc will not add any new components. The fields in a **final assembly** are what **Client Assembly** it represents, and which components on the structure are part of it.

[Final Assembly Schema](../resources/schema/spidacalc/calc/assembly.schema)

So the general flow of staking type integration is for the Staker to write structures with **Input Assemblies** based on their knowledge of the Common Units on the structure. SPIDAcalc will combine that with the construction standards stored in the Client File to produce a structure with many components grouped into **Final Assemblies**. 

**Input Assemblies** can be mixed with normal SPIDAcalc components, if the staking application needs to add a specific component in an ad hoc fashion that is not linked to a Common Unit.

## Input Assembly Fields and Logic
- **clientItem** - The code or alias of the **client assembly** AKA your Common Unit. 
- **owner** - This owner will be applied to all components created by this input assembly.
- **attachHeight** - The height of the highest component. If this is not defined, SPIDAcalc will apply its stacking logic as if the assembly were dropped onto Top View.
- **direction** - The direction of the assembly. This is mostly relevant for assemblies without wires. If this is not defined, SPIDAcalc will treat the direction as though the assembly were dropped on Side View.
- **wire end points** - Wires in this assembly will go these wire end points. This is equivalent to the selected wire end points in the UI when an assembly is dropped. The number of Wire End Points specified must match the number of Wire End Points defined in the Client Assembly.
  - **id** - Which wire end point these wires will go to.
  - **wires** - A map of SPIDAcalc [usage group](../resources/schema/spidacalc/calc/enums/usage_group.schema) to wire alias. This matches the wire selection in the UI. Note that in the UI, these choices apply to all wire end points, but in the **Input Assemblies** wires must be specified separately per wire end point to allow for double dead ends with tension group changes.
- **support** - This defines anchors, guys, or **support assemblies**. Multiple support elements will be applied to available **Guy Attach Points** in the assembly in a top down order.
  - **supportItem** - A Client Anchor alias, code or alias for a **support assembly** containing an anchor, or WEP ID (in the case of span guys.
    - **distance** - Optionally provide the distance of the anchor from the pole, or SPIDAcalc will use the default as if it were dropped in Side View.
    - **direction** - Optionally provide the direction of the anchor, or SPIDAcalc will use the default based on the configuration of the Guy Attach Point in the **client assembly**.
    - **attachments** - The list of guys or support assemblies attached to the **support**
      - **attachmentItem** - A guy alias, or the code or alias for a **support assembly** containing only guys, that are attached to this anchor.

### Guy Attach Points
**Client Assemblies** can be configured with set attach points for guying. These can be configured to support specific wire end points or the bisector at specific heights relative to the top of the assembly. Support elements added in input assemblies (or in the UI) are added to guy attach points in a top down order, and take their direction from those Guy Attach Points unless an override is specified.

If the integrating tool needs to set guy attach heights specifically (ignoring the Guy Attach Point configuration), then the guys and anchors can be added as separate components.

If the integrator wants to take advantage of guy attach point configuration, guying must be specified *per input assembly*. 

## Stacking Logic
If no attach height is provided, **Input Assemblies** will attempt to stack in order from top of the pole to the bottom. The first assembly will use the **Distance from Pole Top** value. The next assembly will go underneathe that assembly, and use the previous assembly's **Distance to Underbuild** value. These values are pre-configured in the **client file**. Neutrals in the overbuild assemblies will automatically be lowered to match those in the underbuild assembly.

## Wire Tensions
Wire aliases are configured to point to a specific **Tension Group** in the client editor. By using wire aliases, the integration is specifying both wire type and which tension should be applied to that wire.

## Matching Components Between SPIDAcalc and the Integrator
The **externalId** field is provided to match components from the integrating software with the components generated in SPIDAcalc. **ExternaIds** will be preserved and not modified by SPIDAcalc.

In the case of **input assemblies**, the **externalId** will be applied to all components generated by that **input assembly**. So if a **final assembly**, a crossarm, four insulators, and eight wires are added to the structure as a result of an **input assembly**, all of those will have that provided **externalId** when the project is pulled back out of SPIDAcalc.

## Connectivity
Input assemblies make connectivity between different designs much simpler. The integrator does not need to specify Wire connectivity, only [design connectivity between Wire End Points](calc.md#design-connectivity). SPIDAcalc will generate appropriate connectivity for the wires going between poles equivalent to dragging and dropping assemblies within SPIDAcalc.

### Connectivity Requirements with Input Assemblies
- Connected designs must have matching numbers and types of wires in the connected span. For example, if Pole A and Pole B are connected, and Pole A has a three-phase assembly while Pole B has a single phase assembly, that would be invalid.
- Connected designs must have matching client wires and tension groups for the span. If Pole A has 1/0 ACSR primaries and Pole B has 2/0 ASCR primaries the import will fail. 

# Transitioning from a Framing Plan based integration

# Summary

Assemblies are a complete replacement for framings. Framing Planner has been removed entirely. The old Framing Plan integration will no longer function in Calc 7. Framing configurations in Client Data will be automatically converted to assemblies.

## Framing Plans

- Simpler than sending structure
- No control over guy placement.
- Hard to make specific adjustments.
- Completely parallel system to Calc structure editing.
- Required duplicate configuration for "Framing Wire" and "Client Wire" and similar.

## Assemblies

- Integrated with Calc structure editing.
- Optional control over order, attach height, direction, wire end points.
- More options (e.g. communication AND power, guys and anchors together).
- Less "Magic."
- Slightly more complicated than Framing Plans.

### Framing Assemblies

- Any combination of attachments that generate a load (crossarms, insulators, wires, equipment)
- Configurable to line angle and stacking properties in the client editor.
- Can contain Guy Attach Points to define guy associations/heights.

### Support Assemblies

- Any combination of supporting components (anchors, guys, push braces).
- Allows specification of guy/anchor combinations.
- Stores default anchor distances and relative directions.

### Aliases

- An alias is a way to refer to a client item by a single string, e.g. “1/0 ACSR - S.”
- All Client Items have aliases.
- Replace Framing Wire, Framing Insulator, Framing Crossarm, etc.
- Client Wire aliases are to a specific tension group.
- *7.1 Limitation* Output structure will only refer to the first client alias as which alias was used is inputnot tracked.

### Input Assemblies (Think 'Framing Plan')

Creates new components on a structure. Applies framing positioning based on same logic as dropping in top or side view.

### Final Assemblies (Think 'Structure')

Links a list of components to a client assembly. Describes components already existing in the structure.

Calc will read both Input and Final assemblies, but always output Final assemblies.

## Input Assembly Structure

- Input assemblies are now part of the structure instead of in a separate framing plan.
- Wire End Points are defined using the normal structure format, and may be referenced by input assemblies.
- The Pole is defined using the normal structure format.
- Other attachments can be defined using the normal structure format in addition to input assemblies.
- Input assemblies will then be applied/stacked in order onto this structure just as if they were added in the UI.

### Input Assembly Properties

[Input Assembly Schema](../resources/schema/spidacalc/calc/input_assembly.schema)

[Multiple Examples](../resources/examples/spidacalc/projects/input-assembly.json)

[Simpler Example with Demo Client File](../resources/examples/spidacalc/projects/input_assembly_c5.json)

### Required Changes from Framing Plan

- Pole and Wire End Points are defined in structure.
- Support Assemblies must be associated with a Framing Assembly -- Calc no longer guesses at the best position.
- Wires are defined per usage group, per Wire End Point, per Assembly
- Assemblies should be provided in top down order.

### Final Assembly Properties

[Final Assembly Schema)](../resources/schema/spidacalc/calc/assembly.schema)

[Example Final Assembly](../resources/examples/spidacalc/projects/project-with-assemblies.json)
