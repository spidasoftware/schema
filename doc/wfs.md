# WFS

These are the features exposed by SPIDAdb

## Location
A location is single physical location that has a pole

| Property              | Description                                            |
|-----------------------|--------------------------------------------------------|
| Geographic Coordinate | lat, lng                                               |
| Id                    | location id                                            |
| Name                  | the name/label of the location                         |
| Status                | ACTIVE, INACTIVE, or NONE                              |
| Latitude              | location latitude                                      |
| Longitude             | location longitude                                     |
| Project Id            | parent project id                                      |
| Company Id            | company id in usersmaster                              |
| Project Name          | parent project name                                    |
| Source Project Id     | the original project id if active or inactive          |
| Source Project Name   | the original project name if active or inactive        |
| Client File           | calc client file name                                  |
| Client File Version   | the original full client file hash                     |
| Date Modified         | date this location data was modified                   |
| Comments              | any comments for this location                         |
| Street Number         | house number on street for address                     |
| Street                | street for address                                     |
| City                  | city for address                                       |
| County                | county for address                                     |
| State                 | state for address                                      |
| Zip Code              | zip code for address                                   |
| Technician            | technician who did work for this location              |


## Form 
a custom form attached to the location

| Property    | Description                             |
|-------------|-----------------------------------------|
| Location Id | the id of the location this form is on  |
| Id          | this form's id                          |
| Template    | the form template id                    |
| Title       | the name of this form                   |



## FormField
a custom field attached to a custom form

| Property   | Description                              |
|------------|------------------------------------------|
| Form Id    | this id of the form this field is on     |
| Name       | the name of the field                    |
| Value      | the value entered by the user            |
| Group Name | the name of the group this field is in   |



## Remedy
the work to be done at this location

| Property    | Description                          |
|-------------|--------------------------------------|
| Location Id | the parent location id               |
| Value       | the text of the rememdy content      |


## PoleTag
list of physical tags on the pole

| Property    | Description                       |
|-------------|-----------------------------------|
| Location Id | the parent location id            |
| Type        | field, map, or foreign            |
| Value       | the actual value on the tag       |


## SummaryNote
a note about this location

| Property    | Description                  |
|-------------|------------------------------|
| Location Id | the parent location id       |
| Value       | the actual value of the note |


## Pole
contains the properties of the pole itself

| Property            | Description                                                |
|---------------------|------------------------------------------------------------|
| Design Layer Name   | the name of the design layer                               |
| Design Layer Type   | the type of design layer                                   |
| Company Id          | the id of the company in usersmaster                       |
| Location Id         | the id of the parent location                              |
| Location Name       | the name of the parent location                            |
| Status              | ACTIVE, INACTIVE, or NONE                                  |
| Client File         | calc client file name                                      |
| Client File Version | the original full client file hash                         |
| Date Modified       | date this pole data was modified                           |
| Analysis Current    | true or false - true if no data was changed after analysis |
| Result Id           | the id of the result object                                |
| Glc                 | the ground line circumference of the base of the pole      |
| Glc Unit            | the unit of the glc value                                  |
| Agl                 | height above ground level                                  |
| Agl Unit            | the unit of the agl value                                  |
| Species             | the species of the wood of the pole                        |
| Class               | the class of pole                                          |
| Length              | the length of the whole pole                               |
| Length Unit         | the unit of the length value                               |
| Owner               | the name of the pole owner                                 |
| Id                  | the id of this pole/design                                 |


## Analysis
properties of analysis and results

