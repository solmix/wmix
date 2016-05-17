/*
 * Copyright 2015 The Solmix Project
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
package org.solmix.wmix.parser.support;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.solmix.commons.collections.DataTypeMap;
import org.solmix.commons.util.DataUtils;
import org.solmix.commons.util.ObjectUtils;
import org.solmix.wmix.parser.ValueParser;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月31日
 */

public  class AbstractValueParser implements ValueParser
{
    protected final DataTypeMap parameters    = new DataTypeMap();
   
    @Override
    public int size() {
        return parameters.size();
    }

    @Override
    public boolean isEmpty() {
        return parameters.isEmpty();
    }

    
    @Override
    public boolean containsKey(String key) {
        return parameters.containsKey(key);
    }

    @Override
    public Set<String> keySet() {
      return  new LinkedHashSet<String>(parameters.keySet());
    }

   
    @Override
    public String[] getKeys() {
        return parameters.keySet().toArray(new String[parameters.size()]);
    }

    @Override
    public boolean getBoolean(String key) {
        return parameters.getBoolean(key,false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.wmix.parser.ValueParser#getBoolean(java.lang.String, boolean)
     */
    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return parameters.getBoolean(key,defaultValue);
    }


    @Override
    public byte getByte(String key) {
        return parameters.getByte(key, (byte)0);
    }

    @Override
    public byte getByte(String key, byte defaultValue) {
        return parameters.getByte(key, defaultValue);
    }

    @Override
    public byte[] getBytes(String key) throws UnsupportedEncodingException {
        String stringValue = parameters.getString(key);
        return stringValue==null?ObjectUtils.EMPTY_BYTE_ARRAY:stringValue.getBytes();
    }

    @Override
    public char getChar(String key) {
        return parameters.getChar(key);
    }

    @Override
    public char getChar(String key, char defaultValue) {
        
        return parameters.getChar(key, defaultValue);
    }

    @Override
    public double getDouble(String key) {
        return parameters.getDouble(key);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return parameters.getDouble(key, defaultValue);
    }

    @Override
    public float getFloat(String key) {
        return parameters.getFloat(key);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return parameters.getFloat(key, defaultValue);
    }

    @Override
    public int getInt(String key) {
        return parameters.getInt(key);
    }

  
    @Override
    public int getInt(String key, int defaultValue) {
        return parameters.getInt(key, defaultValue);
    }

  
    @Override
    public long getLong(String key) {
        return parameters.getLong(key);
    }

   
    @Override
    public long getLong(String key, long defaultValue) {
        return parameters.getLong(key, defaultValue);
    }


    
    @Override
    public short getShort(String key) {
        return parameters.getShort(key);
    }

    @Override
    public short getShort(String key, short defaultValue) {
        return parameters.getShort(key, defaultValue);
    }

    
    @Override
    public String getString(String key) {
        return parameters.getString(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return parameters.getString(key, defaultValue);
    }
   

    @Override
    public Object get(String key) {
        return parameters.get(key);
    }

    
    @Override
    public Object getObject(String key) {
        return parameters.get(key);
    }

    @Override
    public Object getObject(String key, Object defaultValue) {
        Object o=getObject(key);
        if(o==null){
            return defaultValue;
        }else{
            return o;
        }
    }

  
    @Override
    public Date getDate(String key, DateFormat format) {
      return getDate(key, format);
    }

    @Override
    public Date getDate(String key, DateFormat format, Date defaultValue) {
        Object o = parameters.get(key);
        if(o==null){
            return defaultValue;
        }
        if (o instanceof Date) {
            return (Date) o;
        } else if (format != null && o != null) {
            try {
                format.parse(o.toString());
            } catch (ParseException e) {
            }
        }
        return null;
    }

    @Override
    public void setProperties(Object object) {
       if(object!=null){
          try {
            DataUtils.setProperties(parameters, object);
        } catch (Exception e) {
        }
       }
    }

    @Override
    public void add(String key, boolean value) {
        parameters.put(key, value);

    }

   
    @Override
    public void add(String key, byte value) {
        parameters.put(key, value);
    }

    
    @Override
    public void add(String key, char value) {
        parameters.put(key, value);
    }

   
    @Override
    public void add(String key, double value) {
        parameters.put(key, value);

    }

   
    @Override
    public void add(String key, float value) {
        parameters.put(key, value);
    }

    
    @Override
    public void add(String key, int value) {
        parameters.put(key, value);

    }

    
    @Override
    public void add(String key, long value) {
        parameters.put(key, value);

    }

   
    @Override
    public void add(String key, short value) {
        parameters.put(key, value);

    }

    
    @Override
    public void add(String key, Object value) {
        parameters.put(key, value);

    }


    
    @Override
    public Object remove(String key) {
        return parameters.remove(key);
    }

  
    @Override
    public void clear() {
        parameters.clear();
    }
}
