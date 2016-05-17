
package org.solmix.wmix.upload.support;

import org.apache.commons.fileupload.FileItem;

/**
 * 继承自commons-fileupload-1.2.1的同名类，改进了如下内容：
 * <ul>
 * <li>添加新的<code>keepFormFieldInMemory</code>参数。</li>
 * <li>创建新的DiskFileItem对象。</li>
 * </ul>
 *
 */
public class DiskFileItemFactory extends org.apache.commons.fileupload.disk.DiskFileItemFactory {
    private boolean keepFormFieldInMemory;

    public boolean isKeepFormFieldInMemory() {
        return keepFormFieldInMemory;
    }

    public void setKeepFormFieldInMemory(boolean keepFormFieldInMemory) {
        this.keepFormFieldInMemory = keepFormFieldInMemory;
    }

    @Override
    public FileItem createItem(String fieldName, String contentType, boolean isFormField, String fileName) {
        int sizeThreshold = getSizeThreshold();

        if (isFormField && (sizeThreshold == 0 || keepFormFieldInMemory)) {
            return new InMemoryFormFieldItem(fieldName, contentType, isFormField, fileName, sizeThreshold,
                                             keepFormFieldInMemory, getRepository());
        } else {
            return new DiskFileItem(fieldName, contentType, isFormField, fileName, sizeThreshold,
                                    keepFormFieldInMemory, getRepository());
        }
    }
}
