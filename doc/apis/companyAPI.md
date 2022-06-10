Company API
============

The Company API will let you view company details and other attributes.

## Implementing Apps

1. usersmaster

Methods
========

Get Company
----------

Gets the company details of the given Company ID. Also used to display optional company attribute data.

#### URL

`https://${HOST}/${APP}/companyAPI/getCompany`

#### Parameters

1. `id`: a required company id. Can be obtained from Usersmaster.
2. `params`: a string value representing a list of optional attributes to return.(optional)   

#### Available Attributes

|  Attribute  |  Description                                |
|-------------|:-------------------------------------------:|
|userGroups| Includes the name of all user groups associated with the given company id as a list.|
     

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
--data-urlencode 'apiToken=xxxxxx' 
```

A sample return is as follows for the above example:

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
