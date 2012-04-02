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

import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * An implementation of StatsReporter that fires a StatsSummary to 
 * Ganglia over UDP.
 * 
 * This code was largely taken from <a href="http://code.google.com/p/embeddedgmetric/" target="_blank">EmbeddedGMetric</a>
 * 
 * @author andy
 */
public class GangliaStatsReporter implements StatsReporter {
	public final static int SLOPE_ZERO = 0;
	public final static int SLOPE_POSITIVE = 1;
	public final static int SLOPE_NEGATIVE = 2;
	public final static int SLOPE_BOTH = 3;
	public final static int SLOPE_UNSPECIFIED = 4;

	public final static String VALUE_STRING = "string";
	public final static String VALUE_UNSIGNED_SHORT = "uint16";
	public final static String VALUE_SHORT = "int16";
	public final static String VALUE_UNSIGNED_INT = "uint32";
	public final static String VALUE_INT = "int32";
	public final static String VALUE_FLOAT = "float";
	public final static String VALUE_DOUBLE = "double";
	public final static String VALUE_TIMESTAMP = "timestamp";

    private String hostName; 
    private InetAddress gangliaAddress;
    private int port;
	
	public GangliaStatsReporter(String gangliaHost, int port, String hostName ) throws UnknownHostException {
			this.gangliaAddress = InetAddress.getByName(gangliaHost);
			this.port = port;
			this.hostName = hostName;
	}
	
	public GangliaStatsReporter(String gangliaHost, int port) throws UnknownHostException {
		this(gangliaHost, port, InetAddress.getLocalHost().getHostName());
	}
	
	public void send(String name, String value, String type, String units, int slope, int tmax, int dmax){
		send(gangliaAddress, port, hostName, name, value, type, units, slope, tmax, dmax);
	}
	
	public void send(InetAddress address, int port, String host,
			String name, String value, String type, String units, int slope,
			int tmax, int dmax) {
		
		DatagramSocket socket = null;

		try {
			socket = new DatagramSocket();
			byte[] buf = writemeta(host, name, type, units, slope, tmax, dmax);
			DatagramPacket p = new DatagramPacket(buf, buf.length, address,
					port);
			socket.send(p);
			buf = writevalue(host, name, value);
			p = new DatagramPacket(buf, buf.length, address, port);
			socket.send(p);
		} catch (IOException e) {
			// who cares
		} finally {
			socket.close();
		}
	}

	public void send(InetAddress address, int port, String host,
			String name, double dvalue, String type, String units, int slope,
			int tmax, int dmax) {
		String value = Double.toString(dvalue);
		send(address, port, host, name, value, type, units, slope, tmax, dmax);
	}

	public void send(InetAddress address, int port, String host,
			String name, int dvalue, String type, String units, int slope,
			int tmax, int dmax) {
		String value = Integer.toString(dvalue);
		send(address, port, host, name, value, type, units, slope, tmax, dmax);
	}

	/*
	 * EVERYTHING BELOW HERE YOU DON"T NEED TO USE
	 */

	public static byte[] writevalue(String host, String name, String val) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeInt(128 + 5); // string
			writeXDRString(dos, host);
			writeXDRString(dos, name);
			dos.writeInt(0);
			writeXDRString(dos, "%s");
			writeXDRString(dos, val);
			return baos.toByteArray();
		} catch (IOException e) {
			// really this is impossible
			return null;
		}
	}

	public static byte[] writemeta(String host, String name, String type,
			String units, int slope, int tmax, int dmax) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeInt(128); // gmetadata_full
			writeXDRString(dos, host);
			writeXDRString(dos, name);
			dos.writeInt(0);

			writeXDRString(dos, type);
			writeXDRString(dos, name);
			writeXDRString(dos, units);
			dos.writeInt(slope);
			dos.writeInt(tmax);
			dos.writeInt(dmax);
			dos.writeInt(0);

			// to add extra metadata it's something like this
			// assuming extradata is hashmap , then:
			//
			// write extradata.size();
			// foreach key,value in "extradata"
			// writeXDRString(dos, key)
			// writeXDRString(dos, value)

			return baos.toByteArray();
		} catch (IOException e) {
			// really this is impossible
			return null;
		}
	}

	private static void writeXDRString(DataOutputStream dos, String s)
			throws IOException {
		dos.writeInt(s.length());
		dos.writeBytes(s);
		int offset = s.length() % 4;
		if (offset != 0) {
			for (int i = offset; i < 4; ++i) {
				dos.writeByte(0);
			}
		}
		/*
		 * bytes[] b = s.getBytes("utf8"); int len = b.length();
		 * dos.writeInt(len); dos.write(b, 0, len);
		 */
	}

	@Override
	public void report(StatsSummary stats) {
		for(Map.Entry<String,Long> entry :stats.getCounters().entrySet()){
			send(gangliaAddress, port, hostName,
					entry.getKey(), entry.getValue().toString(), VALUE_UNSIGNED_INT, "count", SLOPE_BOTH, 60, 0); // tmax 60, dmax 0
		}
		
		for(Map.Entry<String, Distribution> entry :stats.getMetrics().entrySet()){
			for(Map.Entry<String, Number> distEntry :entry.getValue().toMap().entrySet()){
				StringBuilder str = new StringBuilder();
				str.append(entry.getKey());
				str.append("[");
				str.append(distEntry.getKey());
				str.append("]");
				send(gangliaAddress, port, hostName,
					str.toString(), distEntry.getValue().toString(), VALUE_DOUBLE, "dist", SLOPE_BOTH, 60, 0); 
			}
		}
	}

}
