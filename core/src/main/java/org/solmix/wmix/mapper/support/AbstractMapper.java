package org.solmix.wmix.mapper.support;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.ProductionAware;
import org.solmix.wmix.mapper.Mapper;


public abstract class AbstractMapper implements Mapper,ProductionAware
{
    private static final Logger LOG  = LoggerFactory.getLogger(AbstractMapper.class);
    private Boolean cacheEnabled;
    private boolean productionMode = true;
    private Map<String, String> cache;
    @Override
    public String map(String name) {
        name = StringUtils.trimToNull(name);
        if (name == null) {
            return null;
        }
        
        String mappedName = null;

        if (isCacheEnabled()) {
            mappedName = cache.get(name);

            // 如果cache中已经有值了，则直接返回。
            // 注意，cache中的空字符串值代表null。
            if (mappedName != null) {
                return StringUtils.trimToNull(mappedName);
            }
        }

        LOG.trace("doMapping(\"{}\")", name);

        mappedName = doMapping(name);

        LOG.debug("doMapping(\"{}\") returned: ", name, mappedName);

        // 注意，可以cache值为null的结果（将null转成空字符串并保存）
        if (isCacheEnabled()) {
            cache.put(name, StringUtils.trimToEmpty(mappedName));
        }

        return mappedName;
    }
    public Boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(Boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    /**
     * 取得默认的<code>cacheEnabled</code>值。
     * <p>
     * 默认情况下取决于当前是否为生产模式。当<code>productionMode</code>为<code>true</code>
     * 时，打开cache。子类可以改变此行为。
     * </p>
     */
    protected boolean isCacheEnabledByDefault() {
        return isProductionMode();
    }

    public boolean isProductionMode() {
        return productionMode;
    }
    @Override
    public void setProduction(boolean productionMode) {
       this.productionMode=productionMode;
        
    }
    
    protected abstract String doMapping(String name);

}
