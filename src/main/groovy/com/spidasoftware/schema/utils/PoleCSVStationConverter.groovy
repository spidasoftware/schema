package com.spidasoftware.schema.utils

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

class PoleCSVStationConverter {

	public static console
	public static fileHeaderToSchemaPropertyMap = [:]
	public static schemaProperties = []
	public static headers

	private static defaultMapping = ["s", "i", "n", "t", "g"]

	public static gets = {
		println it
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
		String readInput = br.readLine()
		return readInput
	}

	public static void main(String[] args) {
		Random generator = new Random()
		if(args==null || args.length==0){
			println "usage: 'java -cp schema.jar com.spidasoftware.schema.utils.PoleCSVStationConverter path/to/import/file"
			println "   or"
			println "usage: 'gradlew csv -Pfile=path/to/import/file"
			return
		}

		def poleSchema = JSONObject.fromObject(new File("resources/v1/schema/spidamin/asset/standard_details/pole_asset.schema").text)
		for(p in poleSchema.get("properties")){
			schemaProperties.add p.key
		}


		schemaProperties = schemaProperties.sort()
		File input = new File(args[0])
		if(!input.exists()){
			println "Input file (${input.absolutePath}) does not exist."
			return 
		}

		File jsonFile = new File(input.parentFile, input.name+".json")
		if(!jsonFile.exists()) jsonFile.createNewFile()
		jsonFile.text = ""
		println ""

		def companyId = gets("What is the company id these poles are associated with? : ")

		// def companyId = this.console.readLine("What is the company id these poles are associated with? : ")
		try {
			companyId = Integer.parseInt(companyId)
		} catch(Exception e) {
			println "Company ID not a valid number"
			return
		}

		def lineNumber = 0

		input.eachLine{line->
			line = line.replaceAll(",", ", ")
			if(lineNumber==0){
				headers = line.tokenize(",")
				for(header in headers){
					def choice = readInput("Which schema value would you like to map '${header}'?")
					if(defaultMapping.contains(choice)){
						fileHeaderToSchemaPropertyMap.put headers.indexOf(header), choice
					}else{
						fileHeaderToSchemaPropertyMap.put headers.indexOf(header), Integer.parseInt(choice)-1
					}
				}
				println "Final Mapping:"
				println "${fileHeaderToSchemaPropertyMap}"
			}else{
				println line
				def values = line.tokenize(",")
				println values
				println "   reading line: $lineNumber that has ${values.size()} values."
				def stationJSON = new JSONObject()
				stationJSON.primaryAssetType = "POLE"
				stationJSON.dataProviderId = companyId
				stationJSON.primaryAssetOwnerId = companyId
				stationJSON.stationAssets = new JSONArray()
				stationJSON.assetTypes = JSONArray.fromObject(["POLE"])

				def poleTags = new JSONArray()
				def poleDetails = new JSONArray()
				def poleJSON = new JSONObject(["primaryAsset":true, "ownerId":companyId, "assetAttachments":[], "assetType":"POLE"])
				def latitude
				def longitude
				fileHeaderToSchemaPropertyMap.each{k,v->
					def value = values.get(k).toString().trim()
					if(v=="s" || value=="" || value==null){
						//Do nothing
					}else if(v=="i"){
						poleTags.add JSONObject.fromObject(["value":value, "name":headers.get(k), "primary":true])
					}else if(v=="g"){
						try {
							longitude = Double.parseDouble(value)
						} catch(Exception e) {
							println "Longitude not a valid number"
							return
						}
					}else if(v=="t"){
						try {
							latitude = Double.parseDouble(value)
						} catch(Exception e) {
							println "Latitude not a valid number"
							return
						}
					}else if(v=="n"){
						poleDetails.add JSONObject.fromObject(["value":value, "name":headers.get(k)])
					}else{
						poleDetails.add JSONObject.fromObject(["value":value, "name":schemaProperties.get(v)])
					}
				}
				if(longitude==null) {
					longitude = generator.nextDouble() * 10.0-80.0
				}
				if(latitude==null){
					latitude = generator.nextDouble() * 10.0	+ 40.0
				}
				poleJSON.assetDetails = poleDetails
				poleJSON.assetTags = poleTags
				stationJSON.stationAssets.add poleJSON
				stationJSON.geometry = JSONObject.fromObject(["type":"Point", "coordinates":[longitude, latitude]])
				jsonFile.append stationJSON.toString()+"\n"
			}
			lineNumber++
		}
	}

	private static getValueFromKey(def key, def values){
		if(key.isNumber()){
			key = Integer.parseInt(key)
		}else{
			key = fileHeaderToSchemaPropertyMap.find{it.value=="g"}
		}
		if(key){
			def headerIndex = headers.indexOf(key)
			return values.get(headerIndex)
		}
	}

	public static readInput(question){
		def invalid = true
		def output
		while(invalid){
			def completeLine = " > "+question.toString().trim()
			println "  s: skip (default)"
			println "  i: primary ID"
			println "  n: include as non-searchable"
			println "  t: latitude"
			println "  g: longitude"
			println " ------------------------"
			def options = []
			options.addAll defaultMapping
			schemaProperties.eachWithIndex{p, index->
				if(!fileHeaderToSchemaPropertyMap.values().contains(index)){
					println "  ${index+1}: $p"
					options.add ""+(index+1)
				}
			}
			output = gets(completeLine+" : ")
			if(output=="") output = "s"
			if(output!="" && options.contains(output)){
				invalid=false
			}else{
				println "That value is not in the available choices."
			}
		}
		return output
	}
}




