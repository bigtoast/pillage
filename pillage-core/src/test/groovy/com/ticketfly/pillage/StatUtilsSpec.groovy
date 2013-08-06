package com.ticketfly.pillage

import spock.lang.*
import groovy.json.*

class StatUtilsSpec extends Specification {

    def "to json would work"(){
        def stats = new StatsContainerImpl( new HistogramMetricFactory() )
        stats.incr("counter")
        stats.incr("counter2")
        def t = stats.getTimer("timer")
        t.start()
        Thread.sleep(250)
        t.stop()
        stats.add("metric", 123)
        stats.add("metric2",234)
        stats.registerGauge("g1", new Gauge() {
             double read() { 55d }
        })
        stats.registerGauge("g2", new Gauge() {
            double read() { 66d }
        })
        stats.set("label1","one")
        stats.set("label2","two")

        def json = null
        def parser = new JsonSlurper()
        def parsed = null

        when:
            json = stats.getSummary().toString()
            parsed = parser.parseText(json)
        then:
            parsed.counters.size() == 2
            parsed.metrics.size() == 3
            parsed.gauges.size() == 2
            parsed.labels.size() == 2
    }

}
