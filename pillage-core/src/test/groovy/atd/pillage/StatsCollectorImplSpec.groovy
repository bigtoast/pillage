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

package atd.pillage

import spock.lang.*;

class StatsCollectorImplSpec extends Specification {

	def "getSummary should call container.getSummary"(){
		given:
			StatsContainer container = Mock()
			container.counters() >> { [:] }
			container.metrics() >> { [:] }
			container.gauges() >> { [:] }
			def collector = new StatsCollectorImpl(container)
		when:
			collector.getFullSummary()
		then:
			1 * container.getSummary()

	}
	
	def "collect should triggerCounterSnap and triggerMetricSnap"(){
		given:
			StatsContainer container = Mock()
			container.counters() >> { [:] }
			container.metrics() >> { [:] }
			container.gauges() >> { [:] }
			def collector = new StatsCollectorImpl(container)
		when:
			collector.collect()
		then:
			1 * container.counters() >> { [:] }
			1 * container.metrics() >> { [:] }
	}
	
	def "getJvmStats should return jvm stats when includeJvmStats not activated but method should return false"(){
		given:
			StatsContainer container = Mock()
			container.counters() >> { [:] }
			container.metrics() >> { [:] }
			container.gauges() >> { [:] }
			def collector = new StatsCollectorImpl(container)
		expect:
			collector.areJvmStatsIncluded() == false
			collector.getJvmStats().size() > 0			
	}
	
	def "collect should not include jvm stats by default"(){
		given:
			StatsContainer container = Mock()
			container.counters() >> { [:] }
			container.metrics() >> { [:] }
			container.gauges() >> { [:] }
			container.getSummary() >> { new StatsSummary([:], [:], [:]) }
			def collector = new StatsCollectorImpl(container)
		when:
			collector.collect()
		then:
			collector.areJvmStatsIncluded() == false
			collector.getDeltaSummary().getGauges().size() == 0
			collector.getFullSummary().getGauges().size() == 0
	}
	
	def "collect should include jvm stats when turned on"(){
		given:
			StatsContainer container = Mock()
			container.counters() >> { [:] }
			container.metrics() >> { [:] }
			container.gauges() >> { [:] }
			container.getSummary() >> { new StatsSummary([:], [:], [:]) }
			def collector = new StatsCollectorImpl(container)
			collector.includeJvmStats(true);
		when:
			collector.collect()
		then:
			collector.areJvmStatsIncluded() == true
			collector.getDeltaSummary().getGauges().size() > 0
			collector.getFullSummary().getGauges().size() > 0
	}
	
	def "getJvmStats should return jvm stats with no null keys"(){
		given:
			StatsContainer container = Mock()
			container.counters() >> { [:] }
			container.metrics() >> { [:] }
			container.gauges() >> { [:] }
			container.getSummary() >> { new StatsSummary([:], [:], [:]) }
			def collector = new StatsCollectorImpl(container)
		when:
			def stats = collector.getJvmStats()
		then:
			stats.get(StatsCollectorImpl.HEAP_USED) > 0
			stats.keySet().findAll{ it == null}.size() == 0
	}
}
