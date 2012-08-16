package com.spidasoftware.schema

import org.apache.log4j.Logger;
import org.codehaus.groovy.grails.web.json.*;
import java.util.regex.Pattern;
import java.util.HashMap;

/**
 * <p>A Validation class for JSON Schemas.</p>
 *
 * <p>This class can parse and validate JSON Objects according to a subset
 * of the JSON Schema Specification
 * (http://tools.ietf.org/html/draft-zyp-json-schema-02#section-5.1) as
 * follows: </p>
 * <ol>
 *  <li>A JSONSchema's schema must be parseable as a JSONObject.</li>
 *  <li>A JSONSchema can have the following properties:
 *   <ol>
 *    <li><p><b>type</b> - <i>REQUIRED.</i> the type of the object. Valid values
 *        are 'string','object' (for org.codehaus.groovy.grails.web.json.JSONObject),
 *        'array' (for org.codehaus.groovy.grails.web.json.JSONArray),
 *        'number','integer', 'boolean', 'null', and 'any'. </p>
 *         <p>Objects of type 'string' can have an additional parameter, 'choices',
 *          which is an array of accepted values.</p>
 *         <p>Objects of type 'number' or 'integer' can have the additional parameters
 *         'max' and 'min', which must be specified as numbers.</p></li>
 *    <li><b>properties</b> - <i>REQUIRED.</i> A JSONObject with named properties which are
 *        JSONSchemas.</li>
 *    <li><b>optional</b> - determines whether this property is optional</li>
 *    <li><b>items</b> - A JSONSchema giving the type and properties of the objects in a array,
 *        required if the parent JSONSchema is of type 'array'.
 *   </ol>
 *  </li>
 * </ol>
 */
public class JSONSchema {

