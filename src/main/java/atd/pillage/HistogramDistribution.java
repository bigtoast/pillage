/*
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  not use this file except in compliance with the License. You may obtain
 *  a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package atd.pillage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A distribution based on a histogram
 */
public class HistogramDistribution implements Distribution, Serializable {

	private static final long serialVersionUID = -7341236827901521769L;

	private Histogram histogram;

    public HistogramDistribution( Histogram histogram){
        this.histogram = histogram;
    }

  /**
   * Returns a clone of the underlying histogram.
   */
    public Histogram getHistogram(){
      return histogram.clone();
    }

    @Override
    public long getCount() {
        return histogram.getCount();
    }

    @Override
    public long getSum() {
      return histogram.getSum();
    }

    /**
     * {@inheritDoc}
     * This is +/-5%
     */
    @Override
    public long getMinimum() {
      return histogram.getMinimum();
    }

    /**
     * {@inheritDoc}
     * This is +/-5%
     */
    @Override
    public long getMaximum() {
      return histogram.getMaximum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMean() {
      if ( histogram.getCount() > 0 )
        return histogram.getSum() / histogram.getCount();
      else
        return 0.0d;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Distribution delta(Distribution dist) throws IllegalArgumentException {
      if ( ! ( dist instanceof HistogramDistribution) ){
          throw new IllegalArgumentException("Distribution of type: " + dist.getClass().getCanonicalName() + " cannot be subtracted from a HistogramDistribution.");
      }
      return new HistogramDistribution( histogram.minus( ((HistogramDistribution) dist).getHistogram() ));
    }

    /**
     * {@inheritDoc}
     * 
     * this will return
     * count
     * sum
     * minimum
     * maximum
     * mean
     * p25
     * p50
     * p75
     * p90
     * p95
     * p99
     * p999
     * p9999
     * 
     * where p{num} represents percentile. i.e. p99 means the 99th percentile
     */
    @Override
    public Map<String,Number> toMap(){
        HashMap<String, Number> map = new HashMap<String,Number>();
        map.put("count", getCount());
        map.put("sum", getSum());
        map.put("minimum", getMinimum());
        map.put("maximum", getMaximum());
        map.put("mean", getMean());
        if( histogram.getCount() > 0 ) {
            map.put("p25", histogram.getPercentile(0.25d));
            map.put("p50", histogram.getPercentile(0.5d));
            map.put("p75", histogram.getPercentile(0.75d));
            map.put("p90", histogram.getPercentile(0.9d));
            map.put("p95", histogram.getPercentile(0.95d));
            map.put("p99", histogram.getPercentile(0.99d));
            map.put("p999", histogram.getPercentile(0.999d));
            map.put("p9999", histogram.getPercentile(0.9999d));
        }
        return map;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((histogram == null) ? 0 : histogram.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HistogramDistribution other = (HistogramDistribution) obj;
		if (histogram == null) {
			if (other.histogram != null)
				return false;
		} else if (!histogram.equals(other.histogram))
			return false;
		return true;
	}

}
