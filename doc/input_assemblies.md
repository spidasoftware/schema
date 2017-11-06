Framing Plan to Input Assembly Upgrade Guide
===========================================

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

(Input Assembly Schema)[../resources/schema/spidacalc/calc/input_assembly.schema]

(Multiple Examples)[../resources/examples/spidacalc/projects/input-assembly.json]

(Simpler Example with Demo Client File)[../resources/examples/spidacalc/projects/input_assembly_c5.json]

### Required Changes from Framing Plan

- Pole and Wire End Points are defined in structure.
- Support Assemblies must be associated with a Framing Assembly -- Calc no longer guesses at the best position.
- Wires are defined per usage group, per Wire End Point, per Assembly
- Assemblies should be provided in top down order.

### Final Assembly Properties

(Final Assembly Schema)[../resources/schema/spidacalc/calc/assembly.schema]

(Example Final Assembly)[../resources/examples/spidacalc/projects/project-with-assemblies.json]
