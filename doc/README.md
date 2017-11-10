Overview
=========

All of the specific endpoints in our APIs are described in the [apis](apis) folder.

We have two main kinds of API's: RPC and REST.

## RPC API

RPC stands for "Remote Procedure Call" and is for "invoking an action on a remote system". Further specific information on our RPC type endpoints is given in [this additional file](rpc.md).

#### Available Services

##### SPIDAMin

1. [project](apis/projectAPI.md) - SPIDAMin projects and services are defined here. This contains the primary means to interact with SPIDAMin.
1. [users](apis/usersAPI.md) -  Interacting with users and user services are in this service.
1. [asset](apis/assetAPI.md) -  These objects deal with the assets and asset services.  An example is an asset is a pole.
1. [asset search](apis/assetSearchAPI.md) -  These methods allow the searching of assets and stations.
1. [asset creation](apis/assetCreationAPI.md) -  These methods allow the creation and modification of assets and stations.
1. [asset file](apis/assetFileAPI.md) -  These methods allow the interaction with file based assets and stations.
1. [station linker](apis/stationLinkerAPI.md) -  These methods allow for the association of related stations between services.

##### SPIDACalc

1. [calc](apis/calcAPI.md) - the ability to control the desktop version of SPIDACalc from external tools.

We based our services on the json-rpc that can be found [here](http://www.simple-is-better.org/json-rpc/jsonrpc20-schema-service-descriptor.html).  

## REST API

REST is a specific type of data service, and a few of our more data centric services can be represented this way.

#### Available Services

##### SPIDAMin

1. [webhooks](apis/webhookAPI.md) - Listen to activity in SPIDAMin from another system
1. [action](apis/actionAPI.md) - Trigger any of the actions in a work flow with this service.
1. [spidadb](apis/spidadbAPI.md) - Push and pull projects into SPIDAdb.

##### SPIDACalc

1. [client](apis/calcAPI.md) - a REST endpoint to retrieve engineering information used in SPIDACalc from external tools.

SPIDACalc
========

Information about the SPIDACalc exchange format JSON and integration API can be found in [The Calc API Developer Guide.](calc.md)

SPIDAMin
========

There are some core concepts that apply to both types of services when used in a client/server relationship like SPIDAMin.

## API Token

For most of the calls against a SPIDAMin service you will need to include your apiToken parameter, this is in addition to any parameters required by the method.  This would be for the service interface if it is implemented on a server environment.  There are times when we implement the same service in the local environments, and then the apiToken would not be needed, but in most cases it will be required.  If you make a service call but get redirect to a security login, then your apiToken was not included or was invalid.

## Sessions

When a call is made to SPIDAMin and the API Token is successfully authenticated a session is created.  A new session is created for each different application, a call to AssetMaster and a call to Project Manager will create two sessions.  Each session will have to be expired in separate calls, there are two ways to expire a session. Here is an example of expiring an AssetMaster session by calling logout:

    curl -g 'https://test.spidasoftware.com/assetmaster/j_spring_security_logout'

The parameter expireSession set to true can also be passed with calls in order to expire the session.  An example of this is:

    curl -g 'https://test.spidasoftware.com/assetmaster/assetAPI/getStations?station_ids=["1"]&expireSession=true'

The advantage of passing the expireSession parameter is that it does not require another http request just to expire the session.

## Switching Companies and Users

Once the API Token is successfully authenticated the user will be logged into their home company.  The session can be switched to a different company and/or user assuming the user has the correct role.  The sessions are per web application so switching in assetmaster will not switch the company in projectmanager.  If accessing a project in projectmanager from different company the session should be switched to the company that the project is in. If accessing stations in assetmaster from a different company than users home company the session should be switched to the stations company.

To switch the the assetmaster session:

    curl -g 'https://test.spidasoftware.com/assetmaster/switchcompany?coselect=TestCompany'

To switch the projectmanager session:

    curl -g 'https://test.spidasoftware.com/projectmanager/switchcompany?coselect=TestCompany'

To switch to a different user make a call to:

    curl -g 'https://test.spidasoftware.com/assetmaster/switchcompany?userId=1'
