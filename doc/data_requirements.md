# Data Requirements

Materials/engineering information is pre-configured per client in their Client Data file. Broadly speaking, the information required from an integrator to be able to perform pole loading is:

- What type of components are in the structure (Pole height and class, which units are attached to the pole)
- Where are those components (next and previous pole distance and direction, attach heights and directions)
- What are the wire tensions (This will be choosing from pre-configured named options in the client file)

A good integration will also potentially include geolocations, photos, and span connectivity information.

## Basic Location Information

A minimal SPIDAcalc file consists of a list of named locations. This will import into SPIDAcalc with default poles based on the client file chosen. This is similar to the CSV import functionality.

In some use cases, an integration that brings in locations, lat/lon, and photos will be a useful time savings for customers. The user will be able to build the structures using the SPIDAcalc interface. If the photos can be annotated with any additional data (such as attach heights) this may be a useful workflow, and is certainly a good starting point for most integrations.

## Input Assemblies

The simplest integration method is using input assemblies. In this case, the integrating application will need to supply the following information:

- The pole species, height and class
- The type of pole top assembly
- The location of adjacent poles that the wires in that assembly go to (in distance, direction, and elevation)
- The type of wires in the assembly and their tension group
- The number and types of down guys and anchors, and which assemblies they support.

All component names will need to match those pre-configured in the client data. SPIDAcalc supports aliasing for these components, allowing systems to refer to them by their own names as long as configuration is kept in sync.

## Full Structure

A more complete and flexible integration approach is to specify every attachment on the pole individually. In this case, the integrating application will need to supply:

- The pole species, height and class
- The locations of adjacent poles that wires or span guys go to (in distance, direction, and elevation)
- For each attachment
  - The type of component (referencing the client data)
  - The attach height and direction
  - All supporting connection points and relationships (e.g., a wire is attached to an insulator which is attached to a crossarm. The wire also goes to the Next Pole, and is connected to a wire going to the Previous pole)
- The tension groups for each wire in the design
- (Optional, but preferred) - connectivity information for adjacent designs in the project, including mapping each individual "equivalent" wire in each design.

## Sources for this data
- The integrating application can read the client data to see available components and assemblies.
- Client GIS systems may have pole lat/lon, names, and pole length and class information. They occasionally have common unit information that could be used to generate input assemblies with some assumptions. They never have full structure information.

## Importance of Completeness
Without properly identifying attachments, pole loading results will rarely be useful.

Highest Priorities for Accurate Loading 
- Pole species, height, and class
- Span geometry
- Wire type and tensions
- Guying type and geometry


The importance of some smaller details, like choosing the correct insulator, will depend on the needs of the individual client and on the specific design. A heavy piece of equipment or a large crossarm may matter, but a lighter one might not. Some clients analyze crossarms and insulators. Some only analyze poles, guys, and anchors.
