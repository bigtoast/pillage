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
 * 
 * @author ATD
 */
public class StatsContainerImpl implements StatsContainer {
	private MetricFactory metricFactory;
	public long start = System.currentTimeMillis();

	protected ConcurrentHashMap<String, Counter> counterMap = new ConcurrentHashMap<String, Counter>();
	protected ConcurrentHashMap<String, Metric> metricMap = new ConcurrentHashMap<String, Metric>();
	protected ConcurrentHashMap<String, String> labelMap = new ConcurrentHashMap<String, String>();
	protected ConcurrentHashMap<String, Gauge> gaugeMap = new ConcurrentHashMap<String, Gauge>();

	public StatsContainerImpl(MetricFactory mFactory) {
		metricFactory = mFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(String name, int value) {
		getMetric(name).add(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(String name, Distribution distribution) {
		getMetric(name).add(distribution);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void incr(String name, int count) {
		getCounter(name).incr(count);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void incr(String name) {
		getCounter(name).incr();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(String name, String value) {
		labelMap.put(name, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearMetric(String name) {
		Metric metric = metricMap.get(name);
		if(metric != null)
			metric.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearCounter(String name) {
		Counter cntr = counterMap.get(name);
		if( cntr != null)
			cntr.reset();		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Double> gauges() {
		HashMap<String, Double> map = new HashMap<String, Double>();
		for(Map.Entry<String, Gauge> entry :gaugeMap.entrySet()){
			map.put(entry.getKey(), entry.getValue().read());
		}
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerGauge(String name, Gauge gauge) {
		gaugeMap.putIfAbsent(name, gauge);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deregisterGauge(String name) {
		gaugeMap.remove(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearLabel(String name) {
		labelMap.remove(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Counter getCounter(String name) {
		Counter counter = counterMap.get(name);
		if (counter == null) {
			counterMap.putIfAbsent(name, new Counter());
			counter = counterMap.get(name);
		}
		return counter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Metric getMetric(String name) {
		Metric metric = metricMap.get(name);
		if (metric == null) {
			metricMap.putIfAbsent(name, metricFactory.newMetric());
			metric = metricMap.get(name);
		}
		return metric;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel(String name) {
		return labelMap.get(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timer getTimer(String name) {
		return new Timer(this, name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Long> counters() {
		HashMap<String, Long> map = new HashMap<String, Long>(counterMap.size());
		for (Map.Entry<String, Counter> entry : counterMap.entrySet()) {
			map.put(entry.getKey(), entry.getValue().value());
		}
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Distribution> metrics() {
		HashMap<String, Distribution> map = new HashMap<String, Distribution>(
				metricMap.size());
		for (Map.Entry<String, Metric> entry : metricMap.entrySet()) {
			map.put(entry.getKey(), entry.getValue().getDistribution());
		}
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> labels() {
		return Collections.unmodifiableMap(labelMap);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearAll() {
		counterMap.clear();
		labelMap.clear();
		metricMap.clear();
		start = System.currentTimeMillis();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StatsSummary getSummary() {
		return new StatsSummary(counters(), metrics(), labels(), start,
				System.currentTimeMillis());
	}

}
