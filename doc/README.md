# SPIDAmin API Overview

All of the specific endpoints in our APIs are described in the [apis](apis) folder.

We have two main kinds of APIs: RPC and REST. We also expose a WFS service using geoserver.

## RPC API

RPC stands for "Remote Procedure Call" and is for "invoking an action on a remote system". Further specific information on our RPC type endpoints is given in [this additional file](rpc.md).

### Available RPC Services

1. [project](apis/projectAPI.md) - SPIDAmin projects and services are defined here. This contains the primary means to interact with SPIDAmin.
1. [project searches](rest/projectSearches.md) - These methods provide project search capabilities.
1. [users](apis/usersAPI.md) - Interacting with users and user services are in this service.
1. [asset](apis/assetAPI.md) - These objects deal with the assets and asset services. An example is an asset is a pole.
1. [asset search](apis/assetSearchAPI.md) - These methods allow the searching of assets and stations.
1. [asset creation](apis/assetCreationAPI.md) - These methods allow the creation and modification of assets and stations.
1. [asset file](apis/assetFileAPI.md) - These methods allow the interaction with file based assets and stations.
1. [station linker](apis/stationLinkerAPI.md) - These methods allow for the association of related stations between services.
1. [company](apis/companyAPI.md) - Interacting with company objects. Allows retrieval of Company attributes and details.

### Available RPC Services (by implementing application)

1. assetmaster
   - [asset](apis/assetAPI.md) - These objects deal with the assets and asset services. An example is an asset is a pole.
   - [asset search](apis/assetSearchAPI.md) - These methods allow the searching of assets and stations.
   - [asset creation](apis/assetCreationAPI.md) - These methods allow the creation and modification of assets and stations.
1. filefort
   - [asset](apis/assetAPI.md) - These objects deal with the assets and asset services. An example is an asset is a pole.
   - [asset search](apis/assetSearchAPI.md) - These methods allow the searching of assets and stations.
   - [asset creation](apis/assetCreationAPI.md) - These methods allow the creation and modification of assets and stations.
   - [asset file](apis/assetFileAPI.md) - These methods allow the interaction with file based assets and stations.
1. projectmanager
   - [project](apis/projectAPI.md) - SPIDAmin projects and services are defined here. This contains the primary means to interact with SPIDAmin.
   - [project](apis/projectAPI.md) - These methods provide project search capabilities.
   - [station linker](apis/stationLinkerAPI.md) - These methods allow for the association of related stations between services.
1. usersmaster
   - [users](apis/usersAPI.md) - Interacting with users and user services are in this service.
   - [company](apis/companyAPI.md) - Interacting with company objects. Allows retrieval of Company attributes and details.

## REST API

REST is a specific type of data service, and a few of our more data centric services can be represented this way.

### Available Services

1. [webhooks](apis/webhookAPI.md) - Listen to activity in SPIDAMin from another system
1. [action](apis/actionAPI.md) - Trigger any of the actions in a work flow with this service.
1. [spidadb](apis/spidadbAPI.md) - Push and pull projects into SPIDAdb.

### License Agreement (EULA)

All users must accept the License Agreement. If this has not been accepted, all HTTP requests will be redirected to usersmaster/agreement. Login to SPIDAmin and you will be redirected to the License Agreement. Click the 'Accept' button at the bottom of the page.

## API Token

For most of the calls against a SPIDAmin service you will need to include your apiToken parameter, this is in addition to any parameters required by the method. This would be for the service interface if it is implemented on a server environment. There are times when we implement the same service in the local environments, and then the apiToken would not be needed, but in most cases it will be required. If you make a service call but get redirect to a security login, then your apiToken was not included or was invalid.

## Sessions

When a call is made to SPIDAmin and the API Token is successfully authenticated a session is created. A new session is created for each different application, a call to AssetMaster and a call to Project Manager will create two sessions. Each session will have to be expired in separate calls, there are two ways to expire a session. Here is an example of expiring an AssetMaster session by calling logout:

    curl -g 'https://test.spidasoftware.com/assetmaster/j_spring_security_logout'

The parameter expireSession set to true can also be passed with calls in order to expire the session. An example of this is:

    curl -g 'https://test.spidasoftware.com/assetmaster/assetAPI/getStations?station_ids=["1"]&expireSession=true'

The advantage of passing the expireSession parameter is that it does not require another http request just to expire the session.

## Switching Companies and Users

Once the API Token is successfully authenticated the user will be logged into their home company. The session can be switched to a different company and/or user assuming the user has the correct role. The sessions are per web application so switching in assetmaster will not switch the company in projectmanager. If accessing a project in projectmanager from different company the session should be switched to the company that the project is in. If accessing stations in assetmaster from a different company than users home company the session should be switched to the stations company.

To switch the the assetmaster session:

    curl -g 'https://test.spidasoftware.com/assetmaster/switchcompany?coselect=123'

To switch the projectmanager session:

    curl -g 'https://test.spidasoftware.com/projectmanager/switchcompany?coselect=123'

---

_Prior to 11-27-2017 coselect value was the company name_

To switch the the assetmaster session:

    curl -g 'https://test.spidasoftware.com/assetmaster/switchcompany?coselect=TestCompany'

To switch the projectmanager session:

    curl -g 'https://test.spidasoftware.com/projectmanager/switchcompany?coselect=TestCompany'

---

To switch to a different user make a call to:

    curl -g 'https://test.spidasoftware.com/assetmaster/switchcompany?userId=1'

## WFS

You can find this at `https://companynamehere.spidastudio.com/spidadb/geoserver/wfs/DB`

XML Schema here `https://companynamehere.spidastudio.com/spidadb/geoserver/wfs/DB?version=1.0.0&service=wfs&request=DescribeFeatureType`

If not logged in, you can use basic authentication. The username is the user's email address. The password is the user's api token.

[Layer Data Exposed](wfs.md)
