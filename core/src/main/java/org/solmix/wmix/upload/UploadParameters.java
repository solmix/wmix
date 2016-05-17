/*
 * Copyright 2015 The Solmix Project
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */
package org.solmix.wmix.upload;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.solmix.commons.util.ArrayUtils;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.HumanReadableSize;
import org.solmix.commons.util.SysInfoUtils;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月31日
 */

public class UploadParameters implements UploadConfiguration
{
    private File repository;
    private HumanReadableSize sizeMax       = new HumanReadableSize(SIZE_MAX_DEFAULT);
    private HumanReadableSize fileSizeMax   = new HumanReadableSize(FILE_SIZE_MAX_DEFAULT);
    private HumanReadableSize sizeThreshold = new HumanReadableSize(SIZE_THRESHOLD_DEFAULT);
    private boolean keepFormFieldInMemory;
    private String  fileNameKey[];

    @Override
    public File getRepository() {
        return repository;
    }

    public void setRepository(File repository) {
        this.repository = repository;
    }

    @Override
    public HumanReadableSize getSizeMax() {
        return sizeMax;
    }

    public void setSizeMax(HumanReadableSize sizeMax) {
        this.sizeMax = Assert.assertNotNull(sizeMax, "sizeMax");
    }

    public void setSizeMax(long sizeMax) {
        setSizeMax(new HumanReadableSize(sizeMax));
    }

    @Override
    public HumanReadableSize getFileSizeMax() {
        return fileSizeMax;
    }

    public void setFileSizeMax(HumanReadableSize fileSizeMax) {
        this.fileSizeMax = Assert.assertNotNull(fileSizeMax, "fileSizeMax");
    }

    public void setFileSizeMax(long fileSizeMax) {
        setFileSizeMax(new HumanReadableSize(fileSizeMax));
    }

    @Override
    public HumanReadableSize getSizeThreshold() {
        return sizeThreshold;
    }

    public void setSizeThreshold(HumanReadableSize sizeThreshold) {
        this.sizeThreshold = Assert.assertNotNull(sizeThreshold, "sizeThreshold");
    }

    public void setSizeThreshold(int sizeThreshold) {
        this.sizeThreshold = new HumanReadableSize(sizeThreshold);
    }

    @Override
    public boolean isKeepFormFieldInMemory() {
        return keepFormFieldInMemory;
    }

    public void setKeepFormFieldInMemory(boolean keepFormFieldInMemory) {
        this.keepFormFieldInMemory = keepFormFieldInMemory;
    }

    @Override
    public String[] getFileNameKey() {
        return fileNameKey;
    }

    public void setFileNameKey(String[] fileNameKey) {
        this.fileNameKey = fileNameKey;
    }

    /** 设置默认值。 */
    public void applyDefaultValues() {
        if (sizeThreshold.getValue() == 0) {
            keepFormFieldInMemory = true;
        }

        if (repository == null) {
           
            repository = new File( SysInfoUtils.getUserInfo().getTempDir());
        }

        if (!repository.exists() && !repository.mkdirs()) {
            throw new IllegalArgumentException("Could not create repository directory for file uploading: "
                                               + repository);
        }

        if (ArrayUtils.isEmptyArray(fileNameKey)) {
            fileNameKey = new String[] { "filename" };
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || !(obj instanceof UploadParameters)) {
            return false;
        }

        UploadParameters other = (UploadParameters) obj;

        if (repository == null) {
            if (other.repository != null) {
                return false;
            }
        } else if (!repository.equals(other.repository)) {
            return false;
        }

        if (sizeMax != other.sizeMax) {
            return false;
        }

        if (fileSizeMax != other.fileSizeMax) {
            return false;
        }

        if (sizeThreshold != other.sizeThreshold) {
            return false;
        }

        if (keepFormFieldInMemory != other.keepFormFieldInMemory) {
            return false;
        }

        if (!Arrays.equals(fileNameKey, other.fileNameKey)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        Map<String,Object> mb = new HashMap<String, Object>();

        mb.put("Repository Path", getRepository());
        mb.put("Maximum Request Size", getSizeMax());
        mb.put("Maximum File Size", getFileSizeMax());
        mb.put("Threshold before Writing to File", getSizeThreshold());
        mb.put("Keep Form Field in Memory", isKeepFormFieldInMemory());
        mb.put("File Name Key", getFileNameKey());

        return mb.toString();
    }
}
