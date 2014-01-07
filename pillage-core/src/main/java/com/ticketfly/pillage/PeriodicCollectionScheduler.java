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

package com.ticketfly.pillage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.Set;
import java.util.HashSet;

/**
 * This is a singleton that allows you to schedule collections for a StatsCollector. Under
 * the hood it wraps a ScheduledExecutorService and runs a thread pool with a single daemon
 * thread.
 * 
 * 
 * @author ATD
 * 
 */
public class PeriodicCollectionScheduler {
	private static final PeriodicCollectionScheduler instance = new PeriodicCollectionScheduler();
	private static final ScheduledExecutorService service = Executors
			.newScheduledThreadPool(1, new ThreadFactory() {
				public Thread newThread(Runnable runnable) {
					Thread thread = new Thread(runnable);
					thread.setDaemon(true);
					return thread;
				}
			});

	/**
	 * Inner class that wraps a StatsCollector in runnable so the scheduler can
	 * call collect().
	 * 
	 * @author andy
	 * 
	 */
	public static class StatsCollectorRunner implements Runnable {
		private StatsCollector collector;

		public StatsCollectorRunner(StatsCollector collector) {
			this.collector = collector;
		}

		@Override
		public void run() {
            try {
			    collector.collect();
            } catch( Exception e ) {
                // nothing
            }
		}
	}

	private Set<ScheduledFuture<?>> tasks = new HashSet<ScheduledFuture<?>>();

	/**
	 * Private constructor
	 */
	private PeriodicCollectionScheduler(){}

	/**
	 * Return the PeriodicCollectionScheduler instance
	 * @return
	 */
	public static PeriodicCollectionScheduler getInstance() {
		return instance;
	}

	/**
	 * Schedule a collection for the given collector.
	 *
	 * @param collector
	 * @param period
	 * @param unit
	 */
	public void scheduleCollection(StatsCollector collector, long period, TimeUnit unit) {
		tasks.add(service.scheduleAtFixedRate(new StatsCollectorRunner(
				collector), 0, period, unit));
	}

	/**
	 * Shutdown the service.
	 */
	public static void shutdown() {
		service.shutdown();
	}

	/**
	 * Cancel the scheduled futures.
	 */
	public void stop() {

		for (ScheduledFuture<?> future : tasks) {
			future.cancel(false);
		}

	}
}
