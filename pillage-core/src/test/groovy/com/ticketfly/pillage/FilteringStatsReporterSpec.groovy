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


package com.ticketfly.pillage


import spock.lang.*

import java.util.regex.Pattern

class FilteringStatsReporterSpec extends Specification {

    def "filtering in stats"() {
        def stats = new StatsContainerImpl( new HistogramMetricFactory() )
        stats.incr("testers.besters")
        stats.incr("testers.besters",665)

        stats.incr("nopers.besters")

        stats.add("testers.crackers", 345)
        stats.add("testers.crackers", 255)

        stats.registerGauge("testers.guagers", new Gauge() { double read(){ 666d; } } )

        def sum

        StatsReporter wrapped = new StatsReporter() { void report( StatsSummary summary ){ sum = summary } }

        Pattern pattern = Pattern.compile("testers.*")

        def reporter = new FilteringStatsReporter( wrapped, pattern )

        reporter.report(stats.getSummary())

        expect:
            sum.counters.containsKey("testers.besters") == true
            sum.counters.containsKey("nopers.besters") == false
            sum.metrics.containsKey("testers.crackers") == true

    }

    def "filtering out stats"() {
        def stats = new StatsContainerImpl( new HistogramMetricFactory() )
        stats.incr("testers.besters")
        stats.incr("testers.besters",665)

        stats.incr("nopers.besters")

        stats.add("testers.crackers", 345)
        stats.add("testers.crackers", 255)

        stats.registerGauge("testers.guagers", new Gauge() { double read(){ 666d; } } )

        def sum

        StatsReporter wrapped = new StatsReporter() { void report( StatsSummary summary ){ sum = summary } }

        Pattern pattern = Pattern.compile("testers.*")

        def reporter = new FilteringStatsReporter( wrapped, pattern )

        reporter.setFilterOut(true)

        reporter.report(stats.getSummary())

        expect:
            sum.counters.containsKey("testers.besters") == false
            sum.counters.containsKey("nopers.besters") == true
            sum.metrics.containsKey("testers.crackers") == false
    }

}
