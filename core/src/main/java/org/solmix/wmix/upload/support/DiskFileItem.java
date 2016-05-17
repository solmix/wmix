
package org.solmix.wmix.upload.support;

import java.io.File;

public class DiskFileItem extends AbstractFileItem {
    private static final long serialVersionUID = 4225039123863446602L;

    public DiskFileItem(String fieldName, String contentType, boolean isFormField, String fileName, int sizeThreshold,
                        boolean keepFormFieldInMemory, File repository) {
        super(fieldName, contentType, isFormField, fileName, sizeThreshold, keepFormFieldInMemory, repository);
    }

    /** Removes the file contents from the temporary storage. */
    @Override
    protected void finalize() throws Throwable {
        try {
            File outputFile = dfos.getFile();

            if (outputFile != null && outputFile.exists()) {
                outputFile.delete();
            }
        } finally {
            super.finalize();
        }
    }
}
