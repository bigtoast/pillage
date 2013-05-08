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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/** Wrap a stats reporter and filter stats so as to just report the ones you want.
 * The filter is by default "filter in" which means that stats matching the given
 * pattern will be passed through the filter and those which do not match will be
 * dropped.
 * */
public class FilteringStatsReporter implements StatsReporter {

    private StatsReporter reporter;

    private List<Pattern> patterns = new ArrayList<Pattern>();

    private volatile boolean isFilterOut = false;

    public FilteringStatsReporter( StatsReporter reporter, Pattern pattern ) {
        this.reporter = reporter;
        patterns.add(pattern);
    }

    public FilteringStatsReporter( StatsReporter reporter, List<Pattern> patterns ) {
        this.reporter = reporter;
        this.patterns.addAll(patterns);
    }

    public void report(StatsSummary stats) {
        StatsSummary updated = stats;

        if ( isFilterOut ) {
            for( Pattern pattern : patterns ) {
                updated = updated.filterOut(pattern);
            }
        } else {
            for( Pattern pattern : patterns ) {
                updated = updated.filter(pattern);
            }
        }

        if ( updated != null )
            reporter.report(updated);
    }

    public boolean isFilterOut() { return isFilterOut; }

    public void setFilterOut( boolean isFilterOut ){
        this.isFilterOut = isFilterOut;
    }

}
