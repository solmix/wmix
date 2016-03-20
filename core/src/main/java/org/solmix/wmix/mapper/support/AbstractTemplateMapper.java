package org.solmix.wmix.mapper.support;

import org.solmix.commons.util.StringUtils;
import org.solmix.service.template.TemplateService;

public abstract class AbstractTemplateMapper extends AbstractMapper
{

    private TemplateService templateService;
    private String          templatePrefix;

    public TemplateService getTemplateService() {
        return templateService;
    }

    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }

    public String getTemplatePrefix() {
        return templatePrefix;
    }

    public void setTemplatePrefix(String templatePrefix) {
        this.templatePrefix = StringUtils.trimToNull(templatePrefix);
    }

}
