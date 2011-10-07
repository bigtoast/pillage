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
 * A timer times stuff.
 */
public class Timer implements Serializable, Cloneable {

	private static final long serialVersionUID = -8365175010198525518L;

	public static class TimerMetric {
		private String _name;
		private long _value;

		public TimerMetric(String name, long value) {
			_name = name;
			_value = value;
		}

		public String getName() {
			return _name;
		}

		public long getValue() {
			return _value;
		}

		public String toString() {
			StringBuilder str = new StringBuilder();
			str.append(_name);
			str.append("[");
			str.append(_value);
			str.append("]");
			return str.toString();
		}
	}

	private long _startTime = 0;
	private long _elapsedTime = 0;

	private String _name;
	private String _safeName;
	private StatsProvider _provider;
	private AtomicBoolean _running = new AtomicBoolean(false);
	private ConcurrentLinkedQueue<TimerMetric> _queue = new ConcurrentLinkedQueue<TimerMetric>();

	public Timer(StatsProvider provider, String name) {
		this(provider, name, false);
	}

	public Timer(StatsProvider provider, String name, boolean start) {
		_name = name;
		_safeName = name.replace(" ", "_");
		_provider = provider;
		if (start)
			start();
	}

	public boolean isRunning() {
		return _running.get();
	}

	/**
	 * Gets the time when this instance was created, or when one of the
	 * <tt>start()</tt> messages was last called.
	 * 
	 * @return The start time in milliseconds since the epoch.
	 */
	public long getStartTime() {
		return _startTime;
	}

	/**
	 * Gets the time in milliseconds between when this Timer was last started
	 * and stopped. Is <tt>stop()</tt> was not called, then the time returned is
	 * the time since the Timer was started.
	 * 
	 * @return The elapsed time in milliseconds.
	 */
	public long getElapsedTime() {
		return (_elapsedTime == -1L) ? System.currentTimeMillis() - _startTime
				: _elapsedTime;
	}

	/**
	 * Gets the tag used to group this Timer instance with other instances used
	 * to time the same code block.
	 * 
	 * @return The grouping tag.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * returns the name as it will appear as a statistic. Spaces are replaced
	 * with underscores.
	 * 
	 * @return name with spaces replaced.
	 * 
	 */
	public String getSafeName() {
		return _safeName;
	}

	// --- Start/Stop methods ---

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
		_startTime = System.currentTimeMillis();
		_elapsedTime = -1L;
		_running.set(true);
	}

	/**
	 * Stops this Timer, which "freezes" its elapsed time. You should normally
	 * call this method (or one of the other stop methods) before passing this
	 * instance to a logger.
	 * 
	 * @return this.toString(), which is a message suitable for logging
	 */
	public long stop(String milestone) {
		if (_running.getAndSet(false) == true) {
			_elapsedTime = System.currentTimeMillis() - _startTime;
			String mName = metricName(milestone);
			Metric m = _provider.getMetric(mName);
			m.add((int) _elapsedTime);
			_queue.add(new TimerMetric(metricName(milestone), _elapsedTime));
		}
		return _elapsedTime;
	}

	protected String metricName(String milestone) {
		StringBuilder str = new StringBuilder();
		str.append(_safeName);
		str.append("-");
		str.append(milestone.replace(" ", "_"));
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
		for (TimerMetric metric : _queue) {
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

		if (_elapsedTime != timer.getElapsedTime()) {
			return false;
		}
		if (_startTime != timer.getStartTime()) {
			return false;
		}

		if (_running.get() != timer.isRunning()) {
			return false;
		}

		if (_name != null ? !_name.equals(timer.getName())
				: timer.getName() != null) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		int result;
		result = (int) (_startTime ^ (_startTime >>> 32));
		result = 31 * result + (int) (_elapsedTime ^ (_elapsedTime >>> 32));
		result = 31 * result + (_name != null ? _name.hashCode() : 0);
		result = 31 * result + (_provider != null ? _provider.hashCode() : 0);
		result = 31 * result + (_running != null ? _running.hashCode() : 0);
		return result;
	}
}
