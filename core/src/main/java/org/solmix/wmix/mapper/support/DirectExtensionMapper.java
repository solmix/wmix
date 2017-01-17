package org.solmix.wmix.mapper.support;

import org.solmix.commons.util.ArrayUtils;
import org.solmix.commons.util.StringUtils;

public class DirectExtensionMapper extends AbstractMapper {

	private String type;
	private String prefix;

	@Override
	public String getType() {
		return type;
	}

	public static final String TEMPLATE_NAME_SEPARATOR = "/";
	public static final String NAME_SEPARATOR = ",/";

	public void setType(String type) {
		this.type = type;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public Boolean isCacheEnabled() {
		return false;
	}

	@Override
	protected boolean isCacheEnabledByDefault() {
		return false;
	}

	@Override
	public String doMapping(String name) {
		String[] parts = StringUtils.split(name, NAME_SEPARATOR);

		if (ArrayUtils.isEmptyArray(parts)) {
			throw new IllegalArgumentException("mapping name is empty");
		}

		boolean withPrefix = !StringUtils.isEmpty(getPrefix());
		String firstTemplateName = StringUtils.join(parts,
				TEMPLATE_NAME_SEPARATOR);

		if (withPrefix) {
			firstTemplateName = getPrefix() + TEMPLATE_NAME_SEPARATOR
					+ firstTemplateName;
		}

		return firstTemplateName;
	}
}
