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
	
  /*
  def gauge( name :String, func : => Any ) = {
	registerGauge(name, new Gauge() {
	  def read: Double = {
		var ret = 0d
		try {
			ret = Double.valueOf( func() )
		} catch {
			case e :ClassCastException =>
				// do nothing
		} 
	 }
    })	
  } */
	
}

object PillageStats extends StatsContainerImpl( new HistogramMetricFactory ) with ScalaStatsContainer 