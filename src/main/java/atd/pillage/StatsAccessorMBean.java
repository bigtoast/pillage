/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  not use this file except in compliance with the License. You may obtain
 *  a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package atd.pillage;

import java.util.Map;

import javax.management.*;

public class StatsAccessorMBean implements DynamicMBean {

    private StatsAccessor accessor;
    
    public StatsAccessorMBean( StatsAccessor accessor ){
    	this.accessor = accessor;
    }

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
     StatsSummary stats = accessor.getDeltaSummary();
     
     if(stats.getCounters().containsKey(attribute)){
    	 return stats.getCounters().get(attribute);
     }
     
     if(stats.getLabels().containsKey(attribute)){
    	 return stats.getLabels().get(attribute);
     }
     
     if(stats.getMetrics().containsKey(attribute)){
    	 return stats.getMetrics().get(attribute).toMap();
     }
     
     throw new AttributeNotFoundException(attribute + " not found in counters, labels or metrics.");
    	
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        // not supported
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
    	StatsSummary stats = accessor.getDeltaSummary();
        AttributeList list = new AttributeList();
        for(String attribute :attributes){
        	if( stats.getCounters().containsKey(attribute)){
        		list.add(new Attribute(attribute, stats.getCounters().get(attribute)));
        	} else if( stats.getLabels().containsKey(attribute)){
        		list.add(new Attribute(attribute, stats.getLabels().get(attribute)));
        	} else if( stats.getMetrics().containsKey(attribute)){
        		list.add(new Attribute(attribute, stats.getMetrics().get(attribute).toMap()));
        	}
        }
        
        return list;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        // not supported
    	return null;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        // not supported
    	return null;  
    }

    @Override
    public MBeanInfo getMBeanInfo() {
    	StatsSummary stats = accessor.getDeltaSummary();
    	MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[stats.getCounters().size() + 
    	                                                    stats.getLabels().size()   +
    	                                                    stats.getMetrics().size() 
    	                                                    ];
    	int i = 0;
    	for (Map.Entry<String, Long> entry : stats.getCounters().entrySet()  ){
    		attrs[i++] = new MBeanAttributeInfo(entry.getKey(),"java.lang.Long", "Counter: " + entry.getKey(), false, false, false );
    	}
    	
    	for (Map.Entry<String, String> entry : stats.getLabels().entrySet()  ){
    		attrs[i++] = new MBeanAttributeInfo(entry.getKey(),"java.lang.String", "Label: " + entry.getKey(), false, true, false );
    	}
    	
    	for (Map.Entry<String, Distribution> entry : stats.getMetrics().entrySet()  ){
    		attrs[i++] = new MBeanAttributeInfo(entry.getKey(),"java.util.Map<String,Number>", "Metric Distribution: " + entry.getKey(), false, false, false );
    	}
        return new MBeanInfo(accessor.getClass().getCanonicalName(),
        							  "A stats listener mBean",
        							  attrs,
        							  null,
        							  new MBeanOperationInfo[0],
        							  null);
    }
}
