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

import spock.lang.*

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class PeriodicCollectionSchedulerSpec extends Specification {
	
	def scheduler = PeriodicCollectionScheduler.getInstance();

	def "A periodic stats scheduler should trigger 2 snaps"(){
		given: 
			StatsCollector collector = Mock()
			CountDownLatch latch = new CountDownLatch(2);
			collector.collect() >> { latch.countDown() }
			
		when:
			scheduler.scheduleCollection(collector, 1, TimeUnit.SECONDS);
			
		then:
			latch.await()
			true
				
	}

    def "An exception thrown in a gauge should not kill the scheduler thread"(){
        given:
            StatsCollector collector = Mock()
            AtomicInteger i = new AtomicInteger(0)
            collector.collect() >> {
                if ( i.incrementAndGet() == 1 ) {
                    println("throwing yo")
                    throw new RuntimeException("Thread Death")
                }
            }

        when:
            scheduler.scheduleCollection(collector, 1, TimeUnit.SECONDS)
            Thread.sleep(3000)

        then:
            i.get() > 1

    }
	
}
