package com.spidasoftware.schema.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by jeremy on 4/15/14.
 */
class VersionUtils {

	/**
	 * the pattern matcher for versions
	 * @param version
	 * @return
	 */
	static Matcher getMatcher(String version) {
		return Pattern.compile(/(\d+)\.?(\d+)?\.?(\d+)?\.?(\d+)?/).matcher(version)
	}

	/**
	 * Converts a version string with 4 numbers into a list of 4 numbers
	 * Example: 1.2.3.4 returns [1, 2, 3, 4]
	 * Example: 1 returns [1, 0, 0, 0]
	 * @param ver
	 * @return
	 */
	static List getNumbers(String version) {
		Matcher matcher = getMatcher(version)
		if (!matcher.matches()){ throw new IllegalArgumentException("Malformed version:${version} (maximum of 4 different numbers separated by a period)")	}

		def nums = []
		for(int i = 0; i < matcher.groupCount(); i++){
			def groupVal = matcher.group(i + 1)
			if(groupVal == null){
				nums[i] = 0
			} else {
				nums[i] = Integer.parseInt(groupVal)
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

}
