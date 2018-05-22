package io.mycat.net;



public class WriteEventCheckRunner implements Runnable {
	private final SocketWR socketWR;
	private volatile boolean finished = true;

	public WriteEventCheckRunner(SocketWR socketWR) {
		this.socketWR = socketWR;
	}

	public boolean isFinished() {
		return finished;
	}

	@Override
	public void run() {
		finished = false;
		socketWR.doNextWriteCheck();
		finished = true;
	}
}