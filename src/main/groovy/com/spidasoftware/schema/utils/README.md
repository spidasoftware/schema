PoleCSVStationConverter
----------------------

Convert poles that are in a CSV into the JSON schema that can be imported into SPIDAMin.

#### Usage

    mvn exec:java -Dexec.mainClass="com.spidasoftware.schema.utils.PoleCSVStationConverter" -Dexec.args="/path/to/file.csv"

This will ask you mapping questions then it will create a file next to the csv file that is a .json file to be used with the API.