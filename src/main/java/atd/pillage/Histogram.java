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

import java.util.Arrays;

/**
 * Collect data points into buckets
 */
public class Histogram implements Cloneable {

  /*
   * The midpoint of each bucket is +/- 5% from the boundaries.
   *   (0..139).map { |n| (1.10526315 ** n).to_i + 1 }.uniq
   * Bucket i is the range from BUCKET_OFFSETS(i-1) (inclusive) to
   * BUCKET_OFFSETS(i) (exclusive).
   * The last bucket (the "infinity" bucket) is from 1100858 to infinity.
   */
    final static public int[] BUCKET_OFFSETS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15, 17, 19, 21, 23, 25, 28,
          31, 34, 37, 41, 45, 50, 55, 61, 67, 74, 82, 91, 100, 111, 122, 135,
          150, 165, 183, 202, 223, 246, 272, 301, 332, 367, 406, 449, 496, 548,
          606, 669, 740, 817, 903, 999, 1104, 1220, 1348, 1490, 1647, 1820, 2011,
          2223, 2457, 2716, 3001, 3317, 3666, 4052, 4479, 4950, 5471, 6047, 6684,
          7387, 8165, 9024, 9974, 11024, 12184, 13467, 14884, 16451, 18182, 20096,
          22212, 24550, 27134, 29990, 33147, 36636, 40492, 44754, 49465, 54672,
          60427, 66787, 73818, 81588, 90176, 99668, 110160, 121755, 134572,
          148737, 164393, 181698, 200824, 221963, 245328, 271152, 299694, 331240,
          366108, 404645, 447240, 494317, 546351, 603861, 667426, 737681, 815331,
          901156, 996014, 1100858};

    public Histogram(){}

    private static int binarySearch( int[] array, int key, int low, int high) {
        if (low > high) {
          return low;
        } else {
          int mid = (low + high + 1) >> 1;
          int midValue = array[mid];
          if (midValue < key) {
           return binarySearch(array, key, mid + 1, high);
          } else if (midValue > key) {
           return binarySearch(array, key, low, mid - 1);
          } else {
            // exactly equal to this bucket's value. but the value is an exclusive max, so bump it up.
            return mid + 1;
          }
        }
    }

    public static int binarySearch( int key ) {
        return binarySearch(BUCKET_OFFSETS, key, 0, BUCKET_OFFSETS.length - 1);
    }

    public int bucketIndex(int key){ return binarySearch(key); }

    private int numBuckets = BUCKET_OFFSETS.length + 1;
    private long[] buckets = new long[numBuckets];

    private long count = 0;
    private long sum = 0;

    public long getCount(){ return count; }
    public long getSum(){ return sum; }

    public long addToBucket(int index) {
        buckets[index]++;
        return ++count;
    }

    public long add(int n){
        addToBucket(bucketIndex(n));
        sum += n;
        return count;
    }

    public void clear() {
        for(int i=0; i < numBuckets; i++){
            buckets[i] = 0;
        }
        count = 0;
        sum = 0;
    }

    public long[] get(boolean reset){
        long[] rv = buckets.clone();
        if (reset) {
            clear();
        }
        return rv;
    }

  /**
   * Percentile within 5%, but:
   *   0 if no values
   *   Int.MaxValue if percentile is out of range
   */
  public int getPercentile(double percentile){
    if (percentile == 0.0)
      return getMinimum();

    long total = 0L;
    int index = 0;

    while (total < percentile * count) {
      total += buckets[index];
      index += 1;
    }

    if (index == 0) {
      return 0;
    } else if (index - 1 >= BUCKET_OFFSETS.length) {
      return Integer.MAX_VALUE;
    } else {
      return getMidpoint(index - 1);
    }
  }

  /**
   * Maximum value within 5%, but:
   *    0 if no values
   *    Int.MaxValue if any value is infinity
   */
  public int getMaximum(){
    if (buckets[buckets.length - 1] > 0) {
      // Infinity bucket has a value
      return Integer.MAX_VALUE;
    } else if (count == 0) {
      // No values
        return 0;
    } else {
      int index = BUCKET_OFFSETS.length - 1;
      while (index >= 0 && buckets[index] == 0)
        index -= 1;
      if (index < 0)
        return 0;
      else
        return getMidpoint(index);
    }
  }

  /**
   * Minimum value within 5%, but:
   *    0 if no values
   *    Int.MaxValue if all values are infinity
   */
  public int getMinimum() {
    if (count == 0) {
      return 0;
    } else {
      int index = 0;
      while (index < BUCKET_OFFSETS.length && buckets[index] == 0)
        index += 1;
      if (index >= BUCKET_OFFSETS.length)
        return Integer.MAX_VALUE;
      else
        return getMidpoint(index);
    }
  }

  // Get midpoint of bucket
  protected int getMidpoint( int index ) {
    if (index == 0)
      return 0;
    else if (index - 1 >= BUCKET_OFFSETS.length)
      return Integer.MAX_VALUE;
    else
      return (BUCKET_OFFSETS[index - 1] + BUCKET_OFFSETS[index] - 1) / 2;
  }

  public void merge(Histogram other){
    if (other.count > 0) {
      for(int i=0; i < numBuckets; i++){
          buckets[i] += other.buckets[i];
      }

      count += other.getCount();
      sum += other.getSum();
    }
  }

  public Histogram minus(Histogram other) {
    Histogram rv = new Histogram();
    rv.count = count - other.getCount();
    rv.sum = sum - other.getSum();
    for(int i=0; i < numBuckets; i++){
          rv.buckets[i] = buckets[i] - other.buckets[i];
      }
    return rv;
  }


@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + Arrays.hashCode(buckets);
	result = prime * result + (int) (count ^ (count >>> 32));
	result = prime * result + (int) (sum ^ (sum >>> 32));
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
	Histogram other = (Histogram) obj;
	if (!Arrays.equals(buckets, other.buckets))
		return false;
	if (count != other.count)
		return false;
	if (sum != other.sum)
		return false;
	return true;
}

  @Override
  public String toString(){
    StringBuilder str = new StringBuilder();
    str.append("<Histogram count=");
    str.append(count);
    str.append(" sum=");
    str.append(sum);
    for( int i=0; i < buckets.length; i++){
        str.append(" ");
        str.append(BUCKET_OFFSETS[i]);
        str.append("=");
        str.append(buckets[i]);
    }
    str.append(" />");
    return str.toString();
  }

  @Override
  public Histogram clone(){
    Histogram hist = new Histogram();
    hist.merge(this);
    return hist;
  }

  public HistogramDistribution getDistribution() {
      return new HistogramDistribution(clone());
  }

}
