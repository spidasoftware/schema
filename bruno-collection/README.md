# SPIDAMin Bruno API Collection

A comprehensive [Bruno](https://www.usebruno.com/) collection for making REST calls against the SPIDAMin API. Created by reading the schema documentation and verifying against the actual Grails controller source code.

## Setup

1. Open Bruno and click **Open Collection**
2. Select this `bruno-collection/` folder
3. Choose an environment: **Local** (multi-port dev) or **Docker** (single host)
4. Set the `apiToken` variable in your environment to your user's API token

## Environments

| Environment | Description |
|-------------|-------------|
| **Local** | Local dev with separate ports — PM: 8888, UM: 8383, DB: 8181, AM/FF: 8080 |
| **Docker** | Single Docker host with app context paths on one port |

## Collection Contents — 83 files

### Collection Config (2 files)
- `bruno.json` — Bruno collection metadata
- `collection.bru` — Collection-level settings (Content-Type header, apiToken query param)

### Environments (2 files)
- `environments/Local.bru` — Local development environment variables
- `environments/Docker.bru` — Docker environment variables

### Project API (20 requests → ProjectManager :8888)
| File | Method | Endpoint |
|------|--------|----------|
| Get My Projects | GET | `/projectAPI/getMyProjects` |
| Get Projects | GET | `/projectAPI/getProjects` |
| Get All Projects In Company | GET | `/projectAPI/getAllProjectsInCompany` |
| Get Projects by DB ID | GET | `/projectAPI/getProjectsByDBId` |
| Get DB Project by DB ID | GET | `/projectAPI/getDBProjectByDBId` |
| Get DB Location by DB ID | GET | `/projectAPI/getDBLocationByDBId` |
| Get Location Thumbnails by DB ID | GET | `/projectAPI/getLocationThumbnailsByDBId` |
| Get Location Photos by DB ID | GET | `/projectAPI/getLocationPhotosByDBId` |
| Find DB Projects in DB | GET | `/projectAPI/findDBProjectsInDB` |
| Get Linked DB Locations | GET | `/projectAPI/getLinkedDBLocations` |
| Get Flows | GET | `/projectAPI/getFlows` |
| Get Project Logs | GET | `/projectAPI/getProjectLogs` |
| Is Project Name Valid | GET | `/projectAPI/isProjectNameValid` |
| Max Version | GET | `/projectAPI/maxVersion` |
| Find Stations to Match | POST | `/projectAPI/findStationsToMatch` |
| Create or Update Project | POST | `/projectAPI/createOrUpdateProject` |
| Create or Update with DB | POST | `/projectAPI/createOrUpdateWithDB` |
| Add Log Message | POST | `/projectAPI/addLogMessage` |
| Delete | POST | `/projectAPI/delete` |
| Purge Projects | POST | `/projectAPI/purge` |

### Project Searches (7 requests → ProjectManager :8888)
| File | Method | Endpoint |
|------|--------|----------|
| Get Project Searches | GET | `/rest/projectSearches` |
| Get Company Searches | GET | `/rest/projectSearches/company` |
| Count Search Results | GET | `/rest/projectSearches/{id}/count` |
| Get Search Projects | GET | `/rest/projectSearches/{id}/projects` |
| Get Search Stations | GET | `/rest/projectSearches/{id}/stations` |
| Export Search | GET | `/rest/projectSearches/{id}/export` |
| Export Search CSV | GET | `/rest/projectSearches/{id}/exportCsv` |

### Users API (5 requests → UsersMaster :8383)
| File | Method | Endpoint |
|------|--------|----------|
| Get Logged In User | GET | `/usersAPI/getLoggedInUser` |
| Get User | GET | `/usersAPI/getUser` |
| Get User by External ID | GET | `/usersAPI/getUserByExternalId` |
| Create or Update User | POST | `/usersAPI/createOrUpdate` |
| Delete User | POST | `/usersAPI/delete` |

### Company API (1 request → UsersMaster :8383)
| File | Method | Endpoint |
|------|--------|----------|
| Get Company | GET | `/companyAPI/getCompany` |

### Asset API (5 requests → AssetMaster/FileFort :8080)
| File | Method | Endpoint |
|------|--------|----------|
| Get Name | GET | `/assetAPI/getName` |
| Get Possible Asset Types | GET | `/assetAPI/getPossibleAssetTypes` |
| Get Possible Asset Details | GET | `/assetAPI/getPossibleAssetDetails` |
| Get Stations | GET/POST | `/assetAPI/getStations` |
| Get Assets | GET | `/assetAPI/getAssets` |

### Asset Creation API (2 requests → AssetMaster/FileFort :8080)
| File | Method | Endpoint |
|------|--------|----------|
| Create or Update | POST | `/assetCreationAPI/createOrUpdate` |
| Delete | POST | `/assetCreationAPI/delete` |

### Asset Search API (3 requests → AssetMaster/FileFort :8080)
| File | Method | Endpoint |
|------|--------|----------|
| Quick Find | GET | `/assetSearchAPI/quickFind` |
| Advanced Find | GET | `/assetSearchAPI/advancedFind` |
| Get Advanced Find Options | GET | `/assetSearchAPI/getAdvancedFindOptions` |

### Asset File API (6 requests → FileFort :8080)
| File | Method | Endpoint |
|------|--------|----------|
| Get Version Count | GET | `/assetFileAPI/getVersionCount` |
| Get Created Date | GET | `/assetFileAPI/getCreatedDate` |
| Get Updated Date | GET | `/assetFileAPI/getUpdatedDate` |
| Get File | GET | `/assetFileAPI/getFile` |
| Get Bytes | GET | `/assetFileAPI/getBytes` |
| Get Raw | GET | `/assetFileAPI/getRaw` |

### Station Linker API (5 requests → ProjectManager :8888)
| File | Method | Endpoint |
|------|--------|----------|
| Find Stations | GET | `/stationLinkerAPI/findStations` |
| Get Links | GET | `/stationLinkerAPI/getLinks` |
| Get Stations | GET | `/stationLinkerAPI/getStations` |
| Link Stations | GET | `/stationLinkerAPI/linkStations` |
| Unlink Stations | GET | `/stationLinkerAPI/unlinkStations` |

### Webhook API (4 requests → ProjectManager :8888)
| File | Method | Endpoint |
|------|--------|----------|
| Register | POST | `/webhookAPI/register` |
| Unregister | POST | `/webhookAPI/unregister` |
| Renew | POST | `/webhookAPI/renew` |
| View Webhooks | POST | `/webhookAPI/view` |

### Action API (2 requests → ProjectManager :8888)
| File | Method | Endpoint |
|------|--------|----------|
| Run Action | POST | `/actionAPI/run/{actionName}` |
| Reload Actions | GET | `/actionAPI/reload` |

### SPIDAdb API (21 requests → SPIDAdb :8181)
| File | Method | Endpoint |
|------|--------|----------|
| List Projects | GET | `/projects` |
| Show Project | GET | `/projects/{id}` |
| Save Project | POST | `/projects` |
| Update Project | PUT | `/projects/{id}` |
| Delete Project | DELETE | `/projects/{id}` |
| Create Project from Locations | POST | `/projects/createFromLocations` |
| List Locations | GET | `/locations` |
| Show Location | GET | `/locations/{id}` |
| List Designs | GET | `/designs` |
| Show Design | GET | `/designs/{id}` |
| Get Photo | GET | `/photos/{uuid}` |
| Get Detailed Results | GET | `/results/{resultId}` |
| List Results | POST | `/results` |
| Promote Designs | POST | `/status/promote` |
| Demote Locations | POST | `/status/demote` |
| Max Version | GET | `/versions/max` |
| List Client Files | GET | `/clientFiles` |
| Show Client File | GET | `/clientFiles/{id}` |
| Save Client File | POST | `/clientFiles` |
| Delete Client File | DELETE | `/clientFiles/{id}` |

## Schema Documentation Updated

The following schema docs were updated to match the actual controller source code:

| Doc File | Changes |
|----------|---------|
| `doc/apis/projectAPI.md` | Added missing params to 7 endpoints, added 2 new endpoint docs (maxVersion, purge), fixed title typo, added admin notes |
| `doc/apis/usersAPI.md` | Added `includeForeignCompanies` param to getUser, fixed createOrUpdate params description, fixed typo |
| `doc/apis/assetAPI.md` | Fixed `stations_ids` → `station_ids` typo, fixed duplicate header (was "Get Possible Asset Details" twice) |
| `doc/apis/assetCreationAPI.md` | Fixed getName URL, fixed `attachment` → `attachments` param, added AssetMaster/FileFort support notes |
| `doc/apis/stationLinkerAPI.md` | Documented `only_specified_details` param on getStations |
| `doc/apis/spidadbAPI.md` | Added query parameter tables for all list endpoints, added versions/max endpoint, added results POST list, added client files CRUD docs, fixed photos uuid reference |
| `doc/rest/projectSearches.md` | Fixed all URLs to include `/rest/`, fixed stations examples, added `details` param, added export/exportCsv/update endpoints, fixed delete URL |

No changes needed (verified correct): `assetSearchAPI.md`, `assetFileAPI.md`, `webhookAPI.md`, `actionAPI.md`, `companyAPI.md`
