/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion

import org.apache.commons.codec.binary.Base64

/**
 * Represents a photo that is part of a SPIDAdb Location.
 *
 * Photos are currently weird. The SPIDAdb api currently (3/31/14) just returns the base64 encoded
 * bytes without any context information like fileName or anything else. So, the uuid and fileName are
 * taken from me the parent Location. This needs to get fixed and updated at some point.
 */
public class SpidaDBPhoto implements SpidaDBProjectComponent {
    static final String NO_NAME = "unknown-photo"
    private Map json

    String base64Bytes
    String uuid
    String name

    SpidaDBPhoto(String base64Bytes) {
        this.base64Bytes = base64Bytes
        this.name = NO_NAME
    }

    public SpidaDBPhoto(String base64Bytes, String uuid, String name) {
        this.base64Bytes = base64Bytes
        this.uuid = uuid
        this.name = name
    }

    /**
     * Saves the photo to the specified directory using it's name as the file name. If the file already
     * exists, it will automatically get overwritten
     * @param parentDirectory
     * @return the File that got saved to.
     * @throws IOException
     */
    public File saveToDirectory(File parentDirectory) throws IOException {
        if (!parentDirectory.exists()) {
            parentDirectory.mkdirs()
        }
        byte[] bytes = Base64.decodeBase64(this.base64Bytes)
        File f = new File(parentDirectory, this.name)
        if (f.exists()) {
            f.delete()
        }
        FileOutputStream os = new FileOutputStream(f)
        os.write(bytes)
        os.close()
        return f
    }

    @Override
    public Map getMap() {
        return this.json
    }

    @Override
    public String getName() {
        return this.name
    }

    @Override
    public String getSpidaDBId() {
        return this.uuid
    }

    @Override
    public String getClientFileName() {
        return null
    }

    @Override
    public Date getDateModified() {
        return null  // photos are special
    }
}
