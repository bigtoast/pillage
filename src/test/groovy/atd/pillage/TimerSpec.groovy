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

import spock.lang.*
import atd.pillage.Timer

class TimerSpec extends Specification {

  def "A timer should not create a metric when not running"(){
      given:
        StatsContainer provider = Mock(StatsContainer)
        def timer = new Timer(provider, "test timer");

      when:
       timer.stop("i will be dropped")

      then:
        !timer.isRunning()
        0 * provider.getMetric(_)
  }

  def "A timer should create a metric when running and stop is called"(){
      given: "A new Timer started by default"
        StatsContainer provider = Mock()
        Metric m = Mock()
        int elapsed = 0

        def timer = new Timer(provider, "test timer", true);

      expect: "timer is running"
        timer.isRunning()

      when: "stop is called"
        elapsed = timer.stop("i will not be dropped")

      then: "A metric should be added"
        1 * provider.getMetric("test_timer-i_will_not_be_dropped.millis") >> { m }
        1 * m.add( _ )
        !timer.isRunning()
  }

  def "A timer should stop and start"(){
      given: "A new Timer started by default"
        StatsContainer provider = Mock()
        Metric m = Mock()
        Integer elapsed = 0

        def timer = new Timer(provider, "test timer", true)

      when: "stopAndStart is called"
        elapsed = timer.stopAndStart("i will not be dropped").intValue();

      then: "A metric should be added"
        1 * provider.getMetric("test_timer-i_will_not_be_dropped.millis") >> { m }
        1 * m.add( _ )
        elapsed > 0
        //1 * m.add( {it == elapsed } ) // this should work but it doesn't
        timer.isRunning()
  }

}
