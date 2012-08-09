
package com.ticketfly.pillage;

import spock.lang.*

class GroovyMetaMagicSpec extends Specification {
	/*
	def "The time method should be added to the StatsContainer interface"(){
		given:
		ExpandoMetaClass.enableGlobally()
			def container = new StatsContainerImpl(new HistogramMetricFactory())
		
		when:
			container.time("test_timer"){
				Thread.sleep(1000);
			}
			
		then:
			def sum = container.getSummary()
			sum.getMetrics().size() == 1
			def metric = sum.getMetrics().get("test_timer.millis")
			metric.getCount() == 1
			metric.getSum() > 1000
			metric.getSum() < 2000
	}
	
	def "The stopAndStart should be able to be called from the closure"(){
		given:
			def container = new StatsContainerImpl(new HistogramMetricFactory())
		
		when:
			container.time("test_timer"){
				Thread.sleep(1000);
				stopAndStart("step1")
				Thread.sleep(1000);
			}
			
		then:
			def sum = container.getSummary()
			sum.getMetrics().size() == 2
			def metric = sum.getMetrics().get("test_timer.millis")
			metric.getCount() == 1
			metric.getSum() > 2000
			metric.getSum() < 3000
			
			def metric2 = sum.getMetrics().get("test_timer-step1.millis")
			metric2.getCount() == 1
			metric2.getSum() > 1000
			metric2.getSum() < 2000
	}
	*/
	def "dummytest"(){
		expect:
			1 == 1
	}
	
}