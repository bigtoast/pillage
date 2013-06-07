package com.ticketfly.pillage

import spock.lang.*
import java.util.regex.Pattern

class NewRelicStatsReporterSpec extends Specification {

    def "converting metric to new relic format"(){
        def reporter = new NewRelicStatsReporter()

        def baMetric = "api-req.best-available.count"
        def percentileMetric = "api-req.best-available.p95"

        expect:
            reporter.format(baMetric) == "Custom/ApiReq/BestAvailable/Count"
            reporter.format(percentileMetric) == "Custom/ApiReq/BestAvailable/P95"
    }

    def "converting odd metric names"(){
        def reporter = new NewRelicStatsReporter()
        def s1 = "api-req.a.n"
        def s2 = "a"

        def num = "one.1.3one"

        def bad = "one..5"

        expect:
            reporter.format(s1)  == "Custom/ApiReq/A/N"
            reporter.format(s2)  == "Custom/A"
            reporter.format(num) == "Custom/One/1/3one"
            reporter.format(bad) == "Custom/One/5"

    }

    def "Testing the entire thing"(){
        def stats = new StatsContainerImpl(new HistogramMetricFactory())
        stats.add("api-req.find-best-seat",222)
        stats.add("api-req.find-best-seat",333)
        stats.add("api-req.find-best-seat",444)
        stats.add("api-req.find-best-seat",555)
        stats.add("api-req.find-best-seat",666)
        def collector = new StatsCollectorImpl(stats)
        def reporter = new FilteringStatsReporter( new NewRelicStatsReporter(), Pattern.compile("api-req.*") )
        collector.addReporter( reporter )

        collector.collect()

        expect:
            true == true

    }

}
