package com.oanda.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Close stream after timeout if not updated
 */
public class TimeOutListener implements Runnable{

	protected InputStream stream;
	protected long timeout;
	protected ScheduledFuture<?> future;
	protected ScheduledThreadPoolExecutor executor;
	
	
	/**
	 * @param stream Stream for closing
	 * @param timeout in milliseconds
	 */
	public TimeOutListener(InputStream stream, long timeout) {
		this.stream = stream;
		this.timeout = timeout;
		executor = new ScheduledThreadPoolExecutor(1);
		update();
	}
	
	/**
	 *  Closing of stream throw IOException into JavaApiStream for reconnection
	 */
	public void run() {
		try {
			stream.close();
		} catch (IOException e) {
			System.out.println("Socket closed for reconnection");
		}
	}
	
	/**
	 * reset disconnecting timeout
	 */
	public void update() {
		cancel();		
		future = executor.schedule(this, timeout, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * cancel waiting
	 */
	public void cancel() {
		if ( future != null) {
			future.cancel(true);
			
		
		}
	}
	

}
