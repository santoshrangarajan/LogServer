/**
 * 
 */
package com.abstractlayers.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author santoshrangarajan
 *
 * Mar 23, 2013
 */
public class LogWatcher implements Runnable {

	private File logFile;
	boolean keepRunning;
	MessageQueue messageQueue;
	long milliSec;
	private String lineStartPattern;
	int bytesToSkip;
	int msgCount;
	
	public LogWatcher(String fileName, String lineStartPattern, MessageQueue messageQueue)  {
		logFile = new File(fileName);
		keepRunning = true;
		this.messageQueue = messageQueue;
		bytesToSkip = 0;
		msgCount = 0;
		this.lineStartPattern = lineStartPattern;
		System.out.println("LogWatcher initialized...");
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		System.out.println("Running LogWatcher....");
		System.out.println("LogWatcher Thread - "+Thread.currentThread().getName());
		long origSize = 0;
		while(keepRunning) {
			try {
				if(logFile.length() > origSize) {
					addLinesToQueue();
				}
				origSize = logFile.length();
				if((msgCount % 100) == 0){
					Thread.sleep(20000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}	
	}
	
	
	public void stop(){
		keepRunning = false;
	}
	
	private void addLinesToQueue() {
		try{
			RandomAccessFile raf = new RandomAccessFile(logFile, "r");
			raf.skipBytes(bytesToSkip);
			String line="";
			while((line=raf.readLine())!=null) {
				if ( line.length() >=4 && line.substring(0,4).matches(lineStartPattern) ) {
					messageQueue.addToQueue(line);
					msgCount++;
				}
				bytesToSkip = bytesToSkip + line.getBytes().length;
			}
		} catch (FileNotFoundException fnex){
			System.out.println("cannot locate log file");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	

}
