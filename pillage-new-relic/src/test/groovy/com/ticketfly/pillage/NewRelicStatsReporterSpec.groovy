package com.ticketfly.pillage

import spock.lang.*

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
            reporter.format(s1) == "Custom/ApiReq/A/N"
            reporter.format(s2) == "Custom/A"
            reporter.format(num) == "Custom/One/1/3one"
            reporter.format(bad) == "Custom/One/5"

    }

}
