package upsimulator.speedup;

import java.util.Calendar;

/**
 * Worker is used to speed up simulation by using Multi-thread technology
 * 
 * @author quan
 *
 */
public abstract class Worker extends Thread {

	private static int idGen = 1;

	private int id;
	private boolean finished = false;

	private static Integer count = 0;

	private Calendar start, end;

	public Worker() {
		super();
		addCount();
		finished = false;
		this.id = idGen;
		idGen++;
	}

	public int getWorkerId() {
		return id;
	}

	@Override
	public final void run() {
		start = Calendar.getInstance();
		doWork();
		end = Calendar.getInstance();
		finished = true;
	}

	public abstract void doWork();

	public boolean finished() {
		return finished;
	}

	public long getTimeSpend() {
		return end.getTimeInMillis() - start.getTimeInMillis();
	}

	private void addCount() {
		synchronized (count) {
			count++;
		}
	}

	private void minusCount() {
		synchronized (count) {
			count--;
		}
	}
}
