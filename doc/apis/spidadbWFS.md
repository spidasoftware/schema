SPIDAdb Web Feature Service
===========
There is a Web Feature Service (WFS) that allows for more extensive querying on the SPIDAcalc location and pole details that are stored in SPIDAdb.

You should familiarize yourself with the concepts in the [SPIDAdb API](apis/spidadbAPI.md) and the [SPIDAcalc Schema](https://github.com/spidasoftware/schema/tree/master/resources/schema/spidacalc/calc) before reading the WFS documentation. 

# General Concepts and Schema
![SPIDAdb WFS Schema](https://raw.githubusercontent.com/spidasoftware/schema/master/resources/schema/spidamin/asset/DbWFSSchema.png)
Above is the schema for the WFS, each table is a layer in the service and each row is an attribute for that layer.  Each item on a pole is served up in its own layer and has a reference to what pole it is on by the poleId attribute.  Each pole then has locationId which references the location that the pole is in.

All of the pole and attachment values stored in metric, so make sure that you account for this in your queries.

### Authentication
Every request to the WFS must contain the apiToken parameter from your Usersmaster profile page.

# Examples
To get the XML Schema Definition (XSD) for the service:
```curl https://example.spidastudio.com/spidadb/geoserver/wfs/DB?service=WFS&request=DescribeFeatureType&version=2.0.0&apiToken=abc123```

To query on the details we use [CQL](http://docs.geoserver.org/stable/en/user/tutorials/cql/cql_tutorial.html).
To find locations by label:
```curl https://example.spidastudio.com/spidadb/geoserver/wfs/DB?service=WFS&request=GetFeature&version=2.0.0&typeName=DB:location&apiToken=abc123&CQL_FILTER=label = '418'```

To find all poles between 40 and 45 feet inclusive:
