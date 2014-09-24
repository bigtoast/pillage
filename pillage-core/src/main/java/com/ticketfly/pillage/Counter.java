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

import java.util.concurrent.atomic.AtomicLong;

/**
 * This is a simple class to count stuff. It wraps an AtomicLong
 *
 * @author ATD
 */
public class Counter {

    private AtomicLong counter = new AtomicLong(0L);

    private ReportingMode reportingMode = ReportingMode.DIFFERENTIAL;

    public long incr(){  return counter.incrementAndGet(); }
    public long incr( int i ){ return counter.addAndGet(i); }
    public long value(){ return counter.get(); }
    public void update(long l){ counter.set(l); }
    public void reset() { counter.set(0); }
    public void setReportingMode(ReportingMode mode) { reportingMode = mode; }
    public ReportingMode getReportingMode() { return reportingMode; }

    @Override
    public String toString() { return "Counter[" + counter.get() + "]"; }
    
    public Counter(ReportingMode mode) {
        reportingMode = mode;
    }
}
