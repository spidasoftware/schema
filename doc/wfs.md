# WFS

These are the features exposed by SPIDAdb

| Insulator |                        | an item representing a physical insulator on a pole                                                                   |
|-----------|------------------------|-----------------------------------------------------------------------------------------------------------------------|
|           | PoleId                 | the id of the pole this is on                                                                                         |
|           | Owner                  | the name of the owner of this insulator                                                                               |
|           | Size                   | the name of the client item found in the client file                                                                  |
|           | Attachment Height      | the distance from the ground up to the point of attachment                                                            |
|           | Attachment Height Unit | the unit of the attachment height value                                                                               |
|           | Offset                 | If on pole, then offest is attach height. If on cross arm, then offset is the distance from one end of the cross arm. |
|           | Offset Unit            | the unit of the offset value                                                                                          |
|           | Direction              | the direction of the insulator                                                                                        |
|           | Double Insulator       | true or false - true if it is a double                                                                                |
| CrossArm  |                        | an item representing a physical crossarm on a pole                                                                    |
|           | Pole Id                | the id of the pole this is on                                                                                         |
|           | Owner                  | the name of the owner of this crossarm                                                                                |
|           | Size                   | the name of the client item found in the client file                                                                  |
|           | Attachment Height      | the distance from the ground up to the point of attachment                                                            |
|           | Attachment Height Unit | the unit of the attachment height value                                                                               |
|           | Offset                 | the offset along the pole. 0 Offset will result in a crossarm attached to the pole at one end.                        |
|           | Offset Unit            | the unit of the offset value                                                                                          |
|           | Direction              | Crossarm bearing is the direction the end of the crossarm points, not the direction of the bolt attachment.           |
| PushBrace |                        | an item representing a physical pushbrace on a pole                                                                   |
|           | Pole Id                | the id of the pole this is on                                                                                         |
|           | Owner                  | the name of the owner of this push brace                                                                              |
|           | Attachment Height      | the distance from the ground up to the point of attachment                                                            |
|           | Attachment Height Unit | the unit of the attachment height value                                                                               |
|           | Glc                    | the ground line circumference of the base of the pole                                                                 |
|           | Glc Unit               | the unit of the glc value                                                                                             |
|           | Distance               | distance from the pole                                                                                                |
|           | Distance Unit          | The unit of the distance property                                                                                     |
|           | Direction              | the direction of the push brace around the pole                                                                       |
|           | Species                | the species of the wood of the pole                                                                                   |
| SpanGuy   |                        | an item representing a physical span guy between two poles                                                            |
|           | Pole Id                | the id of the pole this is on                                                                                         |
|           | Owner                  | the name of the owner of this span guy                                                                                |
|           | Size                   | the name of the client item found in the client file                                                                  |
|           | Core Strands           | the number of supportive strands in the guy wire                                                                      |
|           | Conductor Strands      | the number of conductive strands in the guy wire                                                                      |
|           | Attachment Height      | the distance from the ground up to the point of attachment                                                            |
|           | Attachment Height Unit | the unit of the attachment height value                                                                               |
|           | Midspan Height         | the distance from the ground to the span guy at midspan                                                               |
|           | Midspan Height Unit    | the unit of the midspan height value                                                                                  |
|           | Height                 | the height of the other end of the span guy                                                                           |
|           | Height Unit            | the unit of the height value                                                                                          |
| PointLoad |                        | additional load applied to the pole                                                                                   |
|           | Pole Id                | the id of the pole this is on                                                                                         |
|           | Owner                  | the name of the owner of this point load                                                                              |
|           | Attach Height          | the distance from the ground up to the point of attachment                                                            |
|           | Attach Height Unit     | the unit of the attachment height value                                                                               |
|           | X Force                | force in the x direction                                                                                              |
|           | X Force Unit           | the unit of the x force value                                                                                         |
|           | Y Force                | force in the y direction                                                                                              |
|           | Y Force Unit           | the unit of the y force value                                                                                         |
|           | Z Force                | force in the z direction                                                                                              |
|           | Z Force Unit           | the unit of the z force value                                                                                         |
| SpanPoint |                        | holds additional data at one point along a wire                                                                       |
|           | Pole Id                | the id of the pole this is on                                                                                         |
|           | Distance               | the distance from the pole                                                                                            |
|           | Distance Unit          | the unit of the distance value                                                                                        |
|           | Environment            | type of environment the point is in ie street, railroad...                                                            |
| Anchor    |                        | an item representing a physical anchor in the ground near a pole                                                      |
|           | Pole Id                | the id of the pole this is on                                                                                         |
|           | Distance               | The distance from the pole                                                                                            |
|           | Distance Unit          | The unit of the distance property                                                                                     |
|           | Direction              | the direction of the anchor around the pole                                                                           |
|           | Owner                  | the name of the owner of this anchor                                                                                  |
|           | Height                 | the height of the other end of the anchor                                                                             |
|           | Height Unit            | the unit of the height value                                                                                          |
|           | Size                   | the name of the client item found in the client file                                                                  |
| NotePoint |                        | additional notes attached to a specific place in the structure                                                        |
|           | PoleId                 | the id of the pole this is on                                                                                         |
|           | Distance               | The distance from the pole                                                                                            |
|           | Distance Unit          | The unit of the distance property                                                                                     |
|           | Direction              | the direction of the note point around the pole                                                                       |
|           | Note                   | the actual note value                                                                                                 |
|           | Height                 | the height above ground that this note refers to                                                                      |
|           | Height Unit            | the unit of the height value                                                                                          |
| Equipment |                        | an item representing a physical piece of equipment on a pole                                                          |
|           | Pole Id                | the id of the pole this is on                                                                                         |
|           | Owner                  | the name of the owner of this equipment                                                                               |
|           | Size                   | the name of the client item found in the client file                                                                  |
|           | Type                   | transformer, street light...                                                                                          |
|           | Attachment Height      | the distance from the ground up to the point of attachment                                                            |
|           | Attachment Height Unit | the unit of the attachment height value                                                                               |
|           | Bottom Height          | the distance from the ground up to the bottom ot the attachment                                                       |
|           | Bottom Height Unit     | the unit of the bottom height value                                                                                   |
|           | Direction              | the direction of the equipment around the pole                                                                        |
| Damage    |                        | an item representing physical damage on a pole                                                                        |
|           | Pole Id                | the id of the pole this is on                                                                                         |
|           | Attach Height          | the distance from the ground to the top of the damage                                                                 |
|           | Attach Height Unit     | the unit of the attach height value                                                                                   |
|           | Damage Height          | height of the damage itself                                                                                           |
|           | Damage Height Unit     | the unit of the damage height value                                                                                   |
|           | Type                   | the type of damage                                                                                                    |
|           | Width                  | the width of the damage                                                                                               |
|           | Width Unit             | the unit of the width value                                                                                           |
|           | Direction              | the direction of the damage around the pole                                                                           |
|           | Arc                    | the value of the arc of the specific damage type                                                                      |
|           | Arc Unit               | the unit of the arc value                                                                                             |
|           | Depth                  | the value of the depth of the specific damage type                                                                    |
|           | Depth Unit             | the unit of the depth value                                                                                           |
|           | Circumference          | the value of the circumference of the specific damage type                                                            |
|           | Circumference Unit     | the unit of the circumference value                                                                                   |
|           | Shell Thickness        | the value of the shell thickness of the specific damage type                                                          |
|           | Shell Thickness Unit   | the unit of the shell thickness value                                                                                 |
|           | Neck Depth             | the value of the neck depth of the specific damage type                                                               |
|           | Neck Depth Unit        | the unit of the neck depth value                                                                                      |
|           | Neck Diameter          | the value of the neck diameter of the specific damage type                                                            |
|           | Neck Diameter Unit     | the unit of the neck diameter value                                                                                   |
|           | Neck Offset            | the value of the neck offset of the specific damage type                                                              |
|           | Neck Offset Unit       | the unit of the neck offset value                                                                                     |
|           | Nest Depth             | the value of the nest depth of the specific damage type                                                               |
|           | Nest Depth Unit        | the unit of the nest depth value                                                                                      |
