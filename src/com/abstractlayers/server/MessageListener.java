/**
 * 
 */
package com.abstractlayers.server;

/**
 * @author santoshrangarajan
 *
 * Mar 24, 2013
 */
public interface MessageListener {
	
	public void onMessage(String message) /*throws Exception*/;
	public String getName();
}
