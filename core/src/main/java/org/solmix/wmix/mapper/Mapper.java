package org.solmix.wmix.mapper;


public interface Mapper
{
    /** 将指定名称映射成指定类型的名称。如果映射不成功，则返回<code>null</code>。 */
    String map(String name);
    
    String getType();
}