| Property            | Description                                                |
|---------------------|------------------------------------------------------------|
| Design Layer Name   | the name of the design layer                               |
| Design Layer Type   | the type of design layer                                   |
| Result Id           | the id of the result object                                |
| Company Id          | the id of the company in usersmaster                       |
| Location Id         | the id of the parent location                              |
| Location Name       | the name of the parent location                            |
| Status              | ACTIVE, INACTIVE, or NONE                                  |
| Client File         | calc client file name                                      |
| Client File Version | the original full client file hash                         |
| Date Modified       | date this analysis data was modified                       |
| Analysis Current    | true or false - true if no data was changed after analysis |
| Pole Id             | the id of the pole                                         |
| Load Info           | the load case name                                         |
| Id                  | the id of the analysis                                     |
| Actual              | S.F. or percent value                                      |
| Allowable           | allowable S.F. or percent value                            |
| Unit                | S.F. or percent                                            |
| Analysis Date       | date of analysis                                           |
| Component           | the id of the item analyzed                                |
| Analysis Type       | the type of analysis done ie stress                        |
| Passes              | true or false - true if passes                             |


## WireEndPoint
the pole or building wires connect to

| Property         | Description                                                                            |
|------------------|----------------------------------------------------------------------------------------|
| Pole Id          | The pole id                                                                            |
| Distance         | The distance from the pole                                                             |
| Distance Unit    | The unit of the distance property                                                      |
| Direction        | The direction around the pole                                                          |
| Inclination      | degrees of inclination that the base of the pole is to the base of this wire end point |
| Inclination Unit | the unit of the inclination value                                                      |
| Type             | NEXT_POLE, PREVIOUS_POLE, OTHER_POLE, or BUILDING                                      |
| Comments         | any comments about this wire end point                                                 |


## WirePointLoad
additional load applied to wires

| Property      | Description                                   |
|---------------|-----------------------------------------------|
| PoleId        | the id of the pole this is on                 |
| Owner         | the name of the owner of this wire point load |
| Distance      | The distance from the pole                    |
| Distance Unit | The unit of the distance property             |
| Wire          | the id of the wire this is attached to        |
| X Force       | x component of force                          |
| X Force Unit  | the unit of the x force value                 |
| Y Force       | y component of force                          |
| Y Force Unit  | the unit of the y force value                 |
| Z Force       | z component of force                          |
| Z Force Unit  | the unit of the z force value                 |


## SidewalkBrace
an item representing a physical brace between a pole and a guy wire

| Property               | Description                                                         |
|------------------------|---------------------------------------------------------------------|
| Pole Id                | the id of the pole this is on                                       |
| Owner                  | the name of the owner of this sidewalk brace                        |
| Attachment Height      | the distance from the ground up to the point of attachment          |
| Attachment Height Unit | the unit of the attachment height value                             |
| Size                   | a reference to the client item                                      |
| Length                 | the length of the brace from the pole to the end                    |
| Length Unit            | the unit of the length value                                        |
| Direction              | The direction around the pole                                       |


## Wire
an item representing a physical wire on a pole

| Property               | Description                                                                                     |
|------------------------|-------------------------------------------------------------------------------------------------|
| PoleId                 | the id of the pole this is on                                                                   |
| Owner                  | the name of the owner of this wire                                                              |
| Size                   | the name of the wire                                                                            |
| Core Strands           | the number of supportive strands in the wire                                                    |
| Conductor Strands      | the number of conductive strands in the wire                                                    |
| Attachment Height      | the distance from the ground up to the point of attachment                                      |
| Attachment Height Unit | the unit of the attachment height value                                                         |
| Usage Group            | the usage of the wire ie primary, neutral, secondary...                                         |
| Tension Group          | the name of the group defined in the client file                                                |
| Midspan Height         | the distance from the ground to the wire at midspan                                             |
| Midspan Height Unit    | the unit of the midspan height value                                                            |
| Tension Adjustment     | additional wire-specific tension adjustment number in addition to load case tension multipliers |


## Guy
an item representing a physical guy wire on a pole

| Property               | Description                                                |
|------------------------|------------------------------------------------------------|
| Pole Id                | the id of the pole this is on                              |
| Owner                  | the name of the owner of this guy                          |
| Size                   | the name of the guy wire                                   |
| Core Strands           | the number of supportive strands in the guy wire           |
| Conductor Strands      | the number of conductive strands in the guy wire           |
| Attachment Height      | the distance from the ground up to the point of attachment |
| Attachment Height Unit | the unit of the attachment height value                    |


