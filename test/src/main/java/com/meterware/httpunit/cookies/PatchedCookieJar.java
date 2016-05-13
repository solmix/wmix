
package com.meterware.httpunit.cookies;

import static org.solmix.test.TestUtils.getAccessibleField;
import static org.solmix.test.TestUtils.getAccessibleMethod;

import java.net.URL;
import java.util.HashMap;

public class PatchedCookieJar extends CookieJar {
    public PatchedCookieJar() {
    }

    public PatchedCookieJar(CookieSource source) {
        try {
            getAccessibleField(getClass(), "_press").set(this, new PatchedCookiePress(source.getURL()));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        findCookies(source.getHeaderFields("Set-Cookie"), new PatchedRFC2109CookieRecipe());
        findCookies(source.getHeaderFields("Set-Cookie2"), new RFC2965CookieRecipe());
    }

    private void findCookies(String cookieHeader[], CookieRecipe recipe) {
        for (String element : cookieHeader) {
            recipe.findCookies(element);
        }
    }

    private class PatchedRFC2109CookieRecipe extends RFC2109CookieRecipe {
        @Override
        protected boolean isCookieReservedWord(String token) {
            return token.equalsIgnoreCase("httpOnly") || super.isCookieReservedWord(token);
        }
    }

    private class PatchedCookiePress extends CookiePress {
        public PatchedCookiePress(URL sourceURL) {
            super(sourceURL);
        }

        public void addTokenWithEqualsSign(CookieRecipe recipe, String token, int equalsIndex) {
            final String name = token.substring(0, equalsIndex).trim();
            final String value = token.substring(equalsIndex + 1).trim();

            StringBuffer _value = getFieldValue("_value", StringBuffer.class);
            _value.insert(0, value);

            HashMap _attributes = getFieldValue("_attributes", HashMap.class);

            if (recipe.isCookieAttribute(name.toLowerCase())) {
                _attributes.put(name.toLowerCase(), _value.toString());
            } else {
                try {
                    getAccessibleMethod(CookiePress.class, "addCookieIfValid", new Class[] { Cookie.class }).invoke(this, new Cookie(name, _value.toString(), _attributes));
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                _attributes.clear();
            }

            _value.setLength(0);
        }

        private <T> T getFieldValue(String name, Class<T> type) {
            try {
                return type.cast(getAccessibleField(getClass(), name).get(this));
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
