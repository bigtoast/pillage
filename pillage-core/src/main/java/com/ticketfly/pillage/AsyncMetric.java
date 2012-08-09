/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ticketfly.pillage;

public class AsyncMetric implements Metric {

	private AsyncStatsContainer container;
	private String name;
	
	public AsyncMetric( String name, AsyncStatsContainer container ){
		this.name = name;
		this.container = container;	
	}
	
	@Override
	public void clear() {
		container.clearMetric(name);
	}

	@Override
	public long add(int i) {
		container.add(name, i);
		return -1L;
	}

	@Override
	public long add(Distribution d) {
		container.add(name, d);
		return -1L;
	}

	@Override
	public Distribution getDistribution() {
		return null;
	}

}
