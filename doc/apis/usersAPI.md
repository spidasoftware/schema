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

1. `user_json`: _required_, A [user object](../../resources/schema/spidamin/user/user.schema). If `id` is present the user will be updated, otherwise a new user will be created.

#### Returns

1. An [id object](../../resources/schema/general/id.schema)

#### Examples

##### Bruno

Use the **Create or Update User** request in the `Users API` folder. Pass the user JSON in the `user_json` form parameter.
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

#### Examples

##### Bruno

Use the **Get Logged In User** request in the `Users API` folder.


Get User
-----

Return user details

#### URL

`https://${HOST}/${APP}/usersAPI/getUser`

#### Allowed Methods

`GET`

#### Parameters

One of the first three parameters is required.

1. id: `number` the id of the user to retrieve.
1. api_token: `string` the api token of the user to retrieve.
1. email: `string` the email of the user to retrieve.
1. includeForeignCompanies: `boolean` whether to include foreign company data. Defaults to `true`. Set to `false` to exclude.

#### Returns

A [user object](../../resources/schema/spidamin/user/user.schema)

#### Examples

##### Bruno

Use the **Get User** request in the `Users API` folder. Enable one of the optional query parameters (`id`, `api_token`, or `email`) and optionally `includeForeignCompanies`.

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

#### Examples

##### Bruno

Use the **Get User by External ID** request in the `Users API` folder. Set the `system` and `value` query parameters.

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

#### Examples

##### Bruno

Use the **Delete User** request in the `Users API` folder. Set the `id` parameter to the user ID.