	private JSONObject schema;
	private HashMap<String, JSONObject> referenceSchemas = new HashMap();
	public static Logger log = Logger.getLogger(JSONSchema.class);
	final static String Digits = "(\\p{Digit}+)";
	final static String HexDigits = "(\\p{XDigit}+)";
	final static String Exp = "[eE][+-]?" + Digits;
	final static String intRegex = "[-]?" + Digits;
	final static String numRegex = //MORE MAGIC
			("[\\x00-\\x20]*" + "[+-]?(" + "NaN|" + "Infinity|"
			+ "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|"
			+ "(\\.(" + Digits + ")(" + Exp + ")?)|" + "(("
			+ "(0[xX]" + HexDigits + "(\\.)?)|"
			+ "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")"
			+ ")[pP][+-]?" + Digits + "))" + "[fFdD]?))" + "[\\x00-\\x20]*");

	public JSONSchema(File schema) {
		this.schema = new JSONObject(schema.text)
	}

	public JSONSchema(String schema) {
		this.schema = new JSONObject(schema)
	}

	public JSONSchema(JSONObject schema) {
		this.schema = schema;
	}

	public JSONObject getSchema() {
		return schema;
	}

	@Override
	public String toString() {
		return schema.toString();
	}

	public boolean validate(File json) {
		return validate(schema, new JSONObject(json.text));
	}

	public boolean validate(String json) {
		return validate(schema, new JSONObject(json));
	}

	public boolean validate(JSONObject json) {
		return validate(schema, json);
	}

	/**
	 * Validates a JSONObject against a JSONSchema.
	 * JSONObjects are compared by type and by matching properties against the schema.
	 */
	public boolean validate(JSONObject schema, JSONObject json) throws JSONException {
		if (isJSONSchema(schema)) {
			schema = mergeReferences(schema);
			if (!isValidType(schema, json)) {
				return false;
			}
			if (!isJSONObject(schema.get("properties"))) {
				throw new JSONException("Given properties is not a JSONObject: " + schema.get("properties"));
			}
			JSONObject properties = schema.getJSONObject("properties");
			for (Object obj : properties.keySet()) { //for each (JSONObject)item in properties
				String name = (String) obj;
				Object item = properties.get(name);
				if (item.equals(JSONObject.NULL)) {
					if (json.has(name) && !isNull(json.get(name))) {
						return false;
					}
				} else {
					JSONObject schemaItem = mergeReferences((JSONObject) item);
					if (!json.has(name)) {
						if (!(schemaItem.has("optional")
								&& isBoolean(schemaItem.get("optional"))
								&& schemaItem.getBoolean("optional") == true)) {
							log.error("Missing required property: " + name);
							log.error("Object: " + json);
							return false;
						}
					} else {
						Object match = json.get(name);
						if (!isValidType(schemaItem, match)) {
							return false;
						}
						if (schemaItem.get("type").equals("object")) {
							if (!validate(schemaItem, (JSONObject) match)) {
								log.error("Failed to validate object: " + match);
								log.error("Schema: " + schemaItem);
								return false;
							}
						} else if (schemaItem.get("type").equals("array")) {
							if (!validate(schemaItem.getJSONObject("items"), (JSONArray) match)) {
								log.error("Array item failed to validate: " + match);
								log.error("Schema: " + schemaItem);
								return false;
							}
						}
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Validate a JSONArray against a schema object
	 */
	public boolean validate(JSONObject schema, JSONArray array) {
		int j = 0;
		while (j < array.length()) {
			if (!isValidType(schema, array.get(j))) {
				return false;
			}
			if (schema.get("type").equals("object")) {
				if (!validate(schema, array.getJSONObject(j))) {
					return false;
				}
			} else if (schema.get("type").equals("array")) {
				if (!validate(schema.getJSONObject("items"), array.getJSONArray(j))) {
					return false;
				}
			}
			j++;
		}
		return true;
	}

	/**
	 * Add schemas that are referenced in the master schema, to be accessed
	 * during validation.
	 */
	public void addReferenceSchema(String id, JSONObject schema) {
		referenceSchemas.put(id, schema);
	}

	/**
	 * Check whether loaded reference schemas contain a given schema
	 */
	public boolean hasReferenceSchema(String key) {
		return referenceSchemas.containsKey(key);
	}

	/**
	 * Return a stored reference schema
	 */
	public JSONObject getReferenceSchema(String key) {
		return referenceSchemas.get(key);
	}

	/**
	 * Return the mapping of all stored reference schemas and assigned names
	 */
	public HashMap getReferenceSchemas() {
		return referenceSchemas;
	}

	/**
	 * Retrieves a stored schema from the interchange
	 */
	public static String findReferenceSchema(String schemaName, String version) {
		String schemaRef = "";
		def file = new File(".")
		for(f1 in file.listFiles()){
			if(f1.isDirectory() && f1.name==version){
				for(f2 in f1.listFiles()){	
					if(f2.isDirectory()){
						for(f3 in f2.listFiles()){
							if(f3.name==schemaName+".json"){
								log.info f3.toString()
								return f3.text
							}
						}
					}
				}
			}
		}	
		return schemaRef;
	}

	/**
	 * Returns a schema with references replaced by a corresponding type and properties
	 */
	JSONObject mergeReferences(String s) throws JSONException {
		def schema = new JSONObject(s)
		if (!isJSONSchema(schema)) {
			throw new JSONException("Provided schema is invalid: " + schema.toString());
		}
		if (schema.has("extends")) {
			JSONObject extension = (JSONObject) schema.get("extends");
			if (hasReferenceSchema((String) extension.get("\$ref"))) {
				JSONObject ref = getReferenceSchema(extension.getString("\$ref"));
				JSONObject merge = new JSONObject();
				JSONObject properties = new JSONObject();
				JSONObject items = new JSONObject();
				if (ref.has("items")) {
					for (Object item : ((JSONObject) ref.get("items")).keySet()) {
						items.put((String) item, ref.getJSONObject("items").get(item.toString()));
					}
				}
				if (schema.has("items")) {
					for (Object item : ((JSONObject) schema.get("items")).keySet()) {
						items.put((String) item, schema.getJSONObject("items").get(item.toString()));
					}
				}
				if (ref.has("properties")) {
					for (Object item : ((JSONObject) ref.get("properties")).keySet()) {
						properties.put((String) item, ref.getJSONObject("properties").get(item.toString()));
					}
				}
				if (schema.has("properties")) {
					for (Object item : ((JSONObject) schema.get("properties")).keySet()) {
						properties.put((String) item, schema.getJSONObject("properties").get(item.toString()));
					}
				}
				if (ref.has("properties") || schema.has("properties")) {
					merge.put("properties", properties);
				}
				if (ref.has("items") || schema.has("items")) {
					merge.put("items", properties);
				}
				if (ref.has("type")) {
					merge.put("type", ref.get("type"));
				}
				if (schema.has("type")) {
					merge.put("type", schema.get("type"));
				}
				if (ref.has("optional")) {
					merge.put("optional", ref.getBoolean("optional"));
				}
				if (schema.has("optional")) {
					merge.put("optional", schema.getBoolean("optional"));
				}
				return merge;
			} else {
				throw new JSONException("This schema references other unknown schemas: "
						+ extension.getString("\$ref"));
			}
		}
		if (schema.has("\$ref")) {
			JSONObject merge = new JSONObject();
			if (hasReferenceSchema(schema.getString("\$ref"))) {
				JSONObject ref = getReferenceSchema(schema.getString("\$ref"));
				if (ref.has("type")) {
					merge.put("type", ref.get("type"));
				}
				if (ref.has("items")) {
					merge.put("items", ref.get("items"));
				}
				if (ref.has("properties")) {
					merge.put("properties", ref.get("properties"));
				}
				if (ref.has("optional")) {
					merge.put("optional", ref.getBoolean("optional"));
				}
				return merge;
			} else {
				throw new JSONException("This schema references other unknown schemas: "
						+ schema.getString("\$ref"));
			}
		}
		return schema;
	}

	private static boolean isValidType(String sItem, Object objectToValidate) {
		def schemaItem = new JSONObject(sItem)
		String message = null;
		if (!schemaItem.has("type")) {
			throw new JSONException("This item has no type! " + schemaItem.toString());
		}
		String type = schemaItem.getString("type");
		try {
			if (type.equals("object") && !isJSONObject(objectToValidate)) {
				message = "invalid object";

			} else if (type.equals("array") && !isJSONArray(objectToValidate)) {
				message = "invalid array";

			} else if (type.equals("string")) {
				if (!objectToValidate.toString().equals(objectToValidate)) {
					message = "invalid string";
				}

				if (schemaItem.has("choices") && message == null) {
					if (isJSONArray(schemaItem.get("choices"))) {
						JSONArray choices = schemaItem.getJSONArray("choices");
						boolean validChoice = false;
						for (int i = 0; i < choices.size(); i++) {
							if (isJSONObject(choices.get(i))) {
								JSONObject choice = choices.getJSONObject(i);
								if (choice.has("value")) {
									if (choice.get("value").toString().equals(objectToValidate)) {
										validChoice = true;
										break;
									}
								}
							} else {
								throw new JSONException("given choice is not a valid string value: " + choices.get(i).toString());
							}
						}
						if (!validChoice) {
							message = "invalid choice " + choices;
						}
					} else {
						throw new JSONException("String value choices given is not an array: " + schemaItem.get("choices").toString());
					}
				}
			} else if (type.equals("number")) {
				if (!Pattern.matches(numRegex, objectToValidate.toString())) {
					message = "invalid number";
				}
				if (message == null && !isInNumberConstraints(schemaItem, Double.parseDouble(objectToValidate.toString()))) {
					message = "number out of bounds";
				}

			} else if (type.equals("integer")) {
				if (!Pattern.matches(intRegex, objectToValidate.toString())) {
					message = "invalid integer";
				}
				if (message == null && !isInNumberConstraints(schemaItem, Double.parseDouble(objectToValidate.toString()))) {
					message = "integer out of bounds";
				}

			} else if (type.equals("boolean") && !isBoolean(objectToValidate)) {
				message = "invalid boolean";

			} else if (type.equals("null") && !isNull(objectToValidate)) {
				message = "invalid null";

			} else if (type.equals("any")) {
				message = null;

			}
		} catch (JSONException ex) {
			return false;
		}
		if (message != null) {
			log.error("message: " + message);
			log.error("object: " + objectToValidate);
			log.error("schema: " + schemaItem);
		}
		return message == null;
	}

	static boolean isInNumberConstraints(String s, double val) throws JSONException {
		def schema = new JSONObject(s)
		boolean inMax = true;
		boolean inMin = true;
		if (schema.has("max")) {
			if (Pattern.matches(numRegex, schema.get("max").toString())) {
				double max = Double.parseDouble(schema.get("max").toString());
				inMax = val <= max;
			} else {
				throw new JSONException("Given value for max is not a number: " + schema.get("max").toString());
			}
		}
		if (schema.has("min")) {
			if (Pattern.matches(numRegex, schema.get("min").toString())) {
				double min = Double.parseDouble(schema.get("min").toString());
				inMin = val >= min;
			} else {
				throw new JSONException("Given value for min is not a number: " + schema.get("min").toString());
			}
		}
		return inMin && inMax;
	}

	/**
	 * Shallow checking for whether the given object is a JSONSchema
	 */
	static boolean isJSONSchema(Object object) {
		if (isJSONObject(object)) { //TODO: add depth traversal
			JSONObject schema = new JSONObject(object.toString());
			if (schema.has("extends") || schema.has("\$ref")) {
				return true;
			}
			boolean typeMatch = schema.has("type");
			boolean propMatch = true;
			if (typeMatch && schema.get("type").equals("object")) {
				propMatch = schema.has("properties") && isJSONObject(schema.get("properties"));
			} else if (typeMatch && schema.get("type").equals("array")) {
				propMatch = schema.has("items") && isJSONObject(schema.get("items"));
			}
			return propMatch && typeMatch;
		}
		return false;
	}

	static boolean isJSONObject(Object object) {
		boolean val;
		try {
			val = new JSONObject(object.toString()) instanceof JSONObject;
		} catch (JSONException ex) {
			return false;
		}
		return val;
	}

	static boolean isJSONArray(Object object) {
		boolean val;
		try {
			val = new JSONArray(object.toString()) instanceof JSONArray;
		} catch (JSONException ex) {
			return false;
		}
		return val;
	}

	static boolean isBoolean(Object object) {
		if (object.equals(true)
				|| object.equals(false)
				|| object.equals("true")
				|| object.equals("false")) {
			return true;
		}
		return false;
	}

	static boolean isNull(Object object) {
		if (object.equals(JSONObject.NULL)
				|| object == null
				|| object.equals("null")
				|| object.equals("NULL")) {
			return true;
		}
		return false;
	}
}