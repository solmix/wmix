/*
 * Copyright 2014 The Solmix Project
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

package org.solmix.wmix.condition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.regex.MatchResultSubstitution;
import org.solmix.commons.regex.Substitution;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.Extension;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.MessageUtils;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年6月13日
 */
@Extension(name = "path")
public class PathCondition implements org.solmix.wmix.condition.Condition
{

    private static final Logger LOG= LoggerFactory.getLogger(PathCondition.class);
    private Pattern[] patterns;

    private boolean[] negativePatterns;

    private String[] patternStrings;

    public void setRule(String rule) {
        if (rule != null) {
            this.patternStrings = StringUtils.split(rule, ", ");
            this.patterns = new Pattern[patternStrings.length];
            this.negativePatterns = new boolean[patternStrings.length];

            for (int i = 0; i < patternStrings.length; i++) {
                if (patternStrings[i].startsWith("!")) {
                    this.negativePatterns[i] = true;
                    patternStrings[i] = patternStrings[i].substring(1);
                }

                this.patterns[i] = Pattern.compile(patternStrings[i]);
            }
        }
    }

    @Override
    public boolean accept(Message message) {
        String path=MessageUtils.getString(message,Message.PATH_INFO);
        for (int i = 0; i < patterns.length; i++) {
            Matcher matcher = patterns[i].matcher(path);
            boolean matched = matcher.find();
            boolean negative = negativePatterns[i];

            if (negative != matched) {
                LOG.debug("URL path(servletPath/pathInfo) matched pattern: {}",patternStrings[i]);

                Substitution subst = new MatchResultSubstitution(negative ? MatchResultSubstitution.EMPTY_MATCH_RESULT : matcher);

                message.put(Substitution.class, subst);

                return true;
            }
        }

        return false;
    }

}
