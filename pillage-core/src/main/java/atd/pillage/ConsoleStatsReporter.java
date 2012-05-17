package atd.pillage;

public class ConsoleStatsReporter implements StatsReporter {
    @Override
    public void report(StatsSummary stats) {
        System.out.println(stats);
    }
}
