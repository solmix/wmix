/**
 * Copyright (c) 2015 The Solmix Project
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

package org.solmix.wmix.web.util;

import static org.solmix.commons.util.StringUtils.EMPTY_STRING_ARRAY;
import static org.solmix.commons.util.StringUtils.split;
import static org.solmix.commons.util.StringUtils.trimToNull;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.solmix.commons.regex.PathNameWildcardCompiler;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年1月30日
 */

public class RequestURIFilter {

    public static final String EXCLUDE_PREFIX = "!";

    private final String[] uris;

    private final boolean[] excludes;

    private final Pattern[] patterns;

    public RequestURIFilter(String uris) {
        List<String> names = new LinkedList<String>();
        List<Boolean> excludes = new LinkedList<Boolean>();
        List<Pattern> patterns = new LinkedList<Pattern>();

        for (String uri : split(uris==null?"":uris, ", \r\n")) {
            uri = trimToNull(uri);

            if (uri != null) {
                String fullUri = uri;
                boolean exclude = uri.startsWith(EXCLUDE_PREFIX);

                if (exclude) {
                    uri = trimToNull(uri.substring(EXCLUDE_PREFIX.length()));
                }

                if (uri != null) {
                    names.add(fullUri);
                    excludes.add(exclude);
                    patterns.add(PathNameWildcardCompiler.compilePathName(uri));
                }
            }
        }

        if (!patterns.isEmpty()) {
            this.uris = names.toArray(new String[names.size()]);
            this.patterns = patterns.toArray(new Pattern[patterns.size()]);
            this.excludes = new boolean[excludes.size()];

            for (int i = 0; i < excludes.size(); i++) {
                this.excludes[i] = excludes.get(i);
            }
        } else {
            this.uris = EMPTY_STRING_ARRAY;
            this.excludes = new boolean[0];
            this.patterns = null;
        }
    }

    public boolean matches(String path) {
        if (patterns != null) {
            for (int i = patterns.length - 1; i >= 0; i--) {
                if (patterns[i].matcher(path).find()) {
                    return !excludes[i];
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("FilterOf").append(uris).toString();
    }
}
