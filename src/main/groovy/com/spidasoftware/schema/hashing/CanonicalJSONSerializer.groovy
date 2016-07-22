package com.spidasoftware.schema.hashing

import java.text.NumberFormat
import java.text.DecimalFormat

import org.apache.commons.lang.StringEscapeUtils
import net.sf.json.JSON
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import net.sf.json.JSONNull

class CanonicalJSONSerializer {

	private static final String START_OBJ = '{'
	private static final String END_OBJ = '}'
	private static final String SEP_OBJ = ','
	private static final String START_ARR = '['
	private static final String END_ARR = ']'
	private static final String SEP_ARR = ','
	private static final String PROP_SEP = ':'
	private static final String QUOTE = '"'
	
	private static final JSONNull jsonNull = JSONNull.getInstance()

	public static final Closure<ArrayBehavior> DEFAULT_ARRAY_DECIDER = { JSONArray array -> array.opt(0) instanceof Number ? ArrayBehavior.LIST : ArrayBehavior.SET }

	NumberFormat numberFormat = new DecimalFormat('0.############E0')
	Closure<ArrayBehavior> arrayDecider = DEFAULT_ARRAY_DECIDER

	CanonicalJSONSerializer() {
	}

	CanonicalJSONSerializer(Closure<ArrayBehavior> arrayDecider) {
		this.arrayDecider = arrayDecider
	}

	void serialize(Writer writer, JSON json) {
		if (json.isArray()) {
			serializeArray(writer, (JSONArray) json)
		} else if (jsonNull == json) {
			json.write(writer)
		} else { 
			serializeObject(writer, (JSONObject) json)
		}
	}

	String serialize(JSON json) {
		StringWriter writer = new StringWriter()

		serialize(writer,json)

		return writer.toString()
	}

	private void serializeAny(Writer writer, Object val) {
		if (val == null) {
			serializeNull(writer)
		} else if (val instanceof JSONArray) {
			serializeArray(writer, val)
		} else if (val instanceof JSONObject) {
			serializeObject(writer, val)
		} else if (val instanceof Number) {
			serializeNumber(writer, val)
		} else if (val instanceof String) {
			serializeString(writer, val)
		} else if (val instanceof Boolean) {
			serializeBoolean(writer, val)
		} else {
			throw new Exception("I don't know how to serialize a ${val.class}")
		}
	}

	private void serializeArray(Writer writer, JSONArray json) {
		ArrayBehavior behavior = arrayDecider(json)
		if (behavior == ArrayBehavior.SET) {
			serializeSet(writer, json)
		} else if (behavior == ArrayBehavior.LIST) {
			serializeList(writer, json)
		} else {
			json.write(writer)
		}
	}

	private String serializeObject(JSONObject json) {
		StringWriter writer = new StringWriter()

		serializeObject(writer,json)

		return writer.toString()
	}


	private void serializeObject(Writer writer, JSONObject json) {
		writer.write(START_OBJ)
		json.keys().sort().eachWithIndex({ key, i -> 
			if ( i > 0) {
				writer.write(SEP_OBJ)
			}
			serializeString(writer, key)
			writer.write(PROP_SEP)

			serializeAny(writer, json.get(key))
		})

		writer.write(END_OBJ)
	}

	private void serializeList(Writer writer, List array) {
		writer.write(START_ARR)
		array.eachWithIndex({ item, idx ->
			if (idx > 0) {
				writer.write(SEP_ARR)
			}
			serializeAny(writer,item)
		})
		writer.write(END_ARR)
	}

	private void serializeSet(Writer writer, JSONArray array) {
		serializeList(writer, sortJSONArray(array))
	}

	private void serializeString(Writer writer, String string) {
		writer.write(QUOTE)
		StringEscapeUtils.escapeJavaScript(writer, string)
		writer.write(QUOTE)
	}

	private void serializeNumber(Writer writer, Number number) {
		writer.write(numberFormat.format(number))
	}

	private void serializeBoolean(Writer writer, Boolean bool) {
		writer.write(bool.toString())
	}

	private void serializeNull(Writer writer) {
		writer.write(null.toString())
	}

	private List sortJSONArray(JSONArray array) {
		//If items are a JSONObject with an id sort them by if otherwise sort them as strings
		array.sort(this.&sortKey)
	}

	private String sortKey(Object obj) {
		if (obj instanceof JSONObject) {
			if (obj.id) {
				return obj.id.toString()
			} else {
				//TODO: This is extremely inefficient
				return serializeObject((JSONObject) obj)
			}
		} else {
			return obj.toString()
		}
	}

	enum ArrayBehavior { SET, LIST }
	
}
