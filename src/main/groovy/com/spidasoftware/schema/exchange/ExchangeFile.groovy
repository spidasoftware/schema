package com.spidasoftware.schema.exchange

import com.google.common.io.Files
import net.sf.json.JSON
import net.sf.json.JSONObject
import org.apache.log4j.Logger
import org.apache.tools.ant.BuildException

import javax.print.attribute.standard.Compression


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

public class ExchangeFile {
	private static final Logger log = Logger.getLogger(ExchangeFile)
	public static final String EXT = 'exchange.spida'
	public static final String EXT_WITH_DOT = ".${EXT}"
	public static final String PROJECT_FILE_NAME = 'project.json'
	public static final String PHOTO_DIR_NAME = 'Photos'

	/**
	 * A JSONObject that should conform to the SpidaCalc project schema. Reference here so it can be cached
	 */
	JSONObject projectJSON

	/**
	 * directory used to unzip file contents to and to write to prior to zipping. may not be null.
	 */
	final File tempDir

	/**
	 * located at tempDir/project.json
	 */
	final File projectJSONFile

	/**
	 * located at tempDir/Photos
	 */
	final File photoDir

	/**
	 * protected constructor. Use one of the static methods instead.
	 * @param tempDir
	 */
	protected ExchangeFile(File tempDir) {
		this.tempDir = tempDir
		this.projectJSONFile = new File(tempDir, PROJECT_FILE_NAME)
		this.photoDir = new File(tempDir, PHOTO_DIR_NAME)
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
	 * will simply be added to the photos directory in the zip archive.
	 * @return the exchangeFile, ready to be written
	 */
	static ExchangeFile createFromProjectJSON(JSONObject projectJson, Collection<File> photoFiles = null) {
		ExchangeFile exf = new ExchangeFile(Files.createTempDir())
		exf.setProjectJSON(projectJson)
		exf.getProjectJSONFile() << projectJson.toString(4)
		log.debug("Creating exchange file from JSON")
		if (photoFiles) {
			File photoDir = exf.getPhotoDir()
			photoDir.mkdir()
			log.debug("Copying all photos to: ${photoDir}")
			photoFiles.each{File photo->
				File target = new File(photoDir, photo.name)
				Files.copy(photo, target)
			}
		}
		return exf
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

	JSONObject getProjectJSON() {
		if (!projectJSON && getProjectJSONFile().isFile()) {
			log.debug("parsing exchange file json")
			projectJSON = JSONObject.fromObject(getProjectJSONFile().getText("UTF-8"))
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