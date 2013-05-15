package com.ticketfly.pillage.scala

import com.ticketfly.pillage.StatsContainer
import com.ticketfly.pillage.Gauge
import com.ticketfly.pillage.StatsContainerImpl
import com.ticketfly.pillage.HistogramMetricFactory

trait ScalaStatsContainer { self :StatsContainer =>

  def time[T]( name :String)( f : => T ) :T = {
	  val timer = getTimer(name)
	  timer.start
	  val resp = f
	  timer.stop
	  resp
  }

  def gauge( name :String )( func : => Double ) =
    registerGauge(name, new Gauge() { def read = func } )
	
}

