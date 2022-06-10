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
2. `params`: a string value representing a list of optional attributes to return.(optional)   
    Attributes currently available are: ["userGroups"]

**Note: `params` must be URI encoded.

#### Allowed Methods

`GET`, `POST`

#### Returns

`string`

#### Examples
`cURL`
```
curl --location --request POST 'https://demo.spidastudio.com/usersmaster/companyAPI/getCompany' \
--data-urlencode 'id=2' \
--data-urlencode 'params=["userGroups"]' \
--data-urlencode 'apiToken=63b40674-d9d2-47e2-8f8a-76a9b53d927b' 
```

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
