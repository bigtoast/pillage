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

import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A timer times stuff and uses a metric to track the durations in milliseconds. A metric will be
 * created for each milestone in the following format
 * 
 * {timer name}-{milestone_name}.millis
 * 
 * Any spaces in the timer name or milestone name will be replaced with underscores.
 */
public class Timer implements Serializable, Cloneable {

	private static final long serialVersionUID = -8365175010198525518L;

	public static class TimerMetric {
		private String name;
		private long value;

		public TimerMetric(String name, long value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public long getValue() {
			return value;
		}

		public String toString() {
			StringBuilder str = new StringBuilder();
			str.append(name);
			str.append("[");
			str.append(value);
			str.append("]");
			return str.toString();
		}
	}

	private long startTime = 0;
	private long elapsedTime = 0;

	private String name;
	private String safeName;
	private StatsContainer container;
	private AtomicBoolean running = new AtomicBoolean(false);
	private ConcurrentLinkedQueue<TimerMetric> queue = new ConcurrentLinkedQueue<TimerMetric>();

	public Timer(StatsContainer container, String name) {
		this(container, name, false);
	}

	public Timer(StatsContainer container, String name, boolean start) {
		this.name = name;
		this.safeName = name.replace(" ", "_");
		this.container = container;
		if (start)
			start();
	}

	public boolean isRunning() {
		return running.get();
	}

	/**
	 * Gets the time when this instance was created, or when one of the
	 * <tt>start()</tt> messages was last called.
	 * 
	 * @return The start time in milliseconds since the epoch.
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Gets the time in milliseconds between when this Timer was last started
	 * and stopped. Is <tt>stop()</tt> was not called, then the time returned is
	 * the time since the Timer was started.
	 * 
	 * @return The elapsed time in milliseconds.
	 */
	public long getElapsedTime() {
		return (elapsedTime == -1L) ? System.currentTimeMillis() - startTime
				: elapsedTime;
	}

	/**
	 * Gets the tag used to group this Timer instance with other instances used
	 * to time the same code block.
	 * 
	 * @return The grouping tag.
	 */
	public String getName() {
		return name;
	}

	/**
	 * returns the name as it will appear as a statistic. Spaces are replaced
	 * with underscores.
	 * 
	 * @return name with spaces replaced.
	 * 
	 */
	public String getSafeName() {
		return safeName;
	}

	/**
	 * Starts this Timer, which sets its startTime property to the current time
	 * and resets the elapsedTime property. For single-use Timer instance you
	 * should not need to call this method as a Timer is automatically started
	 * when it is created. Note any existing tag and message are not changed.
	 * 
	 * If the watcher is already running this method acts as a reset. It will
	 * reset the start time to NOW and clear the elapsed time.
	 */
	public void start() {
		startTime = System.currentTimeMillis();
		elapsedTime = -1L;
		running.set(true);
	}

	/**
	 * Stops this Timer, which "freezes" its elapsed time. You should normally
	 * call this method (or one of the other stop methods) before passing this
	 * instance to a logger.
	 * 
	 * @return elapsed time
	 */
	public long stop(String milestone) {
		if (running.getAndSet(false) == true) {
			elapsedTime = System.currentTimeMillis() - startTime;
			String mName = metricName(milestone);
			Metric m = container.getMetric(mName);
			m.add((int) elapsedTime);
			queue.add(new TimerMetric(metricName(milestone), elapsedTime));
		}
		return elapsedTime;
	}
	
	/**
	 * Stop the timer and create a metric with the timer name and elapsed time
	 * in millis.
	 * @return
	 */
	public long stop() {
		if (running.getAndSet(false) == true) {
			elapsedTime = System.currentTimeMillis() - startTime;
			Metric m = container.getMetric(metricName(null));
			m.add((int) elapsedTime);
		}
		return elapsedTime;
	}

	protected String metricName(String milestone) {
		StringBuilder str = new StringBuilder();
		str.append(safeName);
		if( milestone != null ){
			str.append("-");
			str.append(milestone.replace(" ", "_"));
		}
		str.append(".millis");
		return str.toString();
	}

	/**
	 * Stops this Timer, collect a metric and restart the timer.
	 * 
	 * @param milestone
	 *            Add a milestone to the metric
	 * @return this.toString(), which is a message suitable for logging
	 */
	public long stopAndStart(String milestone) {
		long elapsed = stop(milestone);
		start();
		return elapsed;
	}

	// --- Object Methods ---
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (TimerMetric metric : queue) {
			str.append(metric.toString());
			str.append(" : ");
		}
		return str.toString();
	}

	public Timer clone() {
		try {
			return (Timer) super.clone();
		} catch (CloneNotSupportedException cnse) {
			throw new Error("Unexpected CloneNotSupportedException");
		}
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Timer)) {
			return false;
		}

		Timer timer = (Timer) o;

		if (elapsedTime != timer.getElapsedTime()) {
			return false;
		}
		if (startTime != timer.getStartTime()) {
			return false;
		}

		if (running.get() != timer.isRunning()) {
			return false;
		}

		if (name != null ? !name.equals(timer.getName())
				: timer.getName() != null) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		int result;
		result = (int) (startTime ^ (startTime >>> 32));
		result = 31 * result + (int) (elapsedTime ^ (elapsedTime >>> 32));
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (container != null ? container.hashCode() : 0);
		result = 31 * result + (running != null ? running.hashCode() : 0);
		return result;
	}
}
