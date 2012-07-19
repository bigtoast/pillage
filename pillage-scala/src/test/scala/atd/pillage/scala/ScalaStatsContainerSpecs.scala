package atd.pillage.scala

import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import atd.pillage._

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
    
    "register a gauge" in {
      PillageStats.gauge("test.gauge", 6d * math.random)
      
      val d :Double = PillageStats.getSummary.getGauges.get("test.gauge") 
      d should be > (0D)
      
      val d2 :Double = PillageStats.getSummary.getGauges.get("test.gauge")
      
      d should not be (d2)
    }
    
    
  }
}