/**
 * 
 */
package com.abstractlayers.server;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author santoshrangarajan
 *
 * Mar 25, 2013
 */
public class ConnectionWatcher implements Runnable {

	BlockingQueue<SocketChannel> connectionQueue;
	boolean keepRunning;
	MessageQueue messageQueue;
	//ByteBuffer buffer;


	public ConnectionWatcher(MessageQueue messageQueue){
		connectionQueue = new LinkedBlockingQueue<SocketChannel>();
		this.messageQueue = messageQueue;
		keepRunning = true;
		
	}


	public void addConnection(SocketChannel socketChannel) {
		try {
			connectionQueue.put(socketChannel);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while(keepRunning){
			System.out.println("Running ConnectionWatcher...");
			for(SocketChannel socketChannel:connectionQueue){
				if(!isConnectionOpen(socketChannel)){
					System.out.println("Removing ="+socketChannel.socket().getInetAddress()+": "+socketChannel.socket().getPort());
					connectionQueue.remove(socketChannel);
					messageQueue.removeListener(socketChannel.socket().getInetAddress()+":"+socketChannel.socket().getPort());
				} else {
					System.out.println("Connection ="+socketChannel.socket().getInetAddress()+": "+socketChannel.socket().getPort()+" is open.");
				}
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}

	public void stop(){
		keepRunning = false;
	}

	private boolean isConnectionOpen (SocketChannel connection){
		try {
			ByteBuffer buffer = ByteBuffer.allocate(24);
			int val = connection.read(buffer);
			System.out.println("Is connection Open , Val ="+val);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
