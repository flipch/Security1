package com.sc.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

import com.sc.utilities.Pair;

/**
 * Class which is gonna run the logic for the initialized socket
 * 
 * @author Felipe
 *
 */
public class ServerThread extends Thread {

	private Socket socket = null;
	private PhotoShare server;

	ServerThread(Socket soc, PhotoShare state) {
		this.socket = soc;
		this.server = state;
		System.out.println("[" + LocalDateTime.now() + "] " + "New client");
	}

	/**
	 * Run client logic for given socket
	 */
	public void run() {
		try {
			// Set response channels
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

			String username = "";
			String pwd = "";

			try {
				// User authentication.
				username = (String) inStream.readObject();
				pwd = (String) inStream.readObject();

				//
				if (auth(username, pwd, outStream)) {
					User localUser = new User(username, pwd);
					while (!this.socket.isClosed()) {
						String operacao = (String) inStream.readObject();
						switch (operacao) {
						case "-a":
							// Add photo from client to server.
							this.server.addPhoto(localUser, inStream, outStream);
							break;
						case "-i":
							// Only checks if given username is a follower
							this.server.checkFollower(localUser, inStream, outStream);
							break;
						case "-l":
							// If localUser follows given user list all photos from given user.
							this.server.listPhotos(localUser, inStream, outStream);
							break;
						case "-g":
							// Send all photos from given user name to client if
							// localUser.follows(givenUser).
							this.server.sendPhotos(localUser, inStream, outStream);
							break;
						case "-f":
							// Adds localUser to given user followers
							this.server.follow(localUser, inStream, outStream);
							break;
						case "-r":
							// Removes localUser from given user followers
							this.server.unfollow(localUser, inStream, outStream);
							break;
						default:
							outStream.writeChars("Operacao invalida");
							break;
						}
					}
				}
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			outStream.close();
			inStream.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Authenticates given username and password. Responds to client with answer.
	 * 
	 * @param inUser
	 *            User to test
	 * @param inPasswd
	 *            Password to test
	 * @param outStream
	 *            Client communication channel for response
	 * @return validUser ? true : false
	 * @throws IOException
	 */
	private boolean auth(String inUser, String inPasswd, ObjectOutputStream outStream) throws IOException {

		Pair<Boolean, String> response = this.server.authUser(inUser, inPasswd);

		System.out.println("[" + LocalDateTime.now() + "] " + "User " + inUser + " logged in ");
		outStream.writeObject(response.first());
		outStream.writeObject(response.second());

		boolean validUser = response.first();
		return validUser;
	}
}