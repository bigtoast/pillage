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
 
 class GangliaStatsReporterSpec extends Specification {
 	@Shared String addy
 	@Shared String name
 	@Shared GangliaStatsReporter reporter
 	@Shared def port = 8649
 
 	def setupSpec() { // why the fuck is this so slow?
 	 //addy = "239.2.11.71"
 	 addy = "10.56.10.123"
 	 name = InetAddress.getLocalHost().getHostName()
 	 reporter = new GangliaStatsReporter(addy, port, name )
 	}
 
 	def "Reporter should send a stat to ganglia"(){
 	  when:    
 		reporter.send("test.stat", "666", reporter.VALUE_INT, "millis", reporter.SLOPE_BOTH, 100, 100);
 		reporter.send("test.stat", "600", reporter.VALUE_INT, "millis", reporter.SLOPE_BOTH, 100, 100);
 		reporter.send("test.stat", "670", reporter.VALUE_INT, "millis", reporter.SLOPE_BOTH, 100, 100);
 		reporter.send("test.ip.stat", "654", reporter.VALUE_INT, "millis", reporter.SLOPE_BOTH, 100, 100);
      then:
      	1 == 1
 	}
 	
 	def "do it again"(){
 	 when:    
 		reporter.send("test.stat", "666", reporter.VALUE_INT, "millis", reporter.SLOPE_BOTH, 100, 100);
 		reporter.send("test.stat", "600", reporter.VALUE_INT, "millis", reporter.SLOPE_BOTH, 100, 100);
 		reporter.send("test.stat", "670", reporter.VALUE_INT, "millis", reporter.SLOPE_BOTH, 100, 100);
 		reporter.send("test.i2p.stat", "654", reporter.VALUE_INT, "millis", reporter.SLOPE_BOTH, 100, 100);
      then:
      	1 == 1
 	}
 }