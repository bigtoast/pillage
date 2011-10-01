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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.Map;

/**
 * Periodic stats collector singleton. 
 * @author ATD
 *
 */
public class PeriodicStatsCollector {
	private static final PeriodicStatsCollector _collector = new PeriodicStatsCollector();
	private static final ScheduledExecutorService _timer = Executors.newScheduledThreadPool(1, new ThreadFactory() {
		public Thread newThread(Runnable runnable){
			Thread thread = new Thread(runnable);
			thread.setDaemon(true);
			return thread;
		}
	});
	private Map<String, ScheduledFuture<?>> _tasks;
	
	private PeriodicStatsCollector(){}
	
	public static PeriodicStatsCollector getInstance() {
		return _collector;
	}
	
	public void scheduleSnap(){
		_timer.scheduleAtFixedRate(new Runnable(){
			public void run(){}
		}, 0, 60, TimeUnit.SECONDS);
	}
	
	public static void shutdown(){ _timer.shutdown(); }
}
