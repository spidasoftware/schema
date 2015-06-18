Asset FILE API
============

This API is set of methods that allow you to create and modify with the file assets.  

## Implementing Apps

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

`https://demo.spidasoftware.com/filefort/assetAPI/getName`

Get Version Count
----------

Get version count of attachment with uuid.  This can be used in the offset in future requests.

#### URL

`https://${HOST}/${APP}/assetAPI/getVersionCount`

#### Allowed Methods

`GET`

#### Parameters

1. `uuid`: a required string of the uuid of the attachment

#### Returns

`number`

#### Examples

Get Created Date
----------

Get date the attachment was created.

#### URL

`https://${HOST}/${APP}/assetAPI/getCreatedDate`

#### Allowed Methods

`GET`

#### Parameters

1. `uuid`: a required string of the uuid of the attachment

#### Returns

`number`

#### Examples

Get Updated Date
----------

Get date the attachment was last updated.

#### URL

`https://${HOST}/${APP}/assetAPI/getUpdatedDate`

#### Allowed Methods

`GET`

#### Parameters

1. `uuid`: a required string of the uuid of the attachment

#### Returns

`number`

#### Examples

Get File
----------

Get path for attachment with uuid

#### URL

`https://${HOST}/${APP}/assetFileAPI/getFile`

#### Allowed Methods

`GET`

#### Parameters

1. `uuid`: a required string of the uuid of the attachment
1. `offset`: A offset count of the file, defaults to 0

#### Returns

`string`

#### Examples

Get Bytes
----------

Get base64 encoded bytes for attachment with uuid

#### URL

`https://${HOST}/${APP}/assetFileAPI/getBytes`

#### Allowed Methods

`GET`

#### Parameters

1. `uuid`: a required string of the uuid of the attachment
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

1. `uuid`: a required string of the uuid of the attachment
1. `offset`: A offset count of the file, defaults to 0

#### Returns

file response body

#### Examples
