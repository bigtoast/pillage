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
 * small object to encapsulate a summary of stats.
 */
public class StatsSummary {
	private long start;
	private long end;
	
	public long span(){ return end - start; }
	
    private Map<String, Long> counters;
    private Map<String, Distribution> metrics;
    private Map<String, String> labels;

    public StatsSummary(Map<String, Long> counters, Map<String, Distribution> metrics, Map<String, String> labels){
        this(counters, metrics, labels,System.currentTimeMillis(), System.currentTimeMillis() );
    }
    
    public StatsSummary(Map<String, Long> counters, Map<String, Distribution> metrics, Map<String, String> labels, long start, long end){
    	this.counters = counters;
    	this.metrics = metrics;
    	this.labels = labels;
    	this.start = start;
    	this.end = end;
    }

    public Map<String, Long> getCounters(){
        return Collections.unmodifiableMap(counters);
    }

    public Map<String, Distribution> getMetrics(){
        return Collections.unmodifiableMap(metrics);
    }

    public Map<String, String> getLabels(){
        return Collections.unmodifiableMap(labels);
    }

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
