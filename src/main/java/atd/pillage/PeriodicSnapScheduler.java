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
import java.util.Set;
import java.util.HashSet;

/**
 * Periodic stats collector singleton. Register
 * 
 * @author ATD
 * 
 */
public class PeriodicSnapScheduler {
	private static final PeriodicSnapScheduler collector = new PeriodicSnapScheduler();
	private static final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1, new ThreadFactory() {
				public Thread newThread(Runnable runnable) {
					Thread thread = new Thread(runnable);
					thread.setDaemon(true);
					return thread;
				}
			});

	/**
	 * Inner class that wraps a StatsAccessor in runnable so the scheduler can
	 * trigger snaps.
	 * 
	 * @author andy
	 * 
	 */
	public static class StatsAccessorRunner implements Runnable {
		private StatsAccessor accessor;

		public StatsAccessorRunner(StatsAccessor accessor) {
			this.accessor = accessor;
		}

		@Override
		public void run() {
			accessor.triggerSnap();
		}
	}

	private Set<ScheduledFuture<?>> tasks = new HashSet<ScheduledFuture<?>>();

	private PeriodicSnapScheduler() {
	}

	public static PeriodicSnapScheduler getInstance() {
		return collector;
	}

	public void scheduleSnap(StatsAccessor accessor, long period, TimeUnit unit) {
		tasks.add(scheduler.scheduleAtFixedRate(new StatsAccessorRunner(
				accessor), 0, period, unit));
	}

	public static void shutdown() {
		scheduler.shutdown();
	}

	public void stop() {

		for (ScheduledFuture<?> future : tasks) {
			future.cancel(false);
		}

	}
}
