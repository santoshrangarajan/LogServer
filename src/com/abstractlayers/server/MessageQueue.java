/**
 * 
 */
package com.abstractlayers.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author santoshrangarajan
 *
 * Mar 27, 2013
 */
public class MessageQueue {
    
	
	private ConcurrentHashMap <String, MessageListener> listeners;
	private BlockingQueue<String> msgQueue;

	
	public MessageQueue() {
		msgQueue = new LinkedBlockingQueue<String>();
		listeners = new ConcurrentHashMap<String, MessageListener>();
	}
	
	public void addToQueue(String msg)  {
		if (listeners.size() > 0) {
			try {
				msgQueue.put(msg);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	   
	   if(msgQueue.size() ==100){
			System.out.println("notifying listeners start");
			notifyListeners();
			System.out.println("notifying listeners end");
		} 
	}
	
	public void addListener(MessageListener messageListener){
		System.out.println("Adding listener ="+ messageListener.getName());
		listeners.put(messageListener.getName(),messageListener);
		System.out.println("Total listeners ="+ listeners.size());
	}
	
	public void removeListener(String listenerName){
		System.out.println("Removing listener ="+ listenerName);
		listeners.remove(listenerName);
		System.out.println("Total listeners ="+ listeners.size());
	}
	
	
	private void notifyListeners()  {
	   for(int count=0;count<100;count++){
	   String msg = msgQueue.remove();
		for(MessageListener messageListener : listeners.values()){
			messageListener.onMessage(msg);
		}
	   }
	   } 
	}
	
