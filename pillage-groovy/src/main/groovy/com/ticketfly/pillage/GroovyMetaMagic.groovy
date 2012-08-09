
package com.ticketfly.pillage;


/**
 * Adds a time method to a stats container. Use it thusly:
 * <pre>
 * container.time("timing_a_task"){
 *   // block of code to be timed
 * }
 * </pre>
 * 
 * You can also use timer methods from within the closure.
 * <pre>
 * 	container.time("time_a_task_with_milestones"){
 *    // do somestuff
 *    
 *    stopAndStart("milestone1")
 *    
 *    //do some more stuff
 *    
 *    stopAndStart("milestone2")
 * }
 * </pre>
 */
/*
StatsContainerImpl.metaClass.time = { String timerName, Closure c ->
	def timer = delegate.getTimer(timerName)
	c.delegate = timer
	c.resolveStrategy = Closure.DELEGATE_FIRST
	timer.start()
	c()
	timer.stop()
}*/
/*
class GroovyStatsContainer implements StatsContainer {
	@Delegate StatsContainer container;
	
	public GroovyStatsContainer(StatsContainer container){
		this.container = container;
	}
	
	def time(String timerName, Closure c){
		def timer = delegate.getTimer(timerName)
		c.delegate = timer
		c.resolveStrategy = Closure.DELEGATE_FIRST
		timer.start()
		c()
		timer.stop()
	}
}
*/


