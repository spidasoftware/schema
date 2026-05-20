# Project Searches

For working with project searches.

## Implementing Apps

1. projectmanager

&nbsp;

> **Note:**  
> Examples use the {{variable}} notation to denote environment variables (i.e. {{host}}, {{apiToken}}, {{token}}).  
> POSTMAN and Bruno collections are available in the repository. Bruno collection is at `bruno-collection/` in the schema repo.  
> Authentication will require the parameter **token** or **apiToken** to be included in each request.

&nbsp;

# Methods

&nbsp;

## Company

Get all project searches for the company to which the current user belongs.

### URL

`https://${HOST}/${APP}/rest/projectSearches/company`

### Allowed Methods

`GET`

### Parameters

`None`

### Returns

`A list of 0 or more` [projectSearch](../../resources/schema/project/projectSearch.schema) objects.

### Examples

##### POSTMAN using api token authentication

`GET {{host}}/projectmanager/rest/projectSearches/company?apiToken={{apiToken}}`

##### POSTMAN using oidc token authentication

`GET {{host}}/projectmanager/rest/projectSearches/company?token={{token}}`

##### Bruno

Use the **Get Company Searches** request in the `Project Searches` folder. The `apiToken` is set at the collection level.

Count the number of projects matching the search criteria.

### URL

`https://${HOST}/${APP}/rest/projectSearches/${ID}/count`

### Allowed Methods

`GET`

### Parameters

`None`

### Returns

1. An [id](../../resources/schema/general/id.schema) object.

### Examples

##### POSTMAN using api token authentication

`GET {{host}}/projectmanager/rest/projectSearches/123456/count?apiToken={{apiToken}}`

##### POSTMAN using oidc token authentication

`GET {{host}}/projectmanager/rest/projectSearches/123456/count?token={{token}}`

##### Bruno

Use the **Count Search Results** request in the `Project Searches` folder. Set the `id` path parameter to the search ID.

### URLs

`https://${HOST}/${APP}/rest/projectSearches`
`https://${HOST}/${APP}/rest/projectSearches/${ID}`  
`https://${HOST}/${APP}/rest/projectSearches/${ID}/show`

### Allowed Methods

`GET`

### Parameters

`ids - a list of 1 or more ids url encoded - i.e. for ids 31 and 36 encode this [31 ,36] to %5B31%20%2C36%5D`

### Returns

`A list of 0 or more` [projectSearch](../../resources/schema/project/projectSearch.schema) objects.

### Examples

##### POSTMAN get all project searches for the current user using api token authentication

`{{host}}/projectmanager/rest/projectSearches?apiToken={{apiToken}}`

##### POSTMAN get all project searches for the current user with matching ids using api token authentication

`{{host}}/projectmanager/rest/projectSearches?apiToken={{apiToken}}&ids=%5B31%20%2C36%5D`

##### POSTMAN get all project searches for the current user with matching single id using api token authentication

`{{host}}/projectmanager/rest/projectSearches/123456/show?apiToken={{apiToken}}`

##### POSTMAN get all project searches for the current user with matching single id using api token authentication

`{{host}}/projectmanager/rest/projectSearches/123456?apiToken={{apiToken}}`

##### POSTMAN get all project searches for the current user using oidc token authentication

`{{host}}/projectmanager/rest/projectSearches?token={{token}}`

##### POSTMAN get all project searches for the current user with matching ids using oidc token authentication

`{{host}}/projectmanager/rest/projectSearches?token={{token}}&ids=%5B31%20%2C36%5D`

##### POSTMAN get all project searches for the current user with matching single id using oidc token authentication

`{{host}}/projectmanager/rest/projectSearches/123456/show?token={{token}}`

##### POSTMAN get all project searches for the current user with matching single id using oidc token authentication

`{{host}}/projectmanager/rest/projectSearches/123456?token={{token}}`

##### Bruno

Use the **Get Project Searches** request in the `Project Searches` folder. Optionally set `ids` query parameter or the `id` path parameter to filter by ID.

### URL

`https://${HOST}/${APP}/rest/projectSearches`

### Allowed Methods

`POST`

### Parameters

`A` [projectSearch](../../resources/schema/project/projectSearch.schema) object passed as JSON in the request body.

### Returns

`A` [projectSearch](../../resources/schema/project/projectSearch.schema) object.

### Examples

##### POSTMAN using api token authentication

`POST {{host}}/projectmanager/rest/projectSearches?apiToken={{apiToken}}`

Body (JSON):
```json
{
    "projectSearch": {
        ...
    }
}
```

##### POSTMAN using oidc token authentication

`POST {{host}}/projectmanager/rest/projectSearches?token={{token}}`

##### Bruno

