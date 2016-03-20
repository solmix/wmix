package org.solmix.wmix.mapper.support;

import java.util.HashMap;
import java.util.Map;

import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerAware;
import org.solmix.wmix.mapper.Mapper;
import org.solmix.wmix.mapper.MapperException;
import org.solmix.wmix.mapper.MapperService;
import org.solmix.wmix.mapper.MapperTypeNotFoundException;


public class DefaultMapperService implements MapperService,ContainerAware
{
    private Map<String, Mapper> rules;
    private MapperService parent;
    private Container container;
    
    public MapperService getParent() {
        return parent;
    }

    public void setParent(MapperService parent) {
        this.parent = parent;
    }
    
    @Override
    public void setContainer(Container container) {
        this.container = container;
        this.container.setExtension(this, MapperService.class);
    }
    
    @Override
    public String map(String ruleType, String name) throws MapperTypeNotFoundException, MapperException {
        Mapper rule = rules.get(ruleType);

        if (rule == null) {
            if (parent == null) {
                throw new MapperTypeNotFoundException("Failed to get mapping rule of \"" + ruleType + "\"");
            } else {
                return parent.map(ruleType, name);
            }
        }

        return rule.map(name);
    }

    
    public void setRules(Map<String, Mapper> rules) {
        this.rules = rules;
    }
    
    public void setRule(Mapper mapper){
        if(mapper!=null){
            this.rules=new HashMap<String, Mapper>();
            this.rules.put(mapper.getType(), mapper);
            
        }
       
    }
    
 

}
