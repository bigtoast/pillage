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

class StatsAccessorImplSpecs extends Specification {

	def "getSummary should call provider.getSummary"(){
		given:
			StatsProvider provider = Mock()
			provider.getCounters() >> { [:] }
			provider.getMetrics() >> { [:] }
			def accessor = new StatsAccessorImpl(provider)
		when:
			accessor.getFullSummary()
		then:
			1 * provider.getSummary()

	}
	
	def "triggerSnap should triggerCounterSnap and triggerMetricSnap"(){
		given:
			StatsProvider provider = Mock()
			provider.getCounters() >> { [:] }
			provider.getMetrics() >> { [:] }
			def accessor = new StatsAccessorImpl(provider)
		when:
			accessor.triggerSnap()
		then:
			1 * provider.getCounters() >> { [:] }
			1 * provider.getMetrics() >> { [:] }
	}
}
