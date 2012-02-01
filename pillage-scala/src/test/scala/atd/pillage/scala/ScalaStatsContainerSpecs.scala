package atd.pillage.scala

import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers

class ScalaStatsContainerSpecs extends WordSpec with ShouldMatchers {

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
    
    
  }
}