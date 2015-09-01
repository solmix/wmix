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

package org.solmix.wmix.upload.support;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.HumanReadableSize;
import org.solmix.wmix.upload.SizeLimitExceededException;
import org.solmix.wmix.upload.UploadException;
import org.solmix.wmix.upload.UploadParameters;
import org.solmix.wmix.upload.UploadService;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年8月31日
 */

public class DefaultUploadService implements UploadService
{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultUploadService.class);

    private final UploadParameters params = new UploadParameters();

    private ServletFileUpload fileUpload;

    private boolean initialized;

    protected void init() {
        params.applyDefaultValues();
        LOG.info("Upload Parameters: {}", params);

        fileUpload = getFileUpload(params, false);
    }

    @Override
    public File getRepository() {
        return params.getRepository();
    }

    @Override
    public HumanReadableSize getSizeMax() {
        return params.getSizeMax();
    }

    @Override
    public HumanReadableSize getFileSizeMax() {
        return params.getFileSizeMax();
    }

    @Override
    public HumanReadableSize getSizeThreshold() {
        return params.getSizeThreshold();
    }

    @Override
    public boolean isKeepFormFieldInMemory() {
        return params.isKeepFormFieldInMemory();
    }

    @Override
    public String[] getFileNameKey() {
        return params.getFileNameKey();
    }

    @Override
    public boolean isMultipart(HttpServletRequest request) {
        return org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent(request);
    }

    @Override
    public FileItem[] parseRequest(HttpServletRequest request) {
        return parseRequest(request, null);
    }

    @Override
    public FileItem[] parseRequest(HttpServletRequest request, UploadParameters params) {
        assertInitialized();
        ServletFileUpload fileUpload;

        if (params == null || params.equals(this.params)) {
            fileUpload = this.fileUpload;
        } else {
            fileUpload = getFileUpload(params, true);
        }

        List<?> fileItems;
        try {
            fileItems = fileUpload.parseRequest(request);
        } catch (FileUpload.SizeLimitExceededException e) {
            throw new SizeLimitExceededException(e);
        } catch (FileUpload.FileSizeLimitExceededException e) {
            throw new SizeLimitExceededException(e);
        } catch (FileUploadException e) {
            throw new UploadException(e);
        }

        return fileItems.toArray(new FileItem[fileItems.size()]);
    }

    /** 是否已经初始化。 */
    public boolean isInitialized() {
        return initialized;
    }

    public void assertInitialized() {
        if (!initialized) {
            throw new IllegalStateException(String.format("Bean instance of %s has not been initialized yet.", getClass().getName()));
        }
    }

    /** 根据参数创建<code>FileUpload</code>对象。 */
    private ServletFileUpload getFileUpload(UploadParameters params, boolean applyDefaultValues) {
        if (applyDefaultValues) {
            params.applyDefaultValues();
            LOG.debug("Upload Parameters: {}", params);
        }

        // 用于生成FileItem的参数
        DiskFileItemFactory factory = new DiskFileItemFactory();

        factory.setRepository(params.getRepository());
        factory.setSizeThreshold((int) params.getSizeThreshold().getValue());
        factory.setKeepFormFieldInMemory(params.isKeepFormFieldInMemory());

        // 用于解析multipart request的参数
        ServletFileUpload fileUpload = new ServletFileUpload(factory);

        fileUpload.setSizeMax(params.getSizeMax().getValue());
        fileUpload.setFileSizeMax(params.getFileSizeMax().getValue());
        fileUpload.setFileNameKey(params.getFileNameKey());

        return fileUpload;
    }
}
