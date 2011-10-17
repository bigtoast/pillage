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

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


/**
 * Attaches to a StatsContainer and reports on all the counters, metrics, gauges, and labels.
 * Each report resets state, so counters are reported as deltas, and metrics distributions are
 * only tracked since the last report.
 */
class StatsCollectorImpl implements StatsCollector {
    private StatsContainer container;
    private List<StatsReporter> reporters = new ArrayList<StatsReporter>();
    private long lastSnap;
    private long currentSnap;

    private Map<String, Long> lastCounterMap = new HashMap<String, Long>();
    private Map<String, Long> deltaCounterMap = new HashMap<String, Long>();
    private Map<String, Distribution> lastMetricMap = new HashMap<String, Distribution>();
    private Map<String, Distribution> deltaMetricMap = new HashMap<String, Distribution>();
    
    public StatsCollectorImpl(StatsContainer container){
    	this(container, true);
    }
    
    public StatsCollectorImpl(StatsContainer container, boolean startClean) {
    	this.container = container;
        if (startClean) {
            for (Map.Entry<String, Long> entry : this.container.getCounters().entrySet()) {
                lastCounterMap.put(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, Distribution> entry : this.container.getMetrics().entrySet()) {
                lastMetricMap.put(entry.getKey(), entry.getValue());
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
	public StatsSummary getFullSummary() {
		return container.getSummary();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public StatsSummary getDeltaSummary() {
		return new StatsSummary(deltaCounterMap, deltaMetricMap, container.getLabels(), lastSnap, currentSnap);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StatsSummary collect() {
		triggerCounterSnap();
		triggerMetricSnap();
		lastSnap = currentSnap;
		currentSnap = System.currentTimeMillis();
		StatsSummary deltas = getDeltaSummary();
		for( StatsReporter reporter : reporters){
			reporter.report(deltas);
		}
		return deltas;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addReporter(StatsReporter reporter) {
		synchronized(this) {
			reporters.add(reporter);
		}		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeReporter(StatsReporter reporter) {
		synchronized(this){
			reporters.remove(reporter);
		}
	}


	/**
	 * Trigger a collection of the counters and overwrite the last collection.
	 */
    protected void triggerCounterSnap() {
        Map<String, Long> deltas = new HashMap<String, Long>();
        synchronized (this) {

            for (Map.Entry<String, Long> entry : container.getCounters().entrySet()) {
                long lastValue = 0;
                if (lastCounterMap.containsKey(entry.getKey())) ;
                lastValue = lastCounterMap.get(entry.getKey());
                deltas.put(entry.getKey(), StatUtils.delta(lastValue, entry.getValue()));
                lastCounterMap.put(entry.getKey(), entry.getValue());
            }
        }
        deltaCounterMap = deltas;
    }

    /**
     * Trigger a collection of the metrics and overwrite the last collection.
     */
    public void triggerMetricSnap() {
        Map<String, Distribution> deltas = new HashMap<String, Distribution>();
        synchronized (this) {

            for (Map.Entry<String, Distribution> entry : container.getMetrics().entrySet()) {

                if (lastMetricMap.containsKey(entry.getKey())) {
                    Distribution dist = lastMetricMap.get(entry.getKey());
                    deltas.put(entry.getKey(), entry.getValue().delta(dist));

                } else {
                    deltas.put(entry.getKey(), entry.getValue());
                }
                lastMetricMap.put(entry.getKey(), entry.getValue());
            }
        }
        deltaMetricMap = deltas;
    }
    

}

