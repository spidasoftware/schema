package com.spidasoftware.schema.utils

import groovy.util.logging.Log4j
import org.apache.commons.io.FilenameUtils

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Utilities for dealing with version number strings.
 */
@Log4j
class VersionUtils {

	static int CURRENT_VERSION = 2
	static Pattern pattern = Pattern.compile(/(\d+)(\.\d+)?(\.\d+)?(\.\d+)?/)

	/**
	 * Pattern matcher for versions strings.
	 * Accepts up to four numbers separated by a period.
	 * @param version
	 * @return
	 */
	static Matcher getMatcher(String version) {
		return pattern.matcher(version)
	}

	/**
	 * Converts a version string with 4 numbers into a list of 4 numbers.
	 * Example: 1.2.3.4 returns [1, 2, 3, 4]
	 * Example: 1 returns [1, 0, 0, 0]
	 * @param ver
	 * @return
	 */
	static List getNumbers(String version) {
		Matcher matcher = getMatcher(version)
		if (!matcher.matches()){
			throw new IllegalArgumentException("Malformed version:${version} (maximum of 4 different numbers separated by a period)")
		}

		def nums = []
		for(int i = 0; i < 4; i++){
			def groupVal = matcher.group(i + 1)
			if(groupVal == null){
				nums[i] = 0
			} else {
				nums[i] = Integer.parseInt(groupVal.replace(".",""))
			}
		}
		return nums
	}

	/**
	 * Checks if if first arg is newer than second arg
	 * Example: 0.5.2 is newer than 0.5.1
	 * @param version
	 * @param currentVersion
	 * @return
	 */
	static boolean isNewer(String version, String currentVersion) {
		//not the same and not older
		return version != currentVersion && !isOlder(version, currentVersion)
	}

	/**
	 * Checks if if first arg is older than second arg
	 * Example: 0.5.1 is older than 0.6
	 * @param version
	 * @param currentVersion
	 * @return
	 */
	static boolean isOlder(String version, String currentVersion) {

		if(version == currentVersion){
			return false
		}

		List versionNums = getNumbers(version);
		List currentVersionNums = getNumbers(currentVersion);

		for (int i = 0; i < versionNums.size(); i++){
			if (versionNums[i] != currentVersionNums[i]){
				return versionNums[i] < currentVersionNums[i]
			}
		}

		return false;
	}

	/**
	 * returns the jar file this class is in
	 * @return
	 */
	static File getJarFile(Class clazz){
		File file = null
		try {
			file = new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI())
		} catch (ex) {
			log.error("unable to get jar file path", ex)
		}
		log.debug("getSchemaJarFile() returning " + file)
		return file
	}

	/**
	 * Example: build/schema-0.5.1.jar returns 0.5.1
	 * Example: build/schema-0.5.1-SNAPSHOT.jar returns 0.5.1
	 * @param jarFile
	 * @return
	 */
	static String getVersionFromFileName(File file){
		if(!file){ return null }
		def name = FilenameUtils.removeExtension(file.name)
		return name.split("-").find { getMatcher(it).matches() }
	}

}
