package atd.pillage

import spock.lang.*

class GraphiteStatsReporterSpec extends Specification {

	@Shared String addy = "23.21.189.231"
	@Shared def port = 2003
	@Shared GraphiteStatsReporter reporter
	@Shared StatsContainer stats
	
	def setupSpec(){
      stats = new StatsContainerImpl( new HistogramMetricFactory() )
	  reporter = new GraphiteStatsReporter( addy, port )	
	}
	
	def "send some stats to graphite"(){
		when:
		  stats.incr("tester")
		  50.times { stats.add("mettest",  ( int ) Math.random() * 1000 ) }
		  reporter.report( stats.getSummary())
		then:
		  1 == 1
	}
	
}
