Client API
=========

Methods to allow you to retrieve client (engineering) information from your running SPIDACalc instance.  This is useful
for constructing and validating structures outside the SPIDACalc interface.

## Implementing Apps

1. calc

Methods
=======

Client Files
-----

Return the available client files to be used in many of the queries.

#### URL

`http://localhost:4560/clientData/clientFiles`

#### Allowed Methods

`GET`

#### Parameters

none

#### Returns

A `array` of client files

Anchors
-----

Return the client anchors in the client file.

#### URL

`http://localhost:4560/clientData/anchors`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. details: a `boolean` on if the complete details should be returned.

#### Returns

A `array` of client anchors

Anchor
-----

Return a client anchor from the client file.

#### URL

`http://localhost:4560/clientData/anchor`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. anchor: a _required_ `string` of the anchor name to retrieve.

#### Returns

A [client anchor](../../resources/schema/spidacalc/client/anchor.schema)

Insulators
-----

Return the client insulators in the client file.

#### URL

`http://localhost:4560/clientData/insulators`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. details: a `boolean` on if the complete details should be returned.

#### Returns

A `array` of client insulators

Insulator
-----

Return a client insulator from the client file.

#### URL

`http://localhost:4560/clientData/insulator`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. insulator: a _required_ `string` of the insulator name to retrieve.

#### Returns

A [client insulator](../../resources/schema/spidacalc/client/insulator.schema)

Poles
-----

Return the client poles in the client file.

#### URL

`http://localhost:4560/clientData/poles`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. details: a `boolean` on if the complete details should be returned.

#### Returns

A `array` of client poles

Pole
-----

Return a client pole from the client file.

#### URL

`http://localhost:4560/clientData/pole`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. pole: a _required_ [pole_reference](../../resources/schema/spidacalc/calc/client_references/pole.schema) of the pole to retrieve. {species,classOfPole,height{unit,value}}

#### Returns

A [client pole](../../resources/schema/spidacalc/client/pole.schema)

Wires
-----

Return the client wires in the client file.

#### URL

`http://localhost:4560/clientData/wires`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. industry: a `string` filter on the industry of the wire.
1. details: a `boolean` on if the complete details should be returned.

#### Returns

A `array` of client wires

Wire
-----

Return a client wire from the client file.

#### URL

`http://localhost:4560/clientData/wire`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. wire: a _required_ [wire reference](../../resources/schema/spidacalc/calc/client_references/wire.schema) of the wire to retrieve. {size,coreStrands,conductorStrands}

#### Returns

A [client wire](../../resources/schema/spidacalc/client/wire.schema)

Bundles
-----

Return the client bundles in the client file.

#### URL

`http://localhost:4560/clientData/bundles`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. details: a `boolean` on if the complete details should be returned.

#### Returns

A `array` of client bundles

Bundle
-----

Return a client bundle from the client file.

#### URL

`http://localhost:4560/clientData/bundle`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. bundle: a _required_ [bundle reference](../../resources/schema/spidacalc/calc/client_references/bundle.schema) of the bundle to retrieve. {size}

#### Returns

A [client bundle](../../resources/schema/spidacalc/client/bundle.schema)

Bundle Components
-----

Return the client bundle components in the client file.

#### URL

`http://localhost:4560/clientData/bundleComponents`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. details: a `boolean` on if the complete details should be returned.

#### Returns

A `array` of client bundle Components

Bundle Component
-----

Return a client bundle Component from the client file.

#### URL

`http://localhost:4560/clientData/bundleComponent`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. bundleComponent: a _required_ [bundle Component reference](../../resources/schema/spidacalc/calc/client_references/bundle_component.schema) of the bundle Component to retrieve. {size}

#### Returns

A [client bundle Component](../../resources/schema/spidacalc/client/bundle_component.schema)

Equipments
-----

Return the client equipments in the client file.

#### URL

`http://localhost:4560/clientData/equipments`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. industry: a `string` filter on the industry of the equipment.
1. details: a `boolean` on if the complete details should be returned.

#### Returns

A `array` of client equipments

Equipment
-----

Return a client equipment from the client file.

#### URL

`http://localhost:4560/clientData/equipment`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from 
1. equipment: a _required_ [equipment reference](../../resources/schema/spidacalc/calc/client_references/equipment.schema) of the equipment to retrieve. {size,type}

#### Returns

A [client equipment](../../resources/schema/spidacalc/client/equipment.schema)

Cross Arms
-----

Return the client crossArms in the client file.

#### URL

`http://localhost:4560/clientData/crossArms`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. details: a `boolean` on if the complete details should be returned.

#### Returns

A `array` of client crossArms

Cross Arm
-----

Return a client crossArm from the client file.

#### URL

`http://localhost:4560/clientData/crossArm`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. crossArm: a _required_ `string` of the crossArm name to retrieve.

#### Returns

A [client crossArm](../../resources/schema/spidacalc/client/crossArm.schema)

Foundations
-----

Return the client foundations in the client file.

#### URL

`http://localhost:4560/clientData/foundations`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. details: a `boolean` on if the complete details should be returned.

#### Returns

A `array` of client foundations

Foundation
-----

Return a client foundation from the client file.

#### URL

`http://localhost:4560/clientData/foundation`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. foundation: a _required_ `string` of the foundation name to retrieve.

#### Returns

A [client foundation](../../resources/schema/spidacalc/client/foundation.schema)

Sidewalk Braces
-----

Return the client sidewalkBraces in the client file.

#### URL

`http://localhost:4560/clientData/sidewalkBraces`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. details: a `boolean` on if the complete details should be returned.

#### Returns

A `array` of client sidewalkBraces

Sidewalk Brace
-----

Return a client sidewalkBrace from the client file.

#### URL

`http://localhost:4560/clientData/sidewalkBrace`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. sidewalkBrace: a _required_ `string` of the sidewalkBrace to retrieve.

#### Returns

A [client sidewalkBrace](../../resources/schema/spidacalc/client/sidewalkBrace.schema)

Load Cases
-----

Return the load cases in the client file.

#### URL

`http://localhost:4560/clientData/loadCases`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. details: a `boolean` on if the complete details should be returned.

#### Returns

A `array` of load cases


Assemblies
-----

Return the framing units in the client file.

#### URL

`http://localhost:4560/clientData/assemblies`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from
1. details: a `boolean` on if the complete details should be returned.
1. assemblyType: an _optional_ 'string' of the assembly type (FRAMING or SUPPORT)

#### Returns

A `array` of assemblies


Scripts
-----

Return the scripts in the client file.

#### URL

`http://localhost:4560/clientData/scripts`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from

#### Returns

A `array` of scripts

Report
-----

Return the reports in the client file.

#### URL

`http://localhost:4560/clientData/reports`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from

#### Returns

A `array` of reports


Owners
-----

Return the owners in the client file.

#### URL

`http://localhost:4560/clientData/owners`

#### Allowed Methods

`GET`

#### Parameters

1. clientFile: a _required_ `string` of the client file name to pull from

#### Returns

A `array` of owners
