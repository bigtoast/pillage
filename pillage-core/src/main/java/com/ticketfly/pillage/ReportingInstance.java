package com.ticketfly.pillage;

/**
 * ReportingInstance.java
 * Define the equivalent of a case class to transport count data for reporting
 */
public class ReportingInstance {
    public long count;
    public ReportingMode mode;
    ReportingInstance(long c, ReportingMode m) {
        count = c;
        mode = m;
    }
}