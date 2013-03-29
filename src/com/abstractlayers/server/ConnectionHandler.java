/**
 * 
 */
package com.abstractlayers.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
//import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * @author santoshrangarajan
 *
 * Mar 24, 2013
 */
public class ConnectionHandler implements MessageListener,Runnable  {

	private BlockingQueue<String> messagesQueue;
	//private SocketChannel connection;
	private volatile boolean keepRunning;
	private String connectionDetails;
	private PrintWriter pw;
	
	
	public ConnectionHandler(SocketChannel connection) throws IOException {
		//this.connection = connection;
		this.messagesQueue = new LinkedBlockingQueue<String>();
		keepRunning = true;
		connectionDetails = connection.socket().getInetAddress()+":"+connection.socket().getPort();
		pw = new PrintWriter(connection.socket().getOutputStream(),true);
		System.out.println("RequestHandler initialized...");
	}
	
	
	//@Override
	public void run() {
		try {
			System.out.println("RequestHandler Thread - "+Thread.currentThread().getName());
			
			//PrintWriter pw = new PrintWriter(connection.socket().getOutputStream(),true);
			while(keepRunning){
					String line = messagesQueue.take();
					if(line.length() > 0) {
						pw.println(line);
					}
			}
			pw.close();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopRequest(){
		keepRunning = false;
	}


	@Override
	public void onMessage(String message)  {
		try {
			messagesQueue.put(message);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return connectionDetails;
	}

}
