/**
 * 
 */
package com.abstractlayers.application;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 * @author santoshrangarajan
 *
 * Mar 23, 2013
 */
public class LoggingApp {

    File file;
    Executor executor;
    Logger logger;
    
	public LoggingApp(){
		executor = Executors.newSingleThreadExecutor();	
		logger = Logger.getLogger(LoggingApp.class);
	}
	
	
	private void writeToLogs() throws IOException{
		int count =0;
		while(true){
			try {
			 	  logger.info("line="+(count++));
			 	  if(count%1000 == 0) {
				     Thread.sleep(5000);
			 	  }
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} 
		}
		
	
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		LoggingApp loggingClient = new LoggingApp();
		loggingClient.writeToLogs();
	}

}
