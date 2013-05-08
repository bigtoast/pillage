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

import com.ticketfly.pillage.org.json.JSONException;
import com.ticketfly.pillage.org.json.JSONObject;
import com.ticketfly.pillage.org.json.JSONStringer;

import java.util.Map;

/**
 * StatUtilities
 */
public class StatUtils {
	private static StatUtils ourInstance = new StatUtils();

	public static StatUtils getInstance() {
		return ourInstance;
	}

	private StatUtils() {
	}

	// helper function for computing deltas over counters
	public static long delta(long oldValue, long newValue) {
		if (oldValue <= newValue) {
			return newValue - oldValue;
		} else {
			return (Long.MAX_VALUE - oldValue) + (newValue - Long.MIN_VALUE)
					+ 1;
		}
	}

	// helper function for computing deltas over gauges
	public static double delta(double oldValue, double newValue) {
		if (oldValue <= newValue) {
			return newValue - oldValue;
		} else {
			return (Double.MAX_VALUE - oldValue)
					+ (newValue - Double.MIN_VALUE) + 1;
		}
	}

    public static String toJSON(StatsSummary stats) throws JSONException {
        JSONStringer json = new JSONStringer();

        json.object().key("counters").array();
        for( Map.Entry<String,Long> entry : stats.getCounters().entrySet() ) {
            json.object().key(entry.getKey()).value(entry.getValue()).endObject();
        }

        json.endArray().key("metrics").array();

        for ( Map.Entry<String,Distribution> entry : stats.getMetrics().entrySet() ) {
            json.object()
                .key(entry.getKey());

            Map<String,Number> metrics = entry.getValue().toMap();
            for ( Map.Entry<String,Number> metric : metrics.entrySet() ) {
                json.object().key(metric.getKey()).value(metric.getValue()).endObject();
            }

            json.endObject();
        }

        json.endArray().key("labels").array();

        for ( Map.Entry<String,String> entry : stats.getLabels().entrySet() ) {
            json.object().key(entry.getKey()).value(entry.getValue()).endObject();
        }

        json.endArray().key("gauges").array();

        for ( Map.Entry<String,Double> entry : stats.getGauges().entrySet()) {
            json.object().key(entry.getKey()).value(entry.getValue()).endObject();
        }

        return json.endArray().endObject().toString();
    }

}
