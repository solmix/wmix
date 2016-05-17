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
package org.solmix.wmix.parser;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Set;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月31日
 */

public interface ValueParser
{
    /**
     * 取得值的数量。
     *
     * @return 值的数量
     */
    int size();

    /**
     * 判断是否无值。
     *
     * @return 如果无值，则返回<code>true</code>
     */
    boolean isEmpty();

    /**
     * 检查是否包含指定名称的参数。
     *
     * @param key 要查找的参数名
     * @return 如果存在，则返回<code>true</code>
     */
    boolean containsKey(String key);

    /*
     * 取得所有参数名的集合。
     * @return 所有参数名的集合
     */
    Set<String> keySet();

    /*
     * 取得所有参数名的数组。
     * @return 所有参数名的数组
     */
    String[] getKeys();

    // =============================================================
    //  取得参数的值
    // =============================================================

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回<code>false</code>。
     *
     * @param key 参数名
     * @return 参数值
     */
    boolean getBoolean(String key);

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回指定默认值。
     *
     * @param key          参数名
     * @param defaultValue 默认值
     * @return 参数值
     */
    boolean getBoolean(String key, boolean defaultValue);

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回<code>0</code>。
     *
     * @param key 参数名
     * @return 参数值
     */
    byte getByte(String key);

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回指定默认值。
     *
     * @param key          参数名
     * @param defaultValue 默认值
     * @return 参数值
     */
    byte getByte(String key, byte defaultValue);

    /**
     * 取得指定参数的字节。这个字节是根据<code>getCharacterEncoding()</code>所返回的字符集进行编码的。
     *
     * @param key 参数名
     * @return 参数值的字节数组
     * @throws UnsupportedEncodingException 如果指定了错误的编码字符集
     */
    byte[] getBytes(String key) throws UnsupportedEncodingException;

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回<code>'\0'</code>。
     *
     * @param key 参数名
     * @return 参数值
     */
    char getChar(String key);

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回指定默认值。
     *
     * @param key          参数名
     * @param defaultValue 默认值
     * @return 参数值
     */
    char getChar(String key, char defaultValue);

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回<code>0</code>。
     *
     * @param key 参数名
     * @return 参数值
     */
    double getDouble(String key);

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回指定默认值。
     *
     * @param key          参数名
     * @param defaultValue 默认值
     * @return 参数值
     */
    double getDouble(String key, double defaultValue);

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回<code>0</code>。
     *
     * @param key 参数名
     * @return 参数值
     */
    float getFloat(String key);

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回指定默认值。
     *
     * @param key          参数名
     * @param defaultValue 默认值
     * @return 参数值
     */
    float getFloat(String key, float defaultValue);

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回<code>0</code>。
     *
     * @param key 参数名
     * @return 参数值
     */
    int getInt(String key);

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回指定默认值。
     *
     * @param key          参数名
     * @param defaultValue 默认值
     * @return 参数值
     */
    int getInt(String key, int defaultValue);

  
    /**
     * 取得参数值，如果指定名称的参数不存在，则返回<code>0</code>。
     *
     * @param key 参数名
     * @return 参数值
     */
    long getLong(String key);

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回指定默认值。
     *
     * @param key          参数名
     * @param defaultValue 默认值
     * @return 参数值
     */
    long getLong(String key, long defaultValue);


  
    /**
     * 取得参数值，如果指定名称的参数不存在，则返回<code>0</code>。
     *
     * @param key 参数名
     * @return 参数值
     */
    short getShort(String key);

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回指定默认值。
     *
     * @param key          参数名
     * @param defaultValue 默认值
     * @return 参数值
     */
    short getShort(String key, short defaultValue);

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回<code>null</code>。
     *
     * @param key 参数名
     * @return 参数值
     */
    String getString(String key);

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回指定默认值。
     *
     * @param key          参数名
     * @param defaultValue 默认值
     * @return 参数值
     */
    String getString(String key, String defaultValue);

  

    /**
     * 取得参数值，如果指定名称的参数不存在，则返回<code>null</code>。 此方法和<code>getString</code>
     * 一样，但在模板中便易于使用。
     *
     * @param key 参数名
     * @return 参数值
     */
    Object get(String key);

    /**
     * 取得指定参数的值。如果参数不存在，则返回<code>null</code>。
     *
     * @param key 参数名
     * @return 参数值
     */
    Object getObject(String key);

    /**
     * 取得指定参数的值。如果参数不存在，则返回默认值。
     *
     * @param key          参数名
     * @param defaultValue 默认值
     * @return 参数值
     */
    Object getObject(String key, Object defaultValue);

    /**
     * 取得日期。字符串将使用指定的<code>DateFormat</code>来解析。如果不存在，则返回<code>null</code>。
     *
     * @param key    参数名
     * @param format <code>DateFormat</code>对象
     * @return <code>java.util.Date</code>对象
     */
    Date getDate(String key, DateFormat format);

    /**
     * 取得日期。字符串将使用指定的<code>DateFormat</code>来解析。如果不存在，则返回默认值。
     *
     * @param key          参数名
     * @param format       <code>DateFormat</code>对象
     * @param defaultValue 默认值
     * @return <code>java.util.Date</code>对象
     */
    Date getDate(String key, DateFormat format, Date defaultValue);


    /** 将数据保存到object properties中。 */
    void setProperties(Object object);

    // =============================================================
    //  添加和修改参数的方法
    // =============================================================

    /**
     * 添加参数名/参数值。
     *
     * @param key   参数名
     * @param value 参数值
     */
    void add(String key, boolean value);

    /**
     * 添加参数名/参数值。
     *
     * @param key   参数名
     * @param value 参数值
     */
    void add(String key, byte value);

    /**
     * 添加参数名/参数值。
     *
     * @param key   参数名
     * @param value 参数值
     */
    void add(String key, char value);

    /**
     * 添加参数名/参数值。
     *
     * @param key   参数名
     * @param value 参数值
     */
    void add(String key, double value);

    /**
     * 添加参数名/参数值。
     *
     * @param key   参数名
     * @param value 参数值
     */
    void add(String key, float value);

    /**
     * 添加参数名/参数值。
     *
     * @param key   参数名
     * @param value 参数值
     */
    void add(String key, int value);

    /**
     * 添加参数名/参数值。
     *
     * @param key   参数名
     * @param value 参数值
     */
    void add(String key, long value);

    /**
     * 添加参数名/参数值。
     *
     * @param key   参数名
     * @param value 参数值
     */
    void add(String key, short value);

    /**
     * 添加参数名/参数值。
     *
     * @param key   参数名
     * @param value 参数值
     */
    void add(String key, Object value);

    

    // =============================================================
    //  清除参数的方法
    // =============================================================

    /**
     * 删除指定名称的参数。
     *
     * @return 原先和指定名称对应的参数值，可能是<code>String[]</code>或<code>null</code>
     */
    Object remove(String key);

    /** 清除所有值。 */
    void clear();
}