## Assembly
represents a set of items to place on a pole

| Property| Description                                  |
|---------|----------------------------------------------|
| Pole Id | the id of the pole this is on                |
| Code    | the code/name/alias of this assembly         |


## Foundation
represents the foundation the pole is in

| Property| Description                                          |
|---------|------------------------------------------------------|
| Pole Id | the id of the pole this is on                        |
| Name    | the name of the client item found in the client file |


## Insulator
an item representing a physical insulator on a pole

| Property               | Description                                                                                                           |
|------------------------|-----------------------------------------------------------------------------------------------------------------------|
| PoleId                 | the id of the pole this is on                                                                                         |
| Owner                  | the name of the owner of this insulator                                                                               |
| Size                   | the name of the client item found in the client file                                                                  |
| Attachment Height      | the distance from the ground up to the point of attachment                                                            |
| Attachment Height Unit | the unit of the attachment height value                                                                               |
| Offset                 | If on pole, then offest is attach height. If on cross arm, then offset is the distance from one end of the cross arm. |
| Offset Unit            | the unit of the offset value                                                                                          |
| Direction              | the direction of the insulator                                                                                        |
| Double Insulator       | true or false - true if it is a double                                                                                |


## CrossArm
an item representing a physical crossarm on a pole

| Property               | Description                                                                                                 |
|------------------------|-------------------------------------------------------------------------------------------------------------|
| Pole Id                | the id of the pole this is on                                                                               |
| Owner                  | the name of the owner of this crossarm                                                                      |
| Size                   | the name of the client item found in the client file                                                        |
| Attachment Height      | the distance from the ground up to the point of attachment                                                  |
| Attachment Height Unit | the unit of the attachment height value                                                                     |
| Offset                 | the offset along the pole. 0 Offset will result in a crossarm attached to the pole at one end.              |
| Offset Unit            | the unit of the offset value                                                                                |
| Direction              | Crossarm bearing is the direction the end of the crossarm points, not the direction of the bolt attachment. |


## PushBrace
an item representing a physical pushbrace on a pole

| Property               | Description                                                |
|------------------------|------------------------------------------------------------|
| Pole Id                | the id of the pole this is on                              |
| Owner                  | the name of the owner of this push brace                   |
| Attachment Height      | the distance from the ground up to the point of attachment |
| Attachment Height Unit | the unit of the attachment height value                    |
| Glc                    | the ground line circumference of the base of the pole      |
| Glc Unit               | the unit of the glc value                                  |
| Distance               | distance from the pole                                     |
| Distance Unit          | The unit of the distance property                          |
| Direction              | the direction of the push brace around the pole            |
| Species                | the species of the wood of the pole                        |


## SpanGuy
an item representing a physical span guy between two poles

| Property               | Description                                                |
|------------------------|------------------------------------------------------------|
| Pole Id                | the id of the pole this is on                              |
| Owner                  | the name of the owner of this span guy                     |
| Size                   | the name of the client item found in the client file       |
| Core Strands           | the number of supportive strands in the guy wire           |
| Conductor Strands      | the number of conductive strands in the guy wire           |
| Attachment Height      | the distance from the ground up to the point of attachment |
| Attachment Height Unit | the unit of the attachment height value                    |
| Midspan Height         | the distance from the ground to the span guy at midspan    |
| Midspan Height Unit    | the unit of the midspan height value                       |
| Height                 | the height of the other end of the span guy                |
| Height Unit            | the unit of the height value                               |


## PointLoad
additional load applied to the pole

| Property           | Description                                                |
|--------------------|------------------------------------------------------------|
| Pole Id            | the id of the pole this is on                              |
| Owner              | the name of the owner of this point load                   |
| Attach Height      | the distance from the ground up to the point of attachment |
| Attach Height Unit | the unit of the attachment height value                    |
| X Force            | force in the x direction                                   |
| X Force Unit       | the unit of the x force value                              |
| Y Force            | force in the y direction                                   |
| Y Force Unit       | the unit of the y force value                              |
| Z Force            | force in the z direction                                   |
| Z Force Unit       | the unit of the z force value                              |


