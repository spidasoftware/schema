Asset FILE API
============

This API is set of methods that allow you to create and modify with the file assets.  

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

Get Version Count
----------

Get version count of attachment with externalId.  This can be used in the offset in future requests.

#### URL

`https://${HOST}/${APP}/assetAPI/getVersionCount`

#### Allowed Methods

`GET`

#### Parameters

1. `externalId`: a required string of the external id of the attachment

#### Returns

`number`

#### Examples

Get File
----------

Get path for attachment with externalId

#### URL

`https://${HOST}/${APP}/assetFileAPI/getFile`

#### Allowed Methods

`GET`

#### Parameters

1. `externalId`: a required string of the external id of the attachment
1. `offset`: A offset count of the file, defaults to 0

#### Returns

`string`

#### Examples

Get Bytes
----------

Get base64 encoded bytes for attachment with externalId

#### URL

`https://${HOST}/${APP}/assetFileAPI/getBytes`

#### Allowed Methods

`GET`

#### Parameters

1. `externalId`: a required string of the external id of the attachment
1. `offset`: A offset count of the file, defaults to 0

#### Returns

`string`

#### Examples

Get Raw
----------

Get get the raw bytes in the response body with the "Content-disposition:attachment" header.  This can be used to download that file.

#### URL

`https://${HOST}/${APP}/assetFileAPI/getRaw`

#### Allowed Methods

`GET`

#### Parameters

1. `externalId`: a required string of the external id of the attachment
1. `offset`: A offset count of the file, defaults to 0

#### Returns

file response body

#### Examples
