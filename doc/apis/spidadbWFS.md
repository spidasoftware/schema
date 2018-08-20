SPIDAdb Web Feature Service
===========
There is a [Web Feature Service (WFS)](http://www.opengeospatial.org/standards/wfs) that allows for more extensive querying on the SPIDAcalc location and pole details that are stored in SPIDAdb.

You should familiarize yourself with the concepts in the [SPIDAdb API](apis/spidadbAPI.md) and the [SPIDAcalc Schema](https://github.com/spidasoftware/schema/tree/master/resources/schema/spidacalc/calc) before reading the WFS documentation. 

# General Concepts and Schema
![SPIDAdb WFS Schema](https://raw.githubusercontent.com/spidasoftware/schema/master/resources/schema/spidamin/asset/DbWFSSchema.png)
Above is the schema for the WFS, each table is a layer in the service and each row is an attribute for that layer.  Each item on a pole is served up in its own layer and has a reference to what pole it is on by the poleId attribute.  Each pole then has locationId which references the location that the pole is in.

All of the pole and attachment values stored in metric, so make sure that you account for this in your queries.

All of the date values are stored in as long values.  Which is the number of milliseconds since January 1, 1970, 00:00:00 GMT.

To query on the details we use [CQL](http://docs.geoserver.org/stable/en/user/tutorials/cql/cql_tutorial.html), all of the available attributes are able to be queried on.

### Authentication
The apiToken from your Usersmaster profile page is required as a parameter to authenticate to the WFS.

# Examples
To get the XML Schema Definition (XSD) for the service:  
```curl https://example.spidastudio.com/spidadb/geoserver/wfs/DB?service=WFS&request=DescribeFeatureType&version=2.0.0&apiToken=abc123```  
The response (with everything but the pole schema removed):  
```
<?xml version="1.0" encoding="UTF-8"?>
<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:DB="http://spida/db" xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:wfs="http://www.opengis.net/wfs/2.0" elementFormDefault="qualified" targetNamespace="http://spida/db">
   <xsd:import namespace="http://www.opengis.net/gml/3.2" schemaLocation="http://localhost:8080/geoserver/schemas/gml/3.2.1/gml.xsd" />
    ..........
    <xsd:complexType name="poleType">
      <xsd:complexContent>
         <xsd:extension base="gml:AbstractFeatureType">
            <xsd:sequence>
               <xsd:element maxOccurs="1" minOccurs="0" name="designType" nillable="true" type="xsd:string" />
               <xsd:element maxOccurs="1" minOccurs="0" name="locationId" nillable="true" type="xsd:string" />
               <xsd:element maxOccurs="1" minOccurs="0" name="locationLabel" nillable="true" type="xsd:string" />
               <xsd:element maxOccurs="1" minOccurs="0" name="clientFile" nillable="true" type="xsd:string" />
               <xsd:element maxOccurs="1" minOccurs="0" name="clientFileVersion" nillable="true" type="xsd:string" />
               <xsd:element maxOccurs="1" minOccurs="0" name="dateModified" nillable="true" type="xsd:long" />
               <xsd:element maxOccurs="1" minOccurs="0" name="glc" nillable="true" type="xsd:double" />
               <xsd:element maxOccurs="1" minOccurs="0" name="glcUnit" nillable="true" type="xsd:string" />
               <xsd:element maxOccurs="1" minOccurs="0" name="agl" nillable="true" type="xsd:double" />
               <xsd:element maxOccurs="1" minOccurs="0" name="aglUnit" nillable="true" type="xsd:string" />
               <xsd:element maxOccurs="1" minOccurs="0" name="species" nillable="true" type="xsd:string" />
               <xsd:element maxOccurs="1" minOccurs="0" name="class" nillable="true" type="xsd:string" />
               <xsd:element maxOccurs="1" minOccurs="0" name="length" nillable="true" type="xsd:double" />
               <xsd:element maxOccurs="1" minOccurs="0" name="lengthUnit" nillable="true" type="xsd:string" />
               <xsd:element maxOccurs="1" minOccurs="0" name="owner" nillable="true" type="xsd:string" />
               <xsd:element maxOccurs="1" minOccurs="0" name="id" nillable="true" type="xsd:string" />
            </xsd:sequence>
         </xsd:extension>
      </xsd:complexContent>
   </xsd:complexType>
   <xsd:element name="pole" substitutionGroup="gml:AbstractFeature" type="DB:poleType" />
   ..........
</xsd:schema>
```

To find locations by label:  
```curl https://example.spidastudio.com/spidadb/geoserver/wfs/DB?service=WFS&request=GetFeature&version=2.0.0&typeName=DB:location&apiToken=abc123&CQL_FILTER=label = '418'```  
The response:  
```
<wfs:FeatureCollection xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:wfs="http://www.opengis.net/wfs/2.0" xmlns:DB="http://spida/db" xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" numberMatched="4" numberReturned="4" timeStamp="2018-08-20T17:36:59.728Z" xsi:schemaLocation="http://www.opengis.net/gml/3.2 http://localhost:8080/geoserver/schemas/gml/3.2.1/gml.xsd http://spida/db http://localhost:8080/geoserver/wfs?service=WFS&version=2.0.0&request=DescribeFeatureType&typeName=DB%3Alocation http://www.opengis.net/wfs/2.0 http://localhost:8080/geoserver/schemas/wfs/2.0/wfs.xsd">
	<wfs:member>
		<DB:location gml:id="5a68e376cff47e000137789b">
			<DB:geographicCoordinate>
				<gml:Point srsName="http://www.opengis.net/gml/srs/epsg.xml#4326" srsDimension="2">
					<gml:pos>40.008289 -82.861893</gml:pos>
				</gml:Point>
			</DB:geographicCoordinate>
			<DB:id>5a68e376cff47e000137789b</DB:id>
			<DB:label>418</DB:label>
			<DB:projectId>5a68e376cff47e00013778a6</DB:projectId>
			<DB:projectName>Exchange File_Same Station Twice 502</DB:projectName>
			<DB:clientFile>SPIDA Power Company.client</DB:clientFile>
			<DB:clientFileVersion>8f0817e420f080bb6953a3ad07145c0a</DB:clientFileVersion>
			<DB:dateModified>1516823414319</DB:dateModified>
			<DB:mapNumber/>
			<DB:comments/>
			<DB:streetNumber/>
			<DB:street>Easy Street</DB:street>
			<DB:city>Gotham City</DB:city>
			<DB:county>County B</DB:county>
			<DB:state>NY</DB:state>
			<DB:zipCode/>
			<DB:user>admin@spidasoftware.com</DB:user>
		</DB:location>
	</wfs:member>
</wfs:FeatureCollection>
```

To find all poles between 40 and 45 feet inclusive:  
```curl https://example.spidastudio.com/spidadb/geoserver/wfs/DB?service=WFS&request=GetFeature&version=2.0.0&typeName=DB:pole&apiToken=abc123&CQL_FILTER=length >= 12.192 AND length <= 13.716```  
The response:  
```
<wfs:FeatureCollection xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:wfs="http://www.opengis.net/wfs/2.0" xmlns:DB="http://spida/db" xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" numberMatched="1302" numberReturned="1302" timeStamp="2018-08-20T17:48:02.156Z" xsi:schemaLocation="http://www.opengis.net/gml/3.2 http://localhost:8080/geoserver/schemas/gml/3.2.1/gml.xsd http://spida/db http://localhost:8080/geoserver/wfs?service=WFS&version=2.0.0&request=DescribeFeatureType&typeName=DB%3Apole http://www.opengis.net/wfs/2.0 http://localhost:8080/geoserver/schemas/wfs/2.0/wfs.xsd">
	<wfs:member>
		<DB:pole gml:id="5a68f736cff47e00013778fc">
			<DB:designType>Design 1</DB:designType>
			<DB:locationId>5a68f736cff47e00013778fd</DB:locationId>
			<DB:locationLabel>407</DB:locationLabel>
			<DB:clientFile>SPIDA Power Company.client</DB:clientFile>
			<DB:clientFileVersion>8f0817e420f080bb6953a3ad07145c0a</DB:clientFileVersion>
			<DB:dateModified>1516828470275</DB:dateModified>
			<DB:glc>0.9768480063706583</DB:glc>
			<DB:glcUnit>METRE</DB:glcUnit>
			<DB:agl>10.363199999999999</DB:agl>
			<DB:aglUnit>METRE</DB:aglUnit>
			<DB:species>Douglas Fir</DB:species>
			<DB:class>2</DB:class>
			<DB:length>12.192</DB:length>
			<DB:lengthUnit>METRE</DB:lengthUnit>
			<DB:owner>SPIDA Power Company</DB:owner>
			<DB:id>5a68f736cff47e00013778fc</DB:id>
		</DB:pole>
	</wfs:member>
</wfs:FeatureCollection>
```
