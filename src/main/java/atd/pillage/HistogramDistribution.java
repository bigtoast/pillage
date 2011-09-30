/*
 *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License. You may obtain
 *  * a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package atd.pillage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A distribution based on histograms
 */
public class HistogramDistribution implements Distribution, Serializable {

    private Histogram _histogram;

    public HistogramDistribution( Histogram histogram){
        _histogram = histogram;
    }

  /**
   * Returns a clone of the underlying histogram.
   */
    public Histogram getHistogram(){
      return _histogram.clone();
    }

    @Override
    public long getCount() {
        return _histogram.getCount();
    }

    @Override
    public long getSum() {
      return _histogram.getSum();
    }

    @Override
    public long getMinimum() {
      return _histogram.getMinimum();
    }

    @Override
    public long getMaximum() {
      return _histogram.getMaximum();
    }

    @Override
    public double getMean() {
      if ( _histogram.getCount() > 0 )
        return _histogram.getSum() / _histogram.getCount();
      else
        return 0.0d;
    }

    @Override
    public Distribution delta(Distribution dist) throws IllegalArgumentException {
      if ( ! ( dist instanceof HistogramDistribution) ){
          throw new IllegalArgumentException("Distribution of type: " + dist.getClass().getCanonicalName() + " cannot be subtracted from a HistogramDistribution.");
      }
      return new HistogramDistribution( _histogram.minus( ((HistogramDistribution) dist).getHistogram() ));
    }

    @Override
    public Map<String,Long> toMap(){
        HashMap map = new HashMap<String,Long>();
        map.put("count", getCount());
        map.put("sum", getSum());
        map.put("minimum", getMinimum());
        map.put("maximum", getMaximum());
        map.put("mean", getMean());
        if( _histogram.getCount() > 0 ) {
            map.put("p25", _histogram.getPercentile(0.25d));
            map.put("p50", _histogram.getPercentile(0.5d));
            map.put("p75", _histogram.getPercentile(0.75d));
            map.put("p90", _histogram.getPercentile(0.9d));
            map.put("p95", _histogram.getPercentile(0.95d));
            map.put("p99", _histogram.getPercentile(0.99d));
            map.put("p999", _histogram.getPercentile(0.999d));
            map.put("p9999", _histogram.getPercentile(0.9999d));
        }
        return map;
    }

    @Override
    public boolean equals( Object obj) {
        if(!(obj instanceof HistogramDistribution))
          return false;

        return _histogram.equals( ((HistogramDistribution) obj).getHistogram() );
    }
}
