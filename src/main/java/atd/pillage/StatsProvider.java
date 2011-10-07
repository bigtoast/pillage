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

package atd.pillage;

import java.util.Map;

/**
 * A stats provider gives access to gather statistics
 */
public interface StatsProvider {
    /**
   * Adds a value to a named metric, which tracks min, max, mean, and a histogram.
   */
  public void addMetric(String name, int value);

  /**
   * Adds a set of values to a named metric. Effectively the incoming distribution is merged with
   * the named metric.
   */
  public void addMetric(String name, Distribution distribution);

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
  public void setLabel(String name,String value);

  /**
   * Clear an existing label.
   */
  public void clearLabel(String name);

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
     * @return StopWatch
     */
  public Timer getTimer(String name);
  /**
   * Summarize all the counters in this collection.
   */
  public Map<String,Long> getCounters();

  /**
   * Summarize all the metrics in this collection.
   */
  public Map<String,Distribution> getMetrics();

  /**
   * Summarize all the labels in this collection.
   */
  public Map<String,String> getLabels();

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

  /**
   * Runs the function f and logs that duration, in milliseconds, with the given name.
   */
  /*def time[T](name: String)(f: => T): T = {
    val (rv, duration) = Duration.inMilliseconds(f)
    addMetric(name + "_msec", duration.inMilliseconds.toInt)
    rv
  } */

  /**
   * Runs the function f and logs that duration until the future is satisfied, in microseconds, with
   * the given name.
   */
  /*def timeFutureMicros[T](name: String)(f: Future[T]): Future[T] = {
    val start = Time.now
    f.respond { _ =>
      addMetric(name + "_usec", start.untilNow.inMicroseconds.toInt)
    }
    f
  } */

  /**
   * Runs the function f and logs that duration until the future is satisfied, in milliseconds, with
   * the given name.
   */
  /*def timeFutureMillis[T](name: String)(f: Future[T]): Future[T] = {
    val start = Time.now
    f.respond { _ =>
      addMetric(name + "_msec", start.untilNow.inMilliseconds.toInt)
    }
    f
  } */

  /**
   * Runs the function f and logs that duration until the future is satisfied, in nanoseconds, with
   * the given name.
   */
  /*def timeFutureNanos[T](name: String)(f: Future[T]): Future[T] = {
    val start = Time.now
    f.respond { _ =>
      addMetric(name + "_nsec", start.untilNow.inNanoseconds.toInt)
    }
    f
  } */

  /**
   * Runs the function f and logs that duration, in microseconds, with the given name.
   */
  /*def timeMicros[T](name: String)(f: => T): T = {
    val (rv, duration) = Duration.inNanoseconds(f)
    addMetric(name + "_usec", duration.inMicroseconds.toInt)
    rv
  } */

  /**
   * Runs the function f and logs that duration, in nanoseconds, with the given name.
   */
  /*def timeNanos[T](name: String)(f: => T): T = {
    val (rv, duration) = Duration.inNanoseconds(f)
    addMetric(name + "_nsec", duration.inNanoseconds.toInt)
    rv
  } */
}
