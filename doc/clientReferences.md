# Client Item References / Keys

## Overview

Each client item can be referenced multiple ways in a structure's components.

1. `clientItem` -- This references the named, human-readable key(s) for each client item. In many cases this a composite key. This corresponds to the "Identification" section of the client editor views.
2. `clientItemAlias` -- These are string aliases of a client item for mapping from another system. A client item may have 0-N aliases.
3. `clientItemVersion` -- This is the Hash of the sorted JSON of the client item. It maps to a *specific version* of a client item. This information is not written in the client items json, but is generated from it by sorting the keys and creating an MD5 hash of the JSON. This is generally only used by SPIDA products.


## SPIDAcalc Handling on Open

When SPIDAcalc loads a structure, it searches the client data for the provided client item in the following order:

1. By `clientItemVersion`. If it finds an exact match, it knows it has found exactly the same client data properties for that client item as the structure was originally saved with. If `clientItemVersion` is not specified, or the specified `clientItemVersion` not found, the code proceeds to the next lookup.
2. By `clientItem`. This looks for a client item of the appropriate type, with the smae matching composite key. This lookup indicates that the specific client item was known at the time for file creation, but the material properties may not have been. This lookup will use whatever material properties are found under that key in the client file. If both `clientItem` and `clientItemVersion` are specified but only the `clientItem` was found in the client data, the user will be informed that the client data has been updated since the project was saved. If `clientItem` is not specified, or the specified `clientItem` was not found, the code proceeds to the next lookup.
3. By `clientItemAlias`. This will search aliases in the client file. This should be used when there is a name for the component in the third-party application that can be configured as an alias as part of the client deployment.

If a client item cannot be identified, depending on the context, either the user will be asked to "map" to a different, compatible client item from the provided client data (SPIDAcalc desktop client), or the project/structure will fail to open (CEE analysis, SPIDAstudio project upload). The user will be shown the user-readable information provided when asked to map -- either the ClientItem or the Alias, generally. This mapping will then be applied to the entire project, for all components referencing that same client item.

This can be used to offload the choice of client items to the SPIDAcalc interface (for example, setting all primaries to a generic "DEFAULT_PRIMARY" client alias, but it is often less than ideal, as the user has lost the context to make these decisions or the ability to make different decisions at different designs or components in the project. The designs will also be unable to be analyzed on SPIDAcee without a user first opening them in SPIDAcalc. If this rough level of information is all that is available in the integrating product, it is better to use client aliases to the give the customer the option at configuration time to set a "DEFAULT_PRIMARY" alias to the specific wire and tension group they want to use when there is no more detailed information available.

In general, `clientItemAlias` should be used if the integrating application already has a store of available components. `clientItem` should be used if the application is pulling potential client items from the client data for user selection, but still generating a "bare" project with no embedded client data. `clientItemVersion` is used by SPIDA products for client data versioning, and is generally beyond the scope requirements of third party integrations.

## Primary Keys and standard display string for all client item types

Please see the individual schema files for type information for these fields.

### Anchor

- size

`"$size"`

`"1/4" Helix"`

### Crossarm

- size

`"$size"`

`"8 Foot Wood Crossarm"`

### Insulator

- size

`"$size"`

`"50kV Dead Endf"`

### Brace

- size

`"$size"`

`"1" O.D Steel"`

### Foundation

- name

`"$name"`

`"Good Soil"`

### Wire

- size
- coreStrands
- conductorStrands

`"$size ($conductorStrands/$coreStrand)"`

`"1/0 AAC (7/0)"`

### Pole

- species
- classOfPole
- height

`"$height-$classOfPole $species"`

`"40'-3 Southern Pine"`

### Equipment

- type
- size

`"$type, $size"`

`"TRANSFORMER, 50kV"`

### Assembly

- code

`"$code"`

`"C1.11"`

## Bundles

- size

`"$size"`

`"1/4" Messenger 3 GX Fiber"`

*The majority of integrations will not deal with bundles. Bundles, unless specified by alias, must include all bundle components in the wire's `clientItem`. This is to deal with the fact that bundles can be created and on the fly in SPIDAcalc, as opposed to only being pre-configured in the client data. Look at the bundle examples or generate one yourself in SPIDAcalc to get a better sense of this process. *
