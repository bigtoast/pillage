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
    private Map<String, Long> _counters;
    private Map<String, Distribution> _metrics;
    private Map<String, String> _labels;

    public StatsSummary(Map<String, Long> counters, Map<String, Distribution> metrics, Map<String, String> labels){
        _counters = counters;
        _metrics = metrics;
        _labels = labels;
    }

    public Map<String, Long> getCounters(){
        return Collections.unmodifiableMap(_counters);
    }

    public Map<String, Distribution> getMetrics(){
        return Collections.unmodifiableMap(_metrics);
    }

    public Map<String, String> getLabels(){
        return Collections.unmodifiableMap(_labels);
    }

    public StatsSummary filterOut(Pattern pattern){
        Map<String, Long> counters = new HashMap<String, Long>();
        Map<String, Distribution> metrics = new HashMap<String, Distribution>();
        Map<String, String> labels = new HashMap<String, String>();

        for(Map.Entry<String, Long> entry :_counters.entrySet()){
            if(!pattern.matcher(entry.getKey()).matches()){
                counters.put(entry.getKey(), entry.getValue());
            }
        }
        for(Map.Entry<String, Distribution> entry :_metrics.entrySet()){
            if(!pattern.matcher(entry.getKey()).matches()){
                metrics.put(entry.getKey(), entry.getValue());
            }
        }
        for(Map.Entry<String, String> entry :_labels.entrySet()){
            if(!pattern.matcher(entry.getKey()).matches()){
                labels.put(entry.getKey(), entry.getValue());
            }
        }
        return new StatsSummary(counters, metrics, labels);
    }
}
