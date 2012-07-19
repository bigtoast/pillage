package atd.pillage.scala

import atd.pillage.StatsContainer
import atd.pillage.Gauge
import atd.pillage.StatsContainerImpl
import atd.pillage.HistogramMetricFactory

trait ScalaStatsContainer { self :StatsContainer =>

  def time[T]( name :String)( f : => T ) :T = {
	val timer = getTimer(name)
	timer.start
	val resp = f
	timer.stop
	resp
  }
	
  
  def gauge( name :String, func : => Double ) = {
	registerGauge(name, new Gauge() { def read = func } )	
  } 
	
}

//object PillageStats extends StatsContainerImpl( new HistogramMetricFactory ) with ScalaStatsContainer 