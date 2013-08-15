Asset JSON Schema's and Interfaces
==============================

### Interfaces

1. asset.json - is used for basic getting of stations and assets based on id, bounding box, and company id's
2. asset_creation.json - the interface to create assets in a service.  This is for generally for adding and removing from the back end services.
3. asset_search.json - the interface for finding assets based on more complex queries than given in the basic asset interface.

### Schemas

1. asset.schema - the basic asset at a given station location.
2. assets.schema - a list of assets
3. find_options.schema - an object that is returned by the search service to describe what can be searched.
4. find_params.schema - an object used for searching in the asset_search service.
5. station.schema - a set of assets with some common geometry
6. stations.schema - a set of stations returned from various services with a combined geometry.