
package org.solmix.wmix.parser;

/**
 * 过滤用户输入的参数值。
 *
 */
public interface ParameterValueFilter extends ParameterParserFilter {
    /**
     * 过滤指定值，如果返回<code>null</code>表示忽略该值。
     * <p>
     * 注意，<code>value</code>可能是<code>null</code>。
     * </p>
     */
    String filter(String key, String value, boolean isHtml);
}
