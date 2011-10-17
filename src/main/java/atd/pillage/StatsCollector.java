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

/**
 * This is the interface that provides access to statistics. The idea is that there needs to 
 * be a read only interface to the stats to make sure that one process reporting or working 
 * with the stats doesn't reset them or screw them up for another process.
 * 
 * Statistics can be aquired as a total or the delta between two snaps.
 * 
 * @author atd
 *
 */
public interface StatsCollector {
	
	/**
	 * Return a full summary of stats since the start of collecting stats or
	 * since the last stats clear
	 * 
	 * @return StatsSummary 
	 */
	public StatsSummary getFullSummary();
	
	/**
	 * Return a summary of stats since the last collection. This method should
	 * return the same summary until the next time collect() is called
	 * 
	 * @return StatsSummary
	 */
	public StatsSummary getDeltaSummary();
	
	/**
	 * Trigger a stats collection. 
	 * 
	 * A stats collection create a delta summary and pass this summary to 
	 * all StatsReporters.
	 * 
	 * @return StatsSummary
	 */
	public StatsSummary collect();
	
	/**
	 * Add a reporter to this collector. If the reporter already exists, no exception
	 * should be thrown
	 * 
	 * @param StatsReporter reporter
	 */
	public void addReporter(StatsReporter reporter );
	
	/**
	 * Remove a reporter from this collector. If the reporter doesn't exist, no exception
	 * should be thrown
	 * 
	 * @param StatsReporter reporter
	 */
	public void removeReporter(StatsReporter reporter);
}
