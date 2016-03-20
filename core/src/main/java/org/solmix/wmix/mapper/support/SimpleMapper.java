
package org.solmix.wmix.mapper.support;

import java.util.TreeMap;

import org.solmix.wmix.mapper.Mapper;

public class SimpleMapper implements Mapper
{
    public static final String TYPE="simple";
    private String type = TYPE;

    public SimpleMapper()
    {
        initMapper();
    }

    private final TreeMap<String, String> map = new TreeMap<String, String>();

    @Override
    public String map(String name) {
        return map.get(name);
    }

    protected void initMapper() {
        
    }

    protected void addMap(String key, String value) {
        map.put(key, value);
    }

    @Override
    public String getType() {
        return type;
    }

    
    public void setType(String type) {
        this.type = type;
    }
    

}
