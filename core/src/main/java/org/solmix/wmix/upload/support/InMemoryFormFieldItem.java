
package org.solmix.wmix.upload.support;

import java.io.File;

/**
 * 用来存储form field的<code>FileItem</code>实现。
 * <p>
 * 避免了<code>DiskFileItem.finalize()</code>方法的开销。
 * </p>
 *
 */
public class InMemoryFormFieldItem extends AbstractFileItem {
    private static final long serialVersionUID = -103002370072467461L;

    public InMemoryFormFieldItem(String fieldName, String contentType, boolean isFormField, String fileName,
                                 int sizeThreshold, boolean keepFormFieldInMemory, File repository) {
        super(fieldName, contentType, isFormField, fileName, sizeThreshold, keepFormFieldInMemory, repository);
    }
}
