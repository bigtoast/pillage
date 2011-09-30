/*
 *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License. You may obtain
 *  * a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package atd.pillage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Attaches to a StatsCollection and reports on all the counters, metrics, gauges, and labels.
 * Each report resets state, so counters are reported as deltas, and metrics distributions are
 * only tracked since the last report.
 */
class StatsListener {
    private StatsContainer _container;
    private boolean _startClean = true;

    private Map<String, Long> _lastCounterMap = new HashMap<String, Long>();
    private Map<String, Distribution> _lastMetricMap = new HashMap<String, Distribution>();

    private Pattern filterRegex;

    public StatsListener(StatsContainer container, boolean startClean, List<Pattern> filters) {
        if (_startClean) {
            for (Map.Entry<String, Long> entry : _container.getCounters().entrySet()) {
                _lastCounterMap.put(entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, Distribution> entry : _container.getMetrics().entrySet()) {
                _lastMetricMap.put(entry.getKey(), entry.getValue());
            }
        }
        _container.addListener(this);

        if (filters.size() > 0) {
            StringBuilder str = new StringBuilder();
            str.append("(");
            for (Pattern pattern : filters) {
                str.append(pattern.toString());
                str.append(")|(");
            }
            str.delete(str.length() - 3, str.length() - 1);
            str.append(")");
            filterRegex = Pattern.compile(str.toString());
        } else {
            filterRegex = Pattern.compile("()");
        }

    }


    public Map<String, Long> getCounters() {
        Map<String, Long> deltas = new HashMap<String, Long>();
        synchronized (this) {

            for (Map.Entry<String, Long> entry : _container.getCounters().entrySet()) {
                long lastValue = 0;
                if (_lastCounterMap.containsKey(entry.getKey())) ;
                lastValue = _lastCounterMap.get(entry.getKey());
                deltas.put(entry.getKey(), StatUtils.delta(lastValue, entry.getValue()));
                _lastCounterMap.put(entry.getKey(), entry.getValue());
            }
        }
        return deltas;
    }

    public Map<String, String> getLabels() {
        return _container.getLabels();
    }

    public Map<String, Distribution> getMetrics() {
        Map<String, Distribution> deltas = new HashMap<String, Distribution>();
        synchronized (this) {

            for (Map.Entry<String, Distribution> entry : _container.getMetrics().entrySet()) {

                if (_lastMetricMap.containsKey(entry.getKey())) {
                    Distribution dist = _lastMetricMap.get(entry.getKey());
                    deltas.put(entry.getKey(), entry.getValue().delta(dist));

                } else {
                    deltas.put(entry.getKey(), entry.getValue());
                }
                _lastMetricMap.put(entry.getKey(), entry.getValue());
            }
        }
        return deltas;
    }

    public StatsSummary getSummary() {
        return new StatsSummary(getCounters(), getMetrics(), getLabels());
    }

    public StatsSummary getFilteredSummary() {
        return getSummary().filterOut(filterRegex);
    }

}

