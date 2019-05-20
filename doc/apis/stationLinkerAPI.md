Station Linker API
=========

Each project [station](project/station_wrapper.schema) points to one or more asset service stations.
![Linker & Asset Services](https://docs.google.com/drawings/d/1tDv9OEJDpCHqTEdCGpnFeng-IpughLqgyBbWlMxdpkk/pub?w=949&h=666 "Linker & Asset Services")

#### Implementing Apps

1. projectmanager

Methods
=======

Implemented
----------

The linker extends a regular asset service in some ways and therefor has most of that base functionality:

1. [getName](./assetAPI.md#get-name)
1. [getPossibleAssetTypes](./assetAPI.md#get-possible-asset-types)
1. [getStations](./assetAPI.md#get-stations)

Find Stations
-----

Get a set of stations based on parameters provided

#### URL

`https://${HOST}/${APP}/stationLinkerAPI/findStations`

#### Allowed Methods

`GET`

#### Parameters

station_id OR tag are required.

1. station_id: `string`, the id of the station in an asset service.
1. tag: `string`, the asset tag value
1. geometry: [geometry](../../resources/schema/general/geometry.schema), the gps coordinates of one location
1. details: `boolean`, true if all details of the station including assets should be returned

#### Returns

result with an array of stations sorted by distance to gps point passed in

Get Links
-----

Get a set of linkages based on parameters provided

#### URL

`https://${HOST}/${APP}/stationLinkerAPI/getLinks`

#### Allowed Methods

`GET`

#### Parameters

source and station OR linker_id are required.

1. source: `string`, the name of the service that owns the station that has the id given in station_id.
1. station_id: `string`, the id of the station in the source service.
1. linker_id: `string`, the common id of the station that links all stations

#### Returns

array of links

Link Stations
-----

Links stations in different services together returns the linkerId of the linked stations

#### URL

`https://${HOST}/${APP}/stationLinkerAPI/linkStations`


#### Allowed Methods

`GET`

#### Parameters

1. `station_ids`: _required_, a [service and ids object](../../resources/schema/spidamin/asset/service_and_ids.schema).

#### Returns

1. An [id array](../../resources/schema/general/ids.schema)

Unlink Stations
-----

Unlinks stations from any other station, returns the linkerIds of the unlinked stations

#### URL

`https://${HOST}/${APP}/stationLinkerAPI/unlinkStations`

#### Allowed Methods

`GET`

#### Parameters

1. `station_ids`: _required_, a [service and ids object](../../resources/schema/spidamin/asset/service_and_ids.schema).

#### Returns

1. An [id array](../../resources/schema/general/ids.schema)
