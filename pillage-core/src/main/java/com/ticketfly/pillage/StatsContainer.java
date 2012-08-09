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

package com.ticketfly.pillage;

import java.util.Map;

/**
 * A stats provider gives access to gather statistics
 */
public interface StatsContainer {
    /**
   * Adds a value to a named metric, which tracks min, max, mean, and a histogram.
   */
  public void add(String name, int value);

  /**
   * Adds a set of values to a named metric. Effectively the incoming distribution is merged with
   * the named metric.
   */
  public void add(String name, Distribution distribution);

  /**
   * Increments a counter, returning the new value.
   */
  public void incr(String name, int count);

  /**
   * Increments a counter by one, returning the new value.
   */
  public void incr(String name);

  /**
   * Set a label to a string.
   */
  public void set(String name, String value);

  /**
   * Clear an existing label.
   */
  public void clearLabel(String name);
  
  /**
   * Clear metric
   * 
   * @param name
   */
  public void clearMetric(String name);

  /**
   * Clear counter
   * 
   * It actually resets the counter but I am going for consistency in naming
   * @param name
   */
  public void clearCounter(String name);
  
  /**
   * Get the Counter object representing a named counter.
   */
  public Counter getCounter(String name);

  /**
   * Get the Metric object representing a named metric.
   */
  public Metric getMetric(String name);

  /**
   * Get the current value of a named label, if it exists.
   */
  public String getLabel(String name);
  
    /**
     * return new stopwatch.
     *
     * @param name String
     * @return a new timer object
     */
  public Timer getTimer(String name);
  
  /**
   * evaluate all the counters in this collection.
   */
  public Map<String,Long> counters();

  /**
   * evaluate all the metrics in this collection.
   */
  public Map<String,Distribution> metrics();

  /**
   * evaluate all the labels in this collection.
   */
  public Map<String,String> labels();

  /**
   * evalutate all the gauges in this container
   */
  public Map<String, Double> gauges();
  
  /**
   * Register a gauge with the container. 
   * 
   * @param name
   * @param gauge
   */
  public void registerGauge(String name, Gauge gauge);

  /**
   * Remove the gauge registered with name from this container.
   * 
   * @param name
   */
  public void deregisterGauge(String name);
  
  
  /**
   * Reset all collected stats and erase the history.
   * Probably only useful for unit tests.
   */
   public void clearAll();
   
   /**
    * Return a summary of the stats since the provider was started or the last time
    * clear() was called, whichever was called more recently.
    * 
    * @return StatsSummary
    */
   public StatsSummary getSummary();

}
