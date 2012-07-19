package atd.pillage;

public class AsyncMetric implements Metric {

	private AsyncStatsContainer container;
	private String name;
	
	public AsyncMetric( String name, AsyncStatsContainer container ){
		this.name = name;
		this.container = container;	
	}
	
	@Override
	public void clear() {
		container.clearMetric(name);
	}

	@Override
	public long add(int i) {
		container.add(name, i);
		return -1L;
	}

	@Override
	public long add(Distribution d) {
		container.add(name, d);
		return -1L;
	}

	@Override
	public Distribution getDistribution() {
		return null;
	}

}
