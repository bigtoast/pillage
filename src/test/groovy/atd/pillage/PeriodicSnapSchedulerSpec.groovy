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

package atd.pillage

import spock.lang.*

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit

class PeriodicSnapSchedulerSpec extends Specification {
	
	def scheduler = PeriodicSnapScheduler.getInstance();

	def "A periodic stats collector should trigger 2 snaps"(){
		given: 
			StatsAccessor accessor = Mock()
			CountDownLatch latch = new CountDownLatch(2);
			accessor.triggerSnap() >> { latch.countDown() }
			
			
		when:
			scheduler.scheduleSnap(accessor, 1, TimeUnit.SECONDS);
			
		then:
			latch.await()
			true
				
	}
	
}
