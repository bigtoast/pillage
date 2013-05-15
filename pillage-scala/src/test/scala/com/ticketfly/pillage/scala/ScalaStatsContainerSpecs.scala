package com.ticketfly.pillage.scala

import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import com.ticketfly.pillage._

class ScalaStatsContainerSpecs extends WordSpec with ShouldMatchers {

  object PillageStats extends StatsContainerImpl( new HistogramMetricFactory ) with ScalaStatsContainer 
  "testing in gradle" should {
    "freakin work" in {
      1 should be (1)
    }
  }
  
  "The PillageStats object" should {
    "create some counters" in {
      PillageStats incr "test-counter"
      PillageStats incr("test-counter", 100)
      
      PillageStats getCounter("test-counter") value() should be (101)
    }
    
    "time some stuff" in {
      PillageStats.time[Unit]("test-timer"){
        Thread.sleep(500)
      }
      
      PillageStats.metrics.get("test-timer.millis").getSum() should be >= (500L)
      PillageStats.metrics.get("test-timer.millis").getSum() should be < (1000L)
      
    }

    "register a gauge with curried method" in {
      PillageStats.gauge("tester2.gauge"){
        666d * math.random
      }

      val d1 :Double = PillageStats.getSummary.getGauges.get("tester2.gauge")
      d1 should be > (0d)

      val d2 :Double = PillageStats.getSummary.getGauges.get("tester2.gauge")
      d1 should not be (d2)

    }
    
    
  }
}