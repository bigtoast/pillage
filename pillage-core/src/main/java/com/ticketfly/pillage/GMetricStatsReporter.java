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

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


import java.util.Map;

/**
 * This reporter uses the gmetric command line client. This thing is intended to be unobtrusive so if 
 * the client path is invalid, doesn't exist to results in IOExceptions this class will fail silently.
 * You can check the canReport() method to see if this thing has a hard time reporting.
 * 
 * @author atd
 *
 */
public class GMetricStatsReporter implements StatsReporter {
	
	private String gMetric;
	private boolean canReport = false;
	private String host;
	
	private static String DOUBLE = " -t double ";
	private static String STRING = " -t string ";
	private static String INT32  = " -t int32 ";
	
	private static String NAME       = " -n ";
	private static String VALUE      = " -v ";
	private static String SLOPE_BOTH = " -s both ";
	private static String HOST       = " -S ";
	private static String LIFETIME   = " -d 60 ";

	/**
	 * 
	 * @param path to gmetric
	 * @param host name. If this is null InetAddress.getLocalHost().getHostName() will be used
	 */
	public GMetricStatsReporter( String path, String host ) {
		initGMetric( new File( path ), host );
	}
	
	/**
	 * 
	 * @param path absolute path to gmetric
	 */
	public GMetricStatsReporter( String path ) {
		this( path, null);
	}
	
	/**
	 * 
	 * @param path gmetric
	 */
	public GMetricStatsReporter( File path ){
		this( path, null );
	}
	
	/**
	 * 
	 * @param path gmetric
	 * @param host
	 */
	public GMetricStatsReporter( File path, String host ){
		initGMetric( path, host );
	}
	
	private void initGMetric( File path, String host ){
		if ( path.exists() && path.isFile() && path.canExecute() ){
			gMetric = path.getAbsolutePath();
			canReport = true;
		}
		
		if ( host != null ){
			this.host = host;
		} else {
			try {
			  this.host = InetAddress.getLocalHost().getHostName();
			} catch ( UnknownHostException e ){
				this.host = "UNKNOWN_HOST";
			}
		}
	}
	
	public boolean canReport() {
		return canReport;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * report the stats
	 */
	@Override
	public void report(StatsSummary stats) {
     if ( canReport ){
		  for( Map.Entry<String, Long> entry : stats.getCounters().entrySet()){
			  reportCounter( entry.getKey(), entry.getValue() );
		  }
		  
		  for ( Map.Entry<String, String> entry : stats.getLabels().entrySet()){
			  reportLabel( entry.getKey(), entry.getValue() );
		  }
		  
		  for ( Map.Entry<String, Distribution> entry : stats.getMetrics().entrySet()){
			  reportMetric( entry.getKey(), entry.getValue() );
		  }
		  
		  for ( Map.Entry<String, Double> entry :stats.getGauges().entrySet()){
			  reportDouble( entry.getKey(), entry.getValue() );
		  }
     }

	}
	
	private void reportMetric( String metric, Distribution distribution ){
      for( Map.Entry<String, Number> entry :distribution.toMap().entrySet() ){
    	  StringBuilder str = new StringBuilder(metric);
    	  str.append(".").append(entry.getKey());
    	  reportDouble(str.toString(), entry.getValue().doubleValue() );
      }
	}
	
	private void reportLabel(String label, String value ){
		StringBuilder cmd = newCommand();
		cmd.append(NAME).append(label);
		cmd.append(VALUE).append('"').append(value).append('"');
		cmd.append(STRING);
		
		execute( cmd.toString() );
	}
	
	private void execute(String cmd){
		try{
			Runtime.getRuntime().exec(cmd);
		} catch  ( IOException e ){
			// do nothing because we are nice an unobtrusive
		}
	}
	
	private void reportCounter(String counter, Long value){
		StringBuilder cmd = newCommand();
		cmd.append(NAME).append(counter);
		cmd.append(VALUE).append(value);
		cmd.append(INT32);
		
		execute( cmd.toString() );
	}
	
	private StringBuilder newCommand(){
		return new StringBuilder(gMetric).append(SLOPE_BOTH).append(LIFETIME); //.append(HOST).append(host);
	}
	
	private void reportDouble(String metric, double value){
		double val = Math.round(value * 100) / 100;
		
		StringBuilder cmd = newCommand();
		cmd.append(NAME).append(metric);
		cmd.append(VALUE).append(val);
		cmd.append(DOUBLE);
		
		execute( cmd.toString() );
	}
	
	

}
