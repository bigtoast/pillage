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
 * A metric collects data points
 */

public interface Metric extends Cloneable {

    /**
     * clear all data points for this metric
     */
    public void clear();

    /**
     * add data point value to the metric
     * @param i int
     * @return  data point count
     */
    public long add( int i);

    /**
     * add a distribution to this metric
     * @param d
     * @return data point count
     */
    public long add( Distribution d );

    /**
     * The value of a metric is represented by a Distribution immutable object
     * @return
     */
    public Distribution getDistribution();

}

