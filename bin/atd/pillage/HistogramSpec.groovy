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

package atd.pillage

import atd.pillage.Histogram
import spock.lang.*

/**
 * HistogramSpec
 */
public class HistogramSpec extends Specification {
    def hist1 = new Histogram();
    def hist2 = new Histogram();

    def setup() {
        hist1.clear()
        hist2.clear()
    }

    def "A histogram should find the right bucket for various timings"(){
        when:
            hist1.add(0)
        then:
            hist1.get(true)[0] == 1
        when:
            hist1.add(9999999)
        then:
            def ary = hist1.get(true)
            ary[ary.length - 1] == 1

        when:
            hist1.add(1)
        then:
            hist1.get(true)[1] == 1 // offset 2

        when:
            hist1.add(2)
        then:
            hist1.get(true)[2] == 1 // offset 3

        when:
            hist1.add(10)
            hist1.add(11)
        then:
            hist1.get(true)[10] == 2 // offset 12
    }

    def "add value buckets.last" () {
        when:
            hist1.add(Histogram.BUCKET_OFFSETS[Histogram.BUCKET_OFFSETS.length - 1])
            def ary = hist1.get(true)
        then:
            ary[ary.length - 1] == 1
    }

    def "add value buckets.last+1"(){
      when:
        hist1.add(Histogram.BUCKET_OFFSETS[Histogram.BUCKET_OFFSETS.length - 1] + 1)
        def ary = hist1.get(true)
      then:
        ary[ary.length - 1] == 1
    }

    def "add value Integer.MAX_VALUE"(){
      when:
        hist1.add(Integer.MAX_VALUE)
        def ary = hist1.get(true)
      then:
        ary[ary.length - 1] == 1
    }

    def "add value Integer.MIN_VALUE"(){
      when:
        hist1.add(Integer.MIN_VALUE)

      then:
        hist1.get(true)[0] == 1
    }

    def "find histogram cutoffs for various percentages"(){
      when:
        (0..1000).each{ n ->
            hist1.add(n)
        }

      then:
        Histogram.binarySearch(hist1.getPercentile(0.0d)) == Histogram.binarySearch(0)
        Histogram.binarySearch(hist1.getPercentile(0.5d)) == Histogram.binarySearch(500)
        Histogram.binarySearch(hist1.getPercentile(0.9d)) == Histogram.binarySearch(900)
        Histogram.binarySearch(hist1.getPercentile(0.99d)) == Histogram.binarySearch(998) // 999 is boundary
        Histogram.binarySearch(hist1.getPercentile(1.0d)) == Histogram.binarySearch(1000)
    }

    def "do clone"() {
      when:
        (0..50).each{ i ->
              hist1.add(i * 10)
        }

      then:
        def histClone = hist1.clone()
        hist1._buckets.toList().containsAll(histClone._buckets.toList()) == true
        histClone._buckets.toList().containsAll( hist1._buckets.toList()) == true
        hist1.count == histClone.count
    }

    def "handle a very large timing"(){
      when:
        hist1.add(100000000)

      then:
        hist1.getPercentile(0.0) == Integer.MAX_VALUE
        hist1.getPercentile(0.1) == Integer.MAX_VALUE
        hist1.getPercentile(0.9) == Integer.MAX_VALUE
        hist1.getPercentile(1.0) == Integer.MAX_VALUE
    }

    def "handle an empty histogram"(){
      expect:
        hist1.getPercentile(0.0) == 0
        hist1.getPercentile(0.1) == 0
        hist1.getPercentile(0.9) == 0
        hist1.getPercentile(1.0) == 0
    }

    def "track count and sum"(){
      when:
        hist1.add(10)
        hist1.add(15)
        hist1.add(20)
        hist1.add(20)

      then:
        hist1.count == 4
        hist1.sum == 65
    }

    def "getPercentile"(){
      when:
        hist1.add(95)

      then:
        hist1.count == 1
        hist1.sum == 95
        // bucket covers [91, 99], midpoint is 95
        hist1.getPercentile(0.0) == 95
        hist1.getPercentile(0.5) == 95
        hist1.getPercentile(1.0) == 95
    }

    def "getPercentile with no values"(){
      expect:
        hist1.getPercentile(0.0) == 0
        hist1.getPercentile(0.5) == 0
        hist1.getPercentile(1.0) == 0
    }

    def "getPercentile with infinity"(){
      when:
        hist1.add(Integer.MAX_VALUE)
      then:
        hist1.getPercentile(0.5) == Integer.MAX_VALUE
    }

    def "getMinimum"(){
      when:
        hist1.add(95)
      then:
        hist1.getMinimum() == 95
    }

    def "getMinimum with no values"(){
      expect:
        hist1.getMinimum() == 0
    }

    def "getMinimum with infinity"(){
      when:
        hist1.add(Integer.MAX_VALUE)
      then:
        hist1.getMinimum() == Integer.MAX_VALUE
    }

    def "getMaximum"(){
      when:
        hist1.add(95)
      then:
        hist1.getMaximum() == 95
    }

    def "getMaximum with no values"(){
      expect:
        hist1.getMaximum() == 0
    }

    def "getMaximum with infinity"(){
      when:
        hist1.add(Integer.MAX_VALUE)
      then:
        hist1.getMaximum() == Integer.MAX_VALUE
    }

    def "equals"(){
      expect:
        hist1.count == hist2.count
        hist1.sum == hist2.sum
        hist1._buckets == hist2._buckets
        hist1.equals( hist2 ) == true
      when:
        hist1.add(10)
      then:
        hist1.equals( hist2) == false
      when:
        hist2.add(10)
      then:
        hist1.equals(hist2) == true
      when:
        hist1.add(5)
        hist1.add(10)
        hist2.add(15)
      then:
        hist1.equals(hist2) == false
    }
}