Use the **Create Search** request (POST) in the `Project Searches` folder if available, or create a new POST request to `{{pmBase}}/rest/projectSearches` with the projectSearch JSON in the body.
    "projectSearch": {
        "name": "test",
        "userId": null,
        "groups": [
            {
                "entries": [
                    {
                        "projectContains": true,
                        "entryType": "PROJECT_CODE",
                        "rows": [
                            {
                                "cells": [
                                    {
                                        "operator": "EQUALS",
                                        "value": "123",
                                        "secondValue": null,
                                        "active": true,
                                        "field": "CODE_COMPANY_NAME",
                                        "caseSensitive": false
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ]
    }
}
```

&nbsp;

---

&nbsp;

## Delete

Delete a project search.

### URL

`https://${HOST}/${APP}/rest/projectSearches/${ID}`

### Allowed Methods

`DELETE`

### Parameters

`None`

### Returns

`true (success) or false (fail)`

### Examples

##### POSTMAN using api token authentication (see project-search-json example below)

`DELETE {{host}}/projectmanager/rest/projectSearches/123456?apiToken={{apiToken}}`

##### POSTMAN using oidc token authentication (see project-search-json example below)

`DELETE {{host}}/projectmanager/rest/projectSearches/123456?token={{token}}`

##### Bruno

Use the **Delete Search** request (DELETE) in the `Project Searches` folder if available, or send a DELETE to `{{pmBase}}/rest/projectSearches/{id}`.

Return all projects matching the project search.

### URL

`https://${HOST}/${APP}/rest/projectSearches/${ID}/projects`

### Allowed Methods

`GET`

### Parameters

1. `details`: `boolean`, if true include full station details (optional, defaults to false)

### Returns

`A list of 0 or more` [project](../../resources/schema/project/project.schema) objects.

### Examples

##### POSTMAN using api token authentication

`GET {{host}}/projectmanager/rest/projectSearches/123456/projects?apiToken={{apiToken}}`

##### POSTMAN using oidc token authentication

`GET {{host}}/projectmanager/rest/projectSearches/123456/projects?token={{token}}`

##### Bruno

Use the **Get Search Projects** request in the `Project Searches` folder. Set the `id` path parameter to the search ID. Optionally enable the `details` query parameter.

Return all stations matching the project search.

### URL

`https://${HOST}/${APP}/rest/projectSearches/${ID}/stations`

### Allowed Methods

`GET`

### Parameters

`None`

### Returns

`A list of 0 or more` [station](../../resources/schema/asset/station.schema) objects.

### Examples

##### POSTMAN using api token authentication

`GET {{host}}/projectmanager/rest/projectSearches/123456/stations?apiToken={{apiToken}}`

##### POSTMAN using oidc token authentication

`GET {{host}}/projectmanager/rest/projectSearches/123456/stations?token={{token}}`

##### Bruno

Use the **Get Search Stations** request in the `Project Searches` folder. Set the `id` path parameter to the search ID.

Export a project search definition as a JSON file download.

### URL

`https://${HOST}/${APP}/rest/projectSearches/${ID}/export`

### Allowed Methods

`GET`

### Parameters

`None`

### Returns

A JSON file download of the [projectSearch](../../resources/schema/project/projectSearch.schema) object.

### Examples

##### POSTMAN using api token authentication

`GET {{host}}/projectmanager/rest/projectSearches/123456/export?apiToken={{apiToken}}`

##### Bruno

Use the **Export Search** request in the `Project Searches` folder. Set the `id` path parameter to the search ID.

Export search results as a CSV file download.

### URL

`https://${HOST}/${APP}/rest/projectSearches/${ID}/exportCsv`

### Allowed Methods

`GET`

### Parameters

1. `mode`: `PROJECT` or `STATION` (determines CSV structure)
1. `tzOffsetMinutes`: timezone offset in minutes
1. `usesDst`: `boolean`, whether timezone uses daylight saving time

### Returns

A CSV file download.

### Examples

##### POSTMAN using api token authentication

`GET {{host}}/projectmanager/rest/projectSearches/123456/exportCsv?mode=PROJECT&apiToken={{apiToken}}`

##### Bruno

Use the **Export Search CSV** request in the `Project Searches` folder. Set the `id` path parameter and configure `mode` (`PROJECT` or `STATION`), `tzOffsetMinutes`, and `usesDst` query parameters.

Update an existing project search.

### URL

`https://${HOST}/${APP}/rest/projectSearches/${ID}`

### Allowed Methods

`PUT`

### Parameters

`A` [projectSearch](../../resources/schema/project/projectSearch.schema) object.

### Returns

`A` [projectSearch](../../resources/schema/project/projectSearch.schema) object.

### Examples

##### POSTMAN using api token authentication

`PUT {{host}}/projectmanager/rest/projectSearches/123456?apiToken={{apiToken}}`

##### Bruno

Send a PUT request to `{{pmBase}}/rest/projectSearches/{id}` with the updated projectSearch JSON in the body.