## SpanPoint
holds additional data at one point along a wire

| Property      | Description                                                |
|---------------|------------------------------------------------------------|
| Pole Id       | the id of the pole this is on                              |
| Distance      | the distance from the pole                                 |
| Distance Unit | the unit of the distance value                             |
| Environment   | type of environment the point is in ie street, railroad... |


## Anchor
an item representing a physical anchor in the ground near a pole

| Property      | Description                                                      |
|---------------|------------------------------------------------------------------|
| Pole Id       | the id of the pole this is on                                    |
| Distance      | The distance from the pole                                       |
| Distance Unit | The unit of the distance property                                |
| Direction     | the direction of the anchor around the pole                      |
| Owner         | the name of the owner of this anchor                             |
| Height        | the height of the other end of the anchor                        |
| Height Unit   | the unit of the height value                                     |
| Size          | the name of the client item found in the client file             |


## NotePoint
additional notes attached to a specific place in the structure

| Property      | Description                                                    |
|---------------|----------------------------------------------------------------|
| PoleId        | the id of the pole this is on                                  |
| Distance      | The distance from the pole                                     |
| Distance Unit | The unit of the distance property                              |
| Direction     | the direction of the note point around the pole                |
| Note          | the actual note value                                          |
| Height        | the height above ground that this note refers to               |
| Height Unit   | the unit of the height value                                   |


## Equipment
an item representing a physical piece of equipment on a pole

| Property               | Description                                                     |
|------------------------|-----------------------------------------------------------------|
| Pole Id                | the id of the pole this is on                                   |
| Owner                  | the name of the owner of this equipment                         |
| Size                   | the name of the client item found in the client file            |
| Type                   | transformer, street light...                                    |
| Attachment Height      | the distance from the ground up to the point of attachment      |
| Attachment Height Unit | the unit of the attachment height value                         |
| Bottom Height          | the distance from the ground up to the bottom ot the attachment |
| Bottom Height Unit     | the unit of the bottom height value                             |
| Direction              | the direction of the equipment around the pole                  |


## Damage
an item representing physical damage on a pole

| Property             | Description                                                  |
|----------------------|--------------------------------------------------------------|
| Pole Id              | the id of the pole this is on                                |
| Attach Height        | the distance from the ground to the top of the damage        |
| Attach Height Unit   | the unit of the attach height value                          |
| Damage Height        | height of the damage itself                                  |
| Damage Height Unit   | the unit of the damage height value                          |
| Type                 | the type of damage                                           |
| Width                | the width of the damage                                      |
| Width Unit           | the unit of the width value                                  |
| Direction            | the direction of the damage around the pole                  |
| Arc                  | the value of the arc of the specific damage type             |
| Arc Unit             | the unit of the arc value                                    |
| Depth                | the value of the depth of the specific damage type           |
| Depth Unit           | the unit of the depth value                                  |
| Circumference        | the value of the circumference of the specific damage type   |
| Circumference Unit   | the unit of the circumference value                          |
| Shell Thickness      | the value of the shell thickness of the specific damage type |
| Shell Thickness Unit | the unit of the shell thickness value                        |
| Neck Depth           | the value of the neck depth of the specific damage type      |
| Neck Depth Unit      | the unit of the neck depth value                             |
| Neck Diameter        | the value of the neck diameter of the specific damage type   |
| Neck Diameter Unit   | the unit of the neck diameter value                          |
| Neck Offset          | the value of the neck offset of the specific damage type     |
| Neck Offset Unit     | the unit of the neck offset value                            |
| Nest Depth           | the value of the nest depth of the specific damage type      |
| Nest Depth Unit      | the unit of the nest depth value                             |

