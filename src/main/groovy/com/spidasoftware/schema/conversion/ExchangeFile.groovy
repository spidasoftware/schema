/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.Files
import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.tools.ant.BuildException
/**
 *
 * Represents a .exchange.spida file and has methods for creating and opening them. Basically just takes care of
 * zipping the file and making sure contents are named correctly. There are static methods that can be used to easily
 * create instances of this class that are ready to be read or written.
 *
 * More precisely, this class represents a directory on the filesystem that contains all the makings of
 * an exchange file. The createFromZipFile method is just an easy way to unzip an exchange file to a
 * temp directory.
 *
 * The exchange file is simply a zip file, with the project json at it's root:
 * <pre>
 *     my-project.exchange.spida (root)
 *     +--- project.json
 *     +--- Photos
 *         \
 *         +--- photo1.jpg
 *         +--- photo2.jpg
 *         ...
 * </pre>
 * The project json should always be named "project.json", and the photos directory must always be named "Photos"
 * (capital P). The names of photos will be resolved relative to the Photos directory. So, if a Location has an image
 * with the url, "photo1.jpg", then the photo file would resolve to "Photos/photo1.jpg".
 * These files can be opened by using the normal open dialog.
 *
 */
@Slf4j
public class ExchangeFile {
	public static final String EXT = 'exchange.spida'
	public static final String EXT_WITH_DOT = ".${EXT}"
	public static final String PROJECT_FILE_NAME = 'project.json'
	public static final String PHOTO_DIR_NAME = 'Photos'
	public static final String RESULTS_DIR_NAME = 'Results'

	/**
	 * A Map that should conform to the SpidaCalc project schema. Reference here so it can be cached
	 */
	Map projectJSON

	/**
	 * directory used to unzip file contents to and to write to prior to zipping. may not be null.
	 */
	File tempDir

	/**
	 * located at tempDir/project.json
	 */
	File projectJSONFile

	/**
	 * located at tempDir/Photos
	 */
	File photoDir

	/**
	 * located at tempDir/Results
	 */
	File resultsDir

	/**
	 * protected constructor. Use one of the static methods instead.
	 * @param tempDir
	 */
	protected ExchangeFile(File tempDir) {
		this.tempDir = tempDir
		this.projectJSONFile = new File(tempDir, PROJECT_FILE_NAME)
		this.photoDir = new File(tempDir, PHOTO_DIR_NAME)
		this.resultsDir = new File(tempDir, RESULTS_DIR_NAME)
	}

	/**
	 * Creates an ExchangeFile from a file on disk. This file must follow the spida exchange format.
	 * Unpacks the zip file immediately.
	 *
	 * @param zipFile a spida exchange file that's already zipped up.
	 * @return a new ExchangeFile that provides access to the project JSON and photos.
	 */
	static ExchangeFile createFromZipFile(File zipFile) {
		File workingTemp = Files.createTempDir()
		AntBuilder ant = new AntBuilder()
		ant.unzip(src: zipFile.getCanonicalPath(), dest: workingTemp.getCanonicalPath(), encoding: 'UTF-8')
		return createFromDirectory(workingTemp)
	}

	/**
	 * Creates a new ExchangeFile from project json and (optionally) photos directory.
	 * This can then easily be written out by calling writeTo()
	 *
	 * @param projectJson a json object representing the project
	 * @param photoDir an optional directory containing the photos for the project. Everything in here
	 * @param resultsCopier The results directory will be the Closure argument, should copy the detailed results into the directory.
	 * will simply be added to the photos directory in the zip archive.
	 * @return the exchangeFile, ready to be written
	 */
	static ExchangeFile createFromProjectJSON(Map projectJson, Collection<File> photoFiles = null, Closure resultsCopier = null) {
		ExchangeFile exf = new ExchangeFile(Files.createTempDir())
		exf.setProjectJSON(projectJson)
		ObjectMapper mapper = new ObjectMapper()
		mapper.writeValue(exf.getProjectJSONFile(), projectJson)
		log.debug("Creating exchange file from JSON")
		copyPhotoFilesToFolder(exf.getPhotoDir(), photoFiles)
		if(resultsCopier != null) {
			exf.resultsDir.mkdir()
			resultsCopier(exf.resultsDir)
		}
		return exf
	}

	static void copyPhotoFilesToFolder(File photoDir, Collection<File> photoFiles) {
		if (photoFiles) {
			photoDir.mkdir()
			log.debug("Copying all photos to: ${photoDir}")
			photoFiles.each{File photo->
				File target = new File(photoDir, photo.name)
				Files.copy(photo, target)
			}
		}
	}

	/**
	 * If you've already got the json and photos arranged in a directory, then this is the method for you!
	 *
	 * @param dir directory that contains a project.json file and an optional Photos directory
	 * @return exchange file, ready to be written
	 */
	static ExchangeFile createFromDirectory(File dir) {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("File is must point to a directory and it must exist")
		}
		return new ExchangeFile(dir)
	}

	Map getProjectJSON() {
		if (!projectJSON && getProjectJSONFile().isFile()) {
			log.debug("parsing exchange file json")

			// there are bugs in jsonslurper that break large values
			ObjectMapper objectMapper = new ObjectMapper()
			projectJSON = objectMapper.readValue(getProjectJSONFile(), LinkedHashMap)
		}
		return projectJSON
	}

	List<File> getAllPhotos() {
		if (getPhotoDir().isDirectory()) {
			return getPhotoDir().listFiles().toList()
		} else {
			return []
		}

	}

	/**
	 * Writes the ExchangeFile to the specified file, overwriting if it already exists.
	 *
	 * @param file location to save to
	 * @return the file that was written to, for convenience.
	 */
	File writeTo(File file) throws IOException {
		AntBuilder ant = new AntBuilder()
		String src = tempDir.getCanonicalPath()
		String dest = file.getCanonicalPath()
		if (file.exists()) {
			file.delete()
		}
		try {
			ant.zip(basedir: src, destfile: dest)
		} catch (BuildException e) {
			throw new IOException("Error writing to Exchange file: ${dest}", e)
		}
		return file
	}

	/**
	 * Deletes the temporary directory and all the files within it.
	 * No zip files will be harmed in the making of this method, but this exchange file was
	 * created using createFromDirectory(File) then that directory would be deleted.
	 */
	void delete(){
		this.tempDir?.deleteDir()
	}

}