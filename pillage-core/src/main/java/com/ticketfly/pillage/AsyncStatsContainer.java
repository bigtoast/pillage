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

package com.ticketfly.pillage;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class AsyncStatsContainer implements StatsContainer {
	
	public static interface StatsCommand{}
	
	private class AsyncStatsListener implements Runnable {	
		
		@Override
		public void run() {
			boolean running = true;
			while( running ) {
			   try {
				  StatsCommand command = queue.take();
				  if ( command instanceof AddMetric ){
					  AddMetric c = (AddMetric) command;
					  container.add(c.name, c.value);
				  }
				  else if ( command instanceof ClearMetric ){
					  ClearMetric c = (ClearMetric) command;
					  container.clearMetric(c.name);
				  }
				  else if ( command instanceof AddDistribution ){
					  AddDistribution c = (AddDistribution) command;
					  container.add( c.name, c.dist );
				  }
				  else if ( command instanceof IncCounter ){
					  IncCounter c = (IncCounter) command;
					  container.incr(c.name, c.value);
				  }
				  else if ( command instanceof ClearCounter ){
					  ClearCounter c = (ClearCounter) command;
					  container.clearCounter(c.name);
				  }
				  else if ( command instanceof SetLabel ){
					  SetLabel c = (SetLabel) command;
					  container.set(c.name, c.value);
				  }
				  else if ( command instanceof ClearLabel ){
					  ClearLabel c = (ClearLabel) command;
					  container.clearLabel(c.name);
				  }
			   } catch ( InterruptedException e ) {
				   running = false;
			   }
			}
		}
	}
	
	private StatsContainer container;
	private LinkedBlockingQueue<StatsCommand> queue;
	private ExecutorService executor;
	
	public AsyncStatsContainer( StatsContainer container ){
		this.container = container;
		this.queue = new LinkedBlockingQueue<StatsCommand>();
		this.executor = Executors.newSingleThreadExecutor();
		executor.execute( new AsyncStatsListener() );		
	}
	
	public void shutdown() {
		this.executor.shutdownNow();
	}
	
	private static class AddMetric implements StatsCommand {
		final public String name;
		final public int value;
		AddMetric( String name, int value ){
			this.name = name;
			this.value = value;
		}
	}
	
	private static class ClearMetric implements StatsCommand {
		final public String name;
		ClearMetric( String name ){
			this.name = name;
		}
	}
	
	private static class AddDistribution implements StatsCommand {
		final public String name;
		final public Distribution dist;
		AddDistribution( String name, Distribution dist ){
			this.name = name;
			this.dist = dist;
		}
	}
	
	private static class IncCounter implements StatsCommand {
		final public String name;
		final public int value;
		IncCounter(String name, int value){
			this.name = name;
			this.value = value;
		}
	}
	
	private static class ClearCounter implements StatsCommand{
	    final public String name;
	    ClearCounter(String name){
	    	this.name = name;
	    }
	}
	
	public static class SetLabel implements StatsCommand{
		final public String name;
		final public String value;
		SetLabel(String name, String value ){
			this.name = name;
			this.value = value;
		}
	}
	
	public static class ClearLabel implements StatsCommand{
		final public String name;
		ClearLabel(String name){
			this.name = name;
		}
	}

	@Override
	public void add(String name, int value) {
		queue.offer(new AddMetric(name, value ));
	}

	@Override
	public void add(String name, Distribution distribution) {
		queue.offer(new AddDistribution(name, distribution));
	}

	@Override
	public void incr(String name, int count) {
		queue.offer(new IncCounter(name, count));

	}

	@Override
	public void incr(String name) {
		queue.offer(new IncCounter(name, 1));
	}

	@Override
	public void set(String name, String value) {
		queue.offer(new SetLabel(name, value));
	}

	@Override
	public void clearLabel(String name) {
		queue.offer(new ClearLabel(name));
	}

	@Override
	public void clearMetric(String name) {
		queue.offer(new ClearMetric( name ));
	}

	@Override
	public void clearCounter(String name) {
		queue.offer( new ClearCounter(name) );
	}

	@Override
	public Counter getCounter(String name) {
		return container.getCounter(name);
	}

	@Override
	public Metric getMetric(String name) {
		return new AsyncMetric(name, this);
	}

	@Override
	public String getLabel(String name) {
		return container.getLabel(name);
	}

	@Override
	public Timer getTimer(String name) {
		return new Timer( this, name);
	}

	@Override
	public Map<String, Long> counters() {
		return container.counters();
	}

	@Override
	public Map<String, Distribution> metrics() {
		return container.metrics();
	}

	@Override
	public Map<String, String> labels() {
		return container.labels();
	}

	@Override
	public Map<String, Double> gauges() {
		return container.gauges();
	}

	@Override
	public void registerGauge(String name, Gauge gauge) {
		container.registerGauge(name, gauge);

	}

	@Override
	public void deregisterGauge(String name) {
		container.deregisterGauge(name);

	}

	@Override
	public void clearAll() {
		container.clearAll();

	}

	@Override
	public StatsSummary getSummary() {
		return container.getSummary();
	}

}
