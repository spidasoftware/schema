package com.spidasoftware.schema.conversion

import org.junit.Test

class PoleCSVStationConverterTest {

	@Test
	public void toJSON() throws Exception {
		File file = new File("build/test.csv");
		if(!file.exists()) file.createNewFile()
		file.text = "POLEID,LAT,LNG,HEIGHT,CLASS\n"
		file.append("2,40.00,100.00,40,4\n")
		PoleCSVStationConverter.gets = {input->
			println input
			if(input.trim()=="What is the company id these poles are associated with?"){
				return "0"
			}else if(input.contains("'LAT'?:")){
				return "t"
			}else if(input.contains("'LNG'?:")){
				return "g"
			}else if(input.contains("'POLEID'?:")){
				return "i"
			}else if(input.contains("'HEIGHT'?:")){
				return "6"
			}else{
				return "n"
			}
		}
		PoleCSVStationConverter.main(file.absolutePath)
		def output = new File("build/test.csv.json").text
		file.delete()
		assert output!=null
	}

}
