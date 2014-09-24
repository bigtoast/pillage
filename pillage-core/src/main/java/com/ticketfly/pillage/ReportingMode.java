package com.ticketfly.pillage;

/**
 * ReportingMode.java
 * Define differences in the ways counter and metric information can be reported.
 */
public enum ReportingMode {
    DIFFERENTIAL, /* Send diffs of counter from last snapshot, to graphite and the other reporters. */
    INTEGRAL /* don't calculate deltas in triggerCounterSnap, etc, just return whole value to graphite */
}
