Asset Creation API
============

This API is set of methods that allow you to create and modify with the assets and stations that SPIDAMin use to create and track projects.  

## Implementing Apps

1. assetmaster
1. filefort

Methods
========

Get Name
----------

Get the name of this service.  Used in display of assets.

#### URL

`https://${HOST}/${APP}/assetAPI/getName`

#### Parameters

none

#### Allowed Methods

`GET`

#### Returns

`string`

#### Examples

`https://demo.spidasoftware.com/assetmaster/assetAPI/getName`

Create or Update
----------

Creates or Updates a Station, Asset, or Attachment.  It will create if no id is present, or update if
an id is in the posted station object.

#### URL

`https://${HOST}/${APP}/assetCreationAPI/createOrUpdate`

#### Allowed Methods

`POST`

#### Parameters

station, asset, or attachment required

1. `asset`: An [asset object](../../resources/v1/schema/spidamin/asset/station.schema)
1. `station`: A [station object](../../resources/v1/schema/spidamin/asset/station.schema)
1. `attachment`: An array of [attachment object](../../resources/v1/schema/spidamin/asset/attachment.schema)

#### Returns

1. An [id array](../../resources/v1/schema/general/ids.schema)

Delete
----------

Deletes stations or assets

#### URL

`https://${HOST}/${APP}/assetCreationAPI/delete`

#### Allowed Methods

`POST`

#### Parameters

1. `asset_ids`: An array of ids
1. `station_ids`: An array of ids
1. `attachment_uuids`: An array of uuids.

#### Returns

1. `asset`: An [method response object](../../resources/v1/schema/general/method_response.schema)

#### Examples
