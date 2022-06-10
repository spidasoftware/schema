Company API
============

The Company API will let you view Company details and other attributes.

## Implementing Apps

1. usersmaster

Methods
========

Get Company
----------

Gets the Company Details of the Company ID. Used to display Company attribute data.

#### URL

`https://${HOST}/${APP}/companyAPI/getCompany`

#### Parameters

1. `id`: a required company id. Can be obtained from Usersmaster.
2. `params`: a string value representing a list of optional attributes to return with the call.(optional)
    Attributes currently available are: ["usergroups"]

**Note: `params` must be URI encoded.

#### Allowed Methods

`GET`, `POST`

#### Returns

`string`

#### Examples
`https://demo.spidasoftware.com/usersmaster/companyAPI/getCompany?id=${company_id}&apiToken=xxxx&params=['userGroups']`

With URI Encoding for params:
`https://demo.spidasoftware.com/usersmaster/companyAPI/getCompany?id=${company_id}&apiToken=xxxx&params=%5B%22userGroups%22%5D`

A sample return is as follows with userGroup value set to "true" :

```
{
    "result": {
        "id": 2,
        "name": "SPIDA",
        "subscriptionStartDate": 1654711746181,
        "subscriptionEndDate": 1686247746181,
        "local": "OH",
        "city": "Gahanna",
        "postalCode": "43230",
        "country": "USA",
        "phone": "614.470.9882",
        "address1": "560 Officenter Pl.",
        "address2": "Suite 1",
        "userGroups": [
            "Just a Group",
            "Another Group"
        ]
    }
}
```
