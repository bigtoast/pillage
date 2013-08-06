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
            NewRelic.recordMetric(format(stat.getKey()), stat.getValue().floatValue());
        }

        for (Map.Entry<String,Distribution> dist :stats.getMetrics().entrySet() ){
            for( Map.Entry<String,Number> stat :dist.getValue().toMap().entrySet() ) {
                NewRelic.recordMetric(format(dist.getKey() + "." + stat.getKey()), stat.getValue().floatValue());
            }
        }

        for (Map.Entry<String,Double> stat :stats.getGauges().entrySet() ){
            NewRelic.recordMetric(format(stat.getKey()), stat.getValue().floatValue());
        }

    }

    /** convert a metric name into new relic's stupid non standard strict crappy
     * format. This converts all periods to / and converts words to CamelCase where
     * the second, third.. part of camel case are words when _ is dropped.. Also
     * "Custom" is prepended.. lamo
     *
     * "api-req.best-available.p95" is converted to
     * "Custom/ApiReq/BestAvailable/P95"
     *
     * @param metric name
     * @return formatted name
     */
    public String format(String name) {
        StringBuilder sb = new StringBuilder("Custom");
        for ( String sub : name.split("\\.") ){
            if ( sub.length() > 0 ) {
                sb.append("/");
                for ( String subSub : sub.split("-") ) {
                    if ( subSub.length() > 0 ) {
                        sb.append(Character.toUpperCase(subSub.charAt(0)));
                        sb.append(subSub.substring(1));
                    }
                }
            }
        }

        return sb.toString();
    }
}
