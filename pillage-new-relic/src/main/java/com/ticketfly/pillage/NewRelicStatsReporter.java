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

package com.ticketfly.pillage;

import com.newrelic.api.agent.NewRelic;
import java.util.Map;


/** if you are running new relic, this will push stats there. It reports counters, metrics and gauges but not labels. */
public class NewRelicStatsReporter implements StatsReporter {

    public void report( StatsSummary stats ) {
        for (Map.Entry<String,Long> stat : stats.getCounters().entrySet() ) {
            NewRelic.recordMetric(stat.getKey(), stat.getValue().floatValue());
        }

        for (Map.Entry<String,Distribution> dist :stats.getMetrics().entrySet() ){
            for( Map.Entry<String,Number> stat :dist.getValue().toMap().entrySet() ) {
                NewRelic.recordMetric(stat.getKey(), stat.getValue().floatValue());
            }
        }

        for (Map.Entry<String,Double> stat :stats.getGauges().entrySet() ){
            NewRelic.recordMetric(stat.getKey(), stat.getValue().floatValue());
        }

    }
}
