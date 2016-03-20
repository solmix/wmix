
package org.solmix.wmix.mapper;

public interface MapperService
{

    String map(String ruleType, String name) throws MapperTypeNotFoundException, MapperException;
}
