package com.abstractlayers.server;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 
 */

/**
 * @author santoshrangarajan
 *
 * Mar 17, 2013
 * 
 */
public class LogServer {

	ServerSocketChannel serverSocketChannel;
	Executor executor;
	private boolean keepRunning;
	LogWatcher logWatcher;
	MessageQueue messageQueue;
	ConnectionWatcher connectionWatcher;
	
	public LogServer(int port, LogWatcher logWatcher, MessageQueue messageQueue,ConnectionWatcher connectionWatcher) throws IOException {
		//socket = new ServerSocket(port);
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(port));
		executor = Executors.newScheduledThreadPool(5);
		keepRunning = true;
		this.logWatcher = logWatcher;
		this.messageQueue = messageQueue;
		this.connectionWatcher = connectionWatcher;
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutdown Hook is running!");
                 stopServer();
            }
        });
		
		System.out.println("Server initialized...");
	}
	
	public void startServer() throws IOException{
		System.out.println("Starting server...");
		executor.execute(logWatcher);
		executor.execute(connectionWatcher);
		while(keepRunning){	
			SocketChannel socketChannel = serverSocketChannel.accept();
			handleConnection(socketChannel);
		}	
	}
	
	public void stopServer(){
		keepRunning = false;
		logWatcher.stop();
		connectionWatcher.stop();
	}
	private void handleConnection(final SocketChannel channel)   {
		
		try{
			System.out.println("Handling request for client. Address ="+channel.socket().getInetAddress()+", port ="+channel.socket().getPort());
			ConnectionHandler connectionHandler = new ConnectionHandler(channel);
			messageQueue.addListener(connectionHandler);
			executor.execute(connectionHandler);
			connectionWatcher.addConnection(channel);
			System.out.println("Handling request for client complete");
		} catch(IOException ioex){
			System.out.println("Exception handling request for client. Address ="+channel.socket().getInetAddress()+", port ="+channel.socket().getPort());
		}
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		try{
			if(args.length<=0 ){
				System.out.println("Please pass complete log file as an argument to main");
				System.exit(-1);
			}
			
			MessageQueue messageQueue = new MessageQueue();
			String logFileName = args[0];
			String linePattern = "[0-9]{4}";
			LogWatcher logWatcher = new LogWatcher(logFileName, linePattern,messageQueue);
			ConnectionWatcher connectionWatcher = new ConnectionWatcher(messageQueue);
			LogServer webServer = new LogServer(8080,logWatcher,messageQueue,connectionWatcher);
			webServer.startServer();
		} catch(IOException ioex){
			System.out.println("Exception Starting server. Msg ="+ioex.getMessage());
		}
	}

}
