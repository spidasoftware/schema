Asset API
============

The Asset API is a set of endpoints that allow you to interact with the assets and stations that SPIDAMin use to create and track projects.  

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

Get Possible Asset Details
----------

Return the asset detail keys that can be returned by this service.

#### URL

`https://${HOST}/${APP}/assetAPI/getPossibleAssetDetails`

#### Parameters

none

#### Allowed Methods

`GET`

#### Returns

Array of `string`

#### Examples

Get Possible Asset Details
----------

Return the asset types this service returns.

#### URL

`https://${HOST}/${APP}/assetAPI/getPossibleAssetTypes`

#### Parameters

#### Allowed Methods

`GET`

#### Returns

[asset_details.schema](../resources/v1/schema/spidamin/asset/asset_details.schema)

#### Examples

Get Stations
----------

Get a set of Stations based on parameters provided

#### URL

`https://${HOST}/${APP}/assetAPI/getStations`

#### Parameters

1. `stations_ids`: an array of the ids to retrieve. Required if bounding box or tags is not provided.  If the tag parameter is not implemented this should search tags as well as station ids
1. `tags`: Services may optionally implement this parameter.  If this is implemented any stations containing this tags will be returned.  Required if bounding box or station_ids is not provided.
1. `bounding_box`: A [polygon object](../resources/v1/schema/general/geometry.schema) that is the south, west, north, east of information to return. Required if no station ids or tags are provided.
1. `company_ids`: an array of ids for the company to retrieve from. Not required.
1. `details`: a boolean on whether to fetch station asset details with the query.

#### Allowed Methods

`GET` or `POST`

#### Returns

[stations.schema](../resources/v1/schema/spidamin/asset/stations.schema)

#### Examples

As you can see, and error object was returned, and it told us we are missing an required parameter.  _station_ids_ or _bounding_box_ was not included.  One of these is needed because you are either getting assets by location or id.  Lets add the required _stations_id_ parameter and try again:

    curl -g 'https://test.spidasoftware.com/assetmaster/assetAPI/getStations?station_ids=["1"]'

The response from the server this time is:

    {"result":{"stations":[]}}

This is a successful message, it just didn't find any stations with that id.

A response that found something would look more like:

    {
      "result": {
        "stations": [
          {
            "assetTypes": [
              "POLE"
            ],
            "stationId": "ff8081813dfe983f013dfea557060000",
            "primaryAssetOwnerId": 43,
            "geometry": {
              "type": "Point",
              "coordinates": [
                -81.90434265136719,
                37.48816680908203
              ]
            },
            "primaryAssetType": "POLE",
            "dataProviderId": 43
          }
        ],
        "geometry": {
          "type": "Point",
          "coordinates": [
            -81.90434265136719,
            37.48816680908203
          ]
        }
      }
    }

Get Assets
----------

Get a set of Assets based on parameters provided

#### URL

`https://${HOST}/${APP}/assetAPI/getAssets`

#### Parameters

1. `asset_ids`: an array of the ids to retrieve.
1. `details`: a boolean on whether to fetch station asset details with the query.

#### Allowed Methods

`GET`

#### Returns

[stations.schema](../resources/v1/schema/spidamin/asset/stations.schema)

#### Examples

Upload JSON File
----------

#### URL

`https://${HOST}/${APP}/assetAPI/uploadJSONFile`

#### Parameters

#### Allowed Methods

#### Returns

#### Examples
