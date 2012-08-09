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

import spock.lang.*;

class GMetricStatsReporterSpec extends Specification {

	@Shared GMetricStatsReporter reporter
	@Shared StatsContainer stats
	@Shared StatsCollector collector
	
	def setupSpec() {
		stats = new StatsContainerImpl( new HistogramMetricFactory() ) 
		collector = new StatsCollectorImpl( stats )
		collector.includeJvmStats( true )
		reporter = new GMetricStatsReporter("/bin/sh")
	}
	
	def "reporter should report stuff"(){
		when:
		  stats.incr "cnt1", 50
		  def timer = stats.getTimer("timer")
		  timer.start() 
		  Thread.sleep(100) 
		  timer.stop()
		  
		  stats.set "lab1", "val1"
		  stats.registerGauge("random", new Gauge() {  
			  @Override double read() { 
				  Math.random() 
			  } 
	      })
		  
		  reporter.report( collector.getDeltaSummary() )
		  
		  stats.incr "cnt1", 50
		  timer.start()
		  Thread.sleep(100)
		  timer.stop()
		  
		  reporter.report( collector.getDeltaSummary() )
		  
		  reporter.report( collector.getFullSummary() )
		  
		then: 
		  reporter.canReport() == true
	}
	
}
