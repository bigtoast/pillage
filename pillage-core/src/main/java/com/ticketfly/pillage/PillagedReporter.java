package com.ticketfly.pillage;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class PillagedReporter implements StatsReporter {

    InetSocketAddress addy;

    public PillagedReporter( String host, int port ) {
        addy = new InetSocketAddress(host, port);
    }

    @Override
    public void report(StatsSummary stats) {
        byte[] json = toJson(stats).getBytes();
        DatagramChannel channel = null;
        SocketChannel sChannel = null;
        try {
            channel = DatagramChannel.open();
            sChannel = SocketChannel.open();

            channel.configureBlocking(true);
            sChannel.configureBlocking(true);
            //sChannel.connect(addy);
            channel.connect(addy);
            Thread.sleep(5000);
            ByteBuffer buffer = ByteBuffer.allocate(json.length);
            buffer.clear();
            buffer.put(json);
            buffer.flip();
            System.out.println("buffer" + buffer);
            System.out.println(json);
            System.out.println(json.length);
            while(buffer.hasRemaining()){
                channel.write(buffer);
                //sChannel.write(buffer);
            }
            //sChannel.finishConnect();
            Thread.sleep(1000);
            System.out.println("send data.");
        } catch (Exception e ) {
            e.printStackTrace();
        } finally {
            if ( channel != null )
                try {
                    sChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    String toJson(StatsSummary stats) {
        StringBuilder sb = new StringBuilder("{counters:[");

        for( Map.Entry<String,Long> entry : stats.getCounters().entrySet() ) {
            sb.append("{n:\"").append(entry.getKey()).append("\",v:").append(entry.getValue()).append("},");
        }

        if ( stats.getCounters().size() > 0 )
            sb.deleteCharAt(sb.length() - 1);

        sb.append("],metrics:[");

        for ( Map.Entry<String,Distribution> entry : stats.getMetrics().entrySet() ) {
            sb.append("{n:\"").append(entry.getKey()).append("\",v:[");
            Map<String,Number> metrics = entry.getValue().toMap();
            for ( Map.Entry<String,Number> metric : metrics.entrySet() ) {
                sb.append("{").append(metric.getKey()).append(":").append(metric.getValue()).append("},");
            }

            if ( metrics.size() > 0 )
                sb.deleteCharAt(sb.length() - 1);

            sb.append("]},");
        }

        if ( stats.getMetrics().size() > 0 )
            sb.deleteCharAt(sb.length() - 1);

        sb.append("],labels:[");

        for ( Map.Entry<String,String> entry : stats.getLabels().entrySet() ) {
            sb.append("{n:\"").append(entry.getKey()).append("\",v:\"").append(entry.getValue()).append("\"},");

        }

        if ( stats.getLabels().size() > 0 )
            sb.deleteCharAt(sb.length() - 1);

        sb.append("],gauges:[");

        for ( Map.Entry<String,Double> entry : stats.getGauges().entrySet()) {
            sb.append("{n:\"").append(entry.getKey()).append("\",v:").append(entry.getValue()).append("},");
        }

        if ( stats.getGauges().size() > 0 )
            sb.deleteCharAt(sb.length() - 1);

        return sb.append("]}").toString();
    }
}
