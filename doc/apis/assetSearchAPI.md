Asset Search API
============

This API is set of methods that allow you to search stations and assets.  

#### Implementing Apps

1. assetmaster
1. filefort

Methods
========

Get Name
----------

Get the name of this service.  Used in display of assets.

#### URL

`https://${HOST}/${APP}/assetSearchAPI/getName`

#### Parameters

none

#### Allowed Methods

`GET`

#### Returns

`string`

#### Examples

`https://demo.spidasoftware.com/assetmaster/assetSearchAPI/getName`

##### Bruno

Not included in the collection (use Get Name from Asset API folder instead).

Quick Find
----------

Find stations based string.  This searches some basic information on the station including ID and any Tags associated with it.

#### URL

`https://${HOST}/${APP}/assetSearchAPI/quickFind`

#### Allowed Methods

`GET`

#### Parameters

1. `find_param`: _required_, a `string` to search on.
1. `companyId`: a `number` to search other than your home company.

#### Returns

1. `stations`: A [stations object](../../resources/schema/spidamin/asset/stations.schema).

#### Examples

##### Bruno

Use the **Quick Find** request in the `Asset Search API` folder. Set the `find_param` query parameter to the search string.

Advanced Find
----------

Find stations based on the given params.

#### URL

`https://${HOST}/${APP}/assetSearchAPI/advancedFind`

#### Allowed Methods

`GET`

#### Parameters

1. `find_query`: _required_, a [query object](../../resources/schema/spidamin/asset/find_query.schema)
1. `companyId`: a `number` to search other than your home company.

#### Returns

1. A [stations object](../../resources/schema/spidamin/asset/stations.schema).

#### Examples

##### Bruno

Use the **Advanced Find** request in the `Asset Search API` folder. Set the `find_query` query parameter to a JSON query object.

Get Advanced Find Options
----------

Get the options for searching on this service.  This would give back an object descriptor that would allow the creation of complex queries.

#### URL

`https://${HOST}/${APP}/assetSearchAPI/getAdvancedFindOptions`

#### Allowed Methods

`GET`

#### Parameters

none

#### Returns

1. `find_options`: An [method response object](../../resources/schema/spidamin/asset/find_options.schema)

#### Examples

##### Bruno

Use the **Get Advanced Find Options** request in the `Asset Search API` folder.
