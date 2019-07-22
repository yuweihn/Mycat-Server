package io.mycat.backend;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ConQueue {
	private final ConcurrentLinkedQueue<BackendConnection> autoCommitCons = new ConcurrentLinkedQueue<BackendConnection>();
	private final ConcurrentLinkedQueue<BackendConnection> manCommitCons = new ConcurrentLinkedQueue<BackendConnection>();
	private long executeCount;

	public BackendConnection takeIdleCon(boolean autoCommit) {
		ConcurrentLinkedQueue<BackendConnection> f1 = autoCommitCons;
		ConcurrentLinkedQueue<BackendConnection> f2 = manCommitCons;

		if (!autoCommit) {
			f1 = manCommitCons;
			f2 = autoCommitCons;
		}
		BackendConnection con = f1.poll();

		while (con  != null && !con.checkAlive()){
			con.close("channel is closed");
			con = f1.poll();
		}

		if (con == null || con.isClosedOrQuit()) {
			con = f2.poll();
		}
		if (con == null || con.isClosedOrQuit()) {
			return null;
		} else {
			return con;
		}
	}

	public long getExecuteCount() {
		return executeCount;
	}

	public void incExecuteCount() {
		this.executeCount++;
	}

	public boolean removeCon(BackendConnection con) {
		return autoCommitCons.remove(con) || manCommitCons.remove(con);
	}

	public boolean isSameCon(BackendConnection con) {
		return autoCommitCons.contains(con) || manCommitCons.contains(con);
	}

	public ConcurrentLinkedQueue<BackendConnection> getAutoCommitCons() {
		return autoCommitCons;
	}

	public ConcurrentLinkedQueue<BackendConnection> getManCommitCons() {
		return manCommitCons;
	}

	public List<BackendConnection> getIdleConsToClose(int count) {
		List<BackendConnection> readyCloseCons = new ArrayList<BackendConnection>(count);
		while (!manCommitCons.isEmpty() && readyCloseCons.size() < count) {
			BackendConnection theCon = manCommitCons.poll();
			if (theCon != null && !theCon.isBorrowed()) {
				readyCloseCons.add(theCon);
			}
		}
		while (!autoCommitCons.isEmpty() && readyCloseCons.size() < count) {
			BackendConnection theCon = autoCommitCons.poll();
			if (theCon != null && !theCon.isBorrowed()) {
				readyCloseCons.add(theCon);
			}
		}
		return readyCloseCons;
	}
}
