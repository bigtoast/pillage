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

/**
 * This is a metric backed by a histogram.
 */
public class HistogramMetric implements Metric {
    //val log = Logger.get(getClass.getName)

  private Histogram _histogram = new Histogram();

  public HistogramMetric( Histogram histogram) {
      _histogram = histogram;
  }

  public HistogramMetric(){}

  /**
   * Resets the state of this Metric. Clears all data points collected so far.
   */
  @Override
  public void clear() {
    synchronized(this) {
      _histogram.clear();
    }
  }

  /**
   * Adds a data point.
   */
  @Override
  public long add( int n){
    if (n > -1) {
      synchronized(this) {
        return _histogram.add(n);
      }
    } else {
      //log.warning("Tried to add a negative data point.")
      return _histogram.getCount();
    }
  }

  /**
   * Add a summarized set of data points.
   */
  @Override
  public long add(Distribution dist) {
    synchronized( this ){
      _histogram.merge( ((HistogramDistribution)dist).getHistogram() );
      return  _histogram.getCount();
    }
  }

  @Override
  public HistogramMetric clone(){
    return new HistogramMetric(_histogram.clone());
  }

  /**
   * Returns a Distribution for this Metric.
   */
  public HistogramDistribution getDistribution() {
      synchronized( this) {
        return _histogram.getDistribution();
      }
  }

}
