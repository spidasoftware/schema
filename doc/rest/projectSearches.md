# Project Searches

For working with project searches.

## Implementing Apps

1. projectmanager

&nbsp;

> **Note:**  
> Examples use the {{variable}} notation to denote POSTMAN environment variables (i.e. {{host}}, {{api-token}}, {{token}}).  
> Authentication will require the parameter **token** or **api-tokens** to be included in each request.

&nbsp;

# Methods

&nbsp;

## Company

Get all project searches for the company to which the current user belongs.

### URL

`https://${HOST}/${APP}/projectSearches/company`

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

&nbsp;

---

&nbsp;

## Count

Count the number of projects matching the search criteria.

### URL

`https://${HOST}/${APP}/projectSearches/${ID}/count`

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

&nbsp;

---

&nbsp;

## Project Searches

Get project searches.

### URLs

`https://${HOST}/${APP}/projectSearches`
`https://${HOST}/${APP}/projectSearches/${ID}`  
`https://${HOST}/${APP}/projectSearches/${ID}/show`

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

&nbsp;

---

&nbsp;

## Project Searches

Create a project search.

### URL

`https://${HOST}/${APP}/projectSearches`

### Allowed Methods

`POST`

### Parameters

`A` [projectSearch](../../resources/schema/project/projectSearch.schema) object.

### Returns

`A` [projectSearch](../../resources/schema/project/projectSearch.schema) object.

### Examples

##### POSTMAN using api token authentication (see project-search-json example below)

`POST {{host}}/projectmanager/rest/projectSearches/projectSearch=<project-search-json>?apiToken={{apiToken}}`

##### POSTMAN using oidc token authentication (see project-search-json example below)

`POST {{host}}/projectmanager/rest/projectSearches/projectSearch=<project-search-json>?token={{token}}`

```
{
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

`https://${HOST}/${APP}/projectSearches/${ID}/delete`

### Allowed Methods

`DELETE`

### Parameters

`None`

### Returns

`true (success) or false (fail)`

### Examples

##### POSTMAN using api token authentication (see project-search-json example below)

`DELETE {{host}}/projectmanager/rest/projectSearches/123456/delete?apiToken={{apiToken}}`

##### POSTMAN using oidc token authentication (see project-search-json example below)

`DELETE {{host}}/projectmanager/rest/projectSearches/123456/delete?token={{token}}`

&nbsp;

---

&nbsp;

## Projects

Return all projects matching the project searches.

### URL

`https://${HOST}/${APP}/${id}/projects`

### Allowed Methods

`GET`

### Parameters

`None`

### Returns

`A list of 0 or more` [project](../../resources/schema/project/project.schema) objects.

### Examples

##### POSTMAN using api token authentication

`GET {{host}}/projectmanager/rest/projectSearches/123456/projects?apiToken={{apiToken}}`

##### POSTMAN using oidc token authentication

`GET {{host}}/projectmanager/rest/projectSearches/123456/projects?token={{token}}`

&nbsp;

---

&nbsp;

## Stations

Return all stations matching the project searches.

### URL

`https://${HOST}/${APP}/${id}/stations`

### Allowed Methods

`GET`

### Parameters

`None`

### Returns

`A list of 0 or more` [station](../../resources/schema/asset/station.schema) objects.

### Examples

##### POSTMAN using api token authentication

`GET {{host}}/projectmanager/rest/projectSearches/123456/projects?apiToken={{apiToken}}`

##### POSTMAN using oidc token authentication

`GET {{host}}/projectmanager/rest/projectSearches/123456/projects?token={{token}}`
