package com.sc.server;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;

/**
 * Runnable class for server.
 * 
 * @author Felipe
 *
 */
public class Server {

	/**
	 * Runs a server and starts listening for connections.
	 */
	public static void main(String[] args) {
		// Check if folder for server files exist else create.
		File serverFolder = new File("Server");
		if (!serverFolder.exists()) {
			if (!serverFolder.mkdir())
				// Folder creation failed
				System.err.println("Failed creating server folder!");
			return;
		}

		// Folder created we can start our server.
		try {
			startServer();
		} catch (SocketException se) {
			System.err.println(se.getMessage());
		}
	}

	private static void startServer() throws SocketException {
		// Initialize server with our wanted attributes
		PhotoShare server = new PhotoShare(); // Maybe let arguments decide port next version.

		// Server ready, let's start listening for connections.
		try {
			server.startListening();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
