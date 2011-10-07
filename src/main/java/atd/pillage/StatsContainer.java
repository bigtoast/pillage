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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class acts as a container for stats
 * @author ATD
 */
public class StatsContainer implements StatsProvider {
  private MetricFactory metricFactory;
  public long start = System.currentTimeMillis();

  protected ConcurrentHashMap<String,Counter> counterMap = new ConcurrentHashMap<String, Counter>();
  protected ConcurrentHashMap<String,Metric> metricMap   = new ConcurrentHashMap<String, Metric>();
  protected ConcurrentHashMap<String,String> labelMap    = new ConcurrentHashMap<String, String>();
  
  public StatsContainer( MetricFactory mFactory ){
	  metricFactory = mFactory;
  }

    @Override
    public void addMetric(String name, int value) {
        getMetric(name).add(value);
    }

    @Override
    public void addMetric(String name, Distribution distribution) {
        getMetric(name).add(distribution);
    }

    @Override
    public void incr(String name, int count) {
        getCounter(name).incr(count);
    }

    @Override
    public void incr(String name) {
        getCounter(name).incr();
    }

    @Override
    public void setLabel(String name, String value) {
        labelMap.put(name,value);
    }

    @Override
    public void clearLabel(String name) {
        labelMap.remove(name);
    }

    @Override
    public Counter getCounter(String name) {
         Counter counter = counterMap.get(name);
        if( counter == null){
            counterMap.putIfAbsent(name, new Counter());
            counter = counterMap.get(name);
        }
        return counter;
    }

    @Override
    public Metric getMetric(String name) {
         Metric metric = metricMap.get(name);
        if( metric == null){
            metricMap.putIfAbsent(name, metricFactory.newMetric());
            metric = metricMap.get(name);
        }
        return metric;
    }

    @Override
    public String getLabel(String name) {
        return labelMap.get(name);
    }

    @Override
    public Timer getTimer(String name) {
        return new Timer(this, name);
    }

    @Override
    public Map<String, Long> getCounters() {
        HashMap<String,Long> map = new HashMap<String, Long>(counterMap.size());
        for(Map.Entry<String, Counter> entry: counterMap.entrySet()){
            map.put(entry.getKey(), entry.getValue().value());
        }
        return map;
    }

    @Override
    public Map<String, Distribution> getMetrics() {
        HashMap<String, Distribution> map = new HashMap<String, Distribution>(metricMap.size());
        for(Map.Entry<String,Metric> entry: metricMap.entrySet()){
            map.put(entry.getKey(),entry.getValue().getDistribution());
        }
        return map;
    }

    @Override
    public Map<String, String> getLabels() {
        return Collections.unmodifiableMap(labelMap);
    }

    @Override
    public void clearAll() {
        counterMap.clear();
        labelMap.clear();
        metricMap.clear();
        start = System.currentTimeMillis();
    }
    
    @Override
    public StatsSummary getSummary(){
    	return new StatsSummary(getCounters(), getMetrics(), getLabels(), start, System.currentTimeMillis());
    }

}
