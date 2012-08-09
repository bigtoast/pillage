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

}
