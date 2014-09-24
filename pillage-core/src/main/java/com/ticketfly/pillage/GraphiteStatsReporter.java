
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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

public class GraphiteStatsReporter implements StatsReporter {

	private String hostName;
	private InetAddress graphiteAddress;
	private int port;

	public GraphiteStatsReporter(String graphiteHost, int port, String hostName)
			throws UnknownHostException {
		this.graphiteAddress = InetAddress.getByName(graphiteHost);
		this.port = port;
		this.hostName = hostName;
	}

	public GraphiteStatsReporter(String graphiteHost, int port)
			throws UnknownHostException {
		this(graphiteHost, port, InetAddress.getLocalHost().getHostName());
	}

	private void reportMetric(Writer writer, String metric,
			Distribution distribution) throws IOException {
		for (Map.Entry<String, Number> entry : distribution.toMap().entrySet()) {
			StringBuilder str = new StringBuilder(hostName);
			str.append(".");
			str.append(metric);
			str.append(".");
			str.append(entry.getKey());
			str.append(" ");
			str.append(entry.getValue().longValue());
			str.append(" ");
			str.append(timestamp());
			str.append("\n");
			writer.write(str.toString());
		}
	}

	private int timestamp() {
		return (int) (System.currentTimeMillis() / 1000L);
	}

	private void reportLabel(Writer writer, String label, String value)
			throws IOException {
		StringBuilder str = new StringBuilder(hostName);
		str.append(".");
		str.append(label);
		str.append(" ");
		str.append(value);
		str.append(" ");
		str.append(timestamp());
		str.append("\n");
		writer.write(str.toString());
	}

	private void reportNumber(Writer writer, String name, Number value)
			throws IOException {
		StringBuilder str = new StringBuilder(hostName);
		str.append(".");
		str.append(name);
		str.append(" ");
		str.append(value.longValue());
		str.append(" ");
		str.append(timestamp());
		str.append("\n");
		writer.write(str.toString());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * report the stats
	 */
	@Override
	public void report(StatsSummary stats) {
		Writer writer = null;
		try {
			writer = getWriter();
			for (Map.Entry<String, Long> entry : stats.getCounters().entrySet()) {
				reportNumber(writer, entry.getKey(), entry.getValue());
			}

			for (Map.Entry<String, String> entry : stats.getLabels().entrySet()) {
				reportLabel(writer, entry.getKey(), entry.getValue());
			}

			for (Map.Entry<String, Distribution> entry : stats.getMetrics()
					.entrySet()) {
				reportMetric(writer, entry.getKey(), entry.getValue());
			}

			for (Map.Entry<String, Double> entry : stats.getGauges().entrySet()) {
				reportNumber(writer, entry.getKey(), entry.getValue());
			}
			writer.flush();
		} catch (IOException e) {
			// cant do nothin
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Writer getWriter() throws IOException {
		Socket socket = null;
		try {
			socket = new Socket(graphiteAddress, port);
			return new PrintWriter( new BufferedOutputStream( socket.getOutputStream() ), true);
		} catch (IOException e) {
			if (socket != null) {
				socket.close();
			}
			throw e;
		}
	}

}
