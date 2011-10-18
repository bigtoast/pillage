/*
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may
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
 *
 */

package atd.pillage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A small object to encapsulate a summary of stats. A summary is an immutable 
 * object.
 */
public class StatsSummary {
	private long start;
	private long end;
	
	/**
	 * Timespan in milliseconds included in this summary
	 * @return
	 */
	public long span(){ return end - start; }
	
    private Map<String, Long> counters;
    private Map<String, Distribution> metrics;
    private Map<String, String> labels;
    private Map<String, Double> gauges;

    public StatsSummary(Map<String, Long> counters, Map<String, Distribution> metrics, Map<String, String> labels){
    	this(counters, metrics, labels, new HashMap<String, Double>());
    }
    
    public StatsSummary(Map<String, Long> counters, Map<String, Distribution> metrics, Map<String, String> labels, Map<String,Double> gauges){
        this(counters, metrics, labels, gauges, System.currentTimeMillis(), System.currentTimeMillis() );
    }
    
    public StatsSummary(Map<String, Long> counters, Map<String, Distribution> metrics, Map<String, String> labels, long start, long end){
    	this(counters, metrics, labels, new HashMap<String, Double>(), start, end);
    }
        
    public StatsSummary(Map<String, Long> counters, Map<String, Distribution> metrics, Map<String, String> labels, Map<String,Double> gauges, long start, long end){
    	this.counters = counters;
    	this.metrics = metrics;
    	this.labels = labels;
    	this.gauges = gauges;
    	this.start = start;
    	this.end = end;
    }

    /**
     * return an unmodifiable map of counter names and their values
     * @return
     */
    public Map<String, Long> getCounters(){
        return Collections.unmodifiableMap(counters);
    }

    /**
     * return an unmodifiable map of metric names and their distributions
     * 
     * @return
     */
    public Map<String, Distribution> getMetrics(){
        return Collections.unmodifiableMap(metrics);
    }

    /**
     * return an unmodifiable map of label names and their string values.
     * 
     * @return
     */
    public Map<String, String> getLabels(){
        return Collections.unmodifiableMap(labels);
    }
    
    /**
     * return an unmodifiable map of gauges.
     * 
     * @return
     */
    public Map<String, Double> getGauges(){
    	if(gauges == null || gauges.isEmpty())
    		return Collections.emptyMap();
    				
    	return Collections.unmodifiableMap(gauges);
    }
    
    /**
     * get the start time in milliseconds for this summary.
     * @return
     */
    public long getStart(){
    	return start;
    }
    
    /**
     * get the end time in milliseconds for this summary.
     * @return
     */
    public long getEnd(){
    	return end;
    }

    /**
     * Create a new StatsSummary object by filtering out unwanted stats based on the Pattern
     * passed in.
     * 
     * @param pattern
     * @return
     */
    public StatsSummary filterOut(Pattern pattern){
        Map<String, Long> counters = new HashMap<String, Long>();
        Map<String, Distribution> metrics = new HashMap<String, Distribution>();
        Map<String, String> labels = new HashMap<String, String>();

        for(Map.Entry<String, Long> entry :this.counters.entrySet()){
            if(!pattern.matcher(entry.getKey()).matches()){
                counters.put(entry.getKey(), entry.getValue());
            }
        }
        for(Map.Entry<String, Distribution> entry :this.metrics.entrySet()){
            if(!pattern.matcher(entry.getKey()).matches()){
                metrics.put(entry.getKey(), entry.getValue());
            }
        }
        for(Map.Entry<String, String> entry :this.labels.entrySet()){
            if(!pattern.matcher(entry.getKey()).matches()){
                labels.put(entry.getKey(), entry.getValue());
            }
        }
        return new StatsSummary(counters, metrics, labels);
    }
}
