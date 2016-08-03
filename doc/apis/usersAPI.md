Users API
=========

A service to manage users and get logged in info.

## Implementing Apps

1. usersmaster

Methods
======

Create or Update
----------

Creates or Updates a User.  It will create if no id is present, or update if
an id is in the posted user object.

#### URL

`https://${HOST}/${APP}/usersAPI/createOrUpdate`

#### Allowed Methods

`POST`

#### Parameters

station, asset, or attachment required

1. `user_json`: A [user object](../../resources/schema/spidamin/user/user.schema)

#### Returns

1. An [id object](../../resources/schema/general/id.schema)

Get Logged In User
-----

Based on the api-token or login process, return the user in the session.

#### URL

`https://${HOST}/${APP}/usersAPI/getLoggedInUser`

#### Allowed Methods

`GET`

#### Parameters

none

#### Returns

A [user object](../../resources/schema/spidamin/user/user.schema)


Get User
-----

Return user details

#### URL

`https://${HOST}/${APP}/usersAPI/getUser`

#### Allowed Methods

`GET`

#### Parameters

One of the parameters is required.

1. id: `number` the id of the user to retrieve.
1. api_token: `string` the api of the user to retrieve.
1. email: `string` the email of the user to retrieve.

#### Returns

A [user object](../../resources/schema/spidamin/user/user.schema)

Get User by External ID
-----

Return user details from the 'alias' that is used by an external system.  

#### URL

`https://${HOST}/${APP}/usersAPI/getUserByExternalId`

#### Allowed Methods

`GET`

#### Parameters


1. system: `string` the required name of the system of the alias.
1. value: `string` the required value in that system.

#### Returns

A [user object](../../resources/schema/spidamin/user/user.schema)

Delete
-------

deletes a user

#### URL

`https://${HOST}/${APP}/usersAPI/delete`

#### Allowed Methods

`POST`

#### Parameters

1. `id`: _required_, a `number` of the id of the user to delete

#### Returns

1. A [general response object](../../resources/schema/general/method_response.schema)
