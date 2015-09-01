
package org.solmix.wmix.parser;

import org.apache.commons.fileupload.FileItem;

/**
 * 过滤用户上传的文件。
 *
 */
public interface UploadedFileFilter extends ParameterParserFilter {
    /**
     * 过滤指定文件，如果返回<code>null</code>表示忽略该文件。
     * <p>
     * 注意，<code>file</code>可能是<code>null</code>。
     * </p>
     */
    FileItem filter(String key, FileItem file);
}
