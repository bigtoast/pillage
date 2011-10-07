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
public interface StatsAccessor {
	
	public StatsSummary getFullSummary();
	public StatsSummary getDeltaSummary();
	
	public void triggerSnap();
	
	public void addSnapReporter(StatsReporter reporter );
	public void removeSnapReporter(StatsReporter reporter);
}
