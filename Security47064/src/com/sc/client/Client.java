package com.sc.client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.sc.utilities.Pair;

/**
 * @author Felipe
 *
 */
public class Client {

	/**
	 * Run client
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Check if folder for server files exist else create.
		File serverFolder = new File("Client");
		if (!serverFolder.exists()) {
			if (!serverFolder.mkdir())
				// Folder creation failed
				System.err.println("[" + LocalDateTime.now() + "] " + "Failed creating server folder!");
			return;
		}

		// Folder created we can start our server.
		try {
			System.out.println("[" + LocalDateTime.now() + "] " + "Welcome to PhotoShare.");
			startClient(args);
		} catch (SocketException se) {
			System.err.println(se.getMessage());
		}

	}

	private static void startClient(String[] args) throws SocketException {
		try {
			Socket socket;
			Scanner input = new Scanner(System.in);

			// Make connection
			String[] untreatedSv = args[2].split(":");
			String ip = untreatedSv[0];
			int sock = Integer.parseInt(untreatedSv[1]);
			Pair<String, Integer> connectionSettings = new Pair<String, Integer>(ip, sock);
			socket = new Socket(connectionSettings.first(), connectionSettings.second());

			// Log in or exit if wrong password.
			handleLogIn(input, socket, args[0], args[1]);

			socket.close();
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void handleLogIn(Scanner input, Socket socket, String username, String pwd) {
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

			// Send our user and pw.
			out.writeObject(username);
			out.writeObject(pwd);

			// Check our answer.
			Pair<Boolean, String> res = (Pair<Boolean, String>) in.readObject();
			if (res.first()) {
				System.out.println("[" + LocalDateTime.now() + "] " + "Bem vindo " + username);
				String userDir = "Clients/" + username;
				File dir = new File(userDir);
				if (!dir.exists())
					dir.mkdir();

				// All good and logged in lets handle commands
				handleCommands(input, socket, username, pwd, in, out);
			} else {
				System.out.println(res.second());
			}

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void handleCommands(Scanner input, Socket socket, String username, String pwd, ObjectInputStream in,
			ObjectOutputStream out) {
		System.out.println("[" + LocalDateTime.now() + "] " + "Escolha uma operacao:");
		System.out.println("[" + LocalDateTime.now() + "] "
				+ "[ -a <photos> | -l <userId> | -i <userId> <photo> | -g <userId> \n"
				+ "| -c <comment> <userId> <photo> | -L <userId> <photo> | \n -D <userId> <photo> | -f <followUserIds> | -r <followUserIds> ]");
		String op = input.nextLine();
		String[] args = op.split("");
		boolean quit = false;
		while (!quit) {
			switch (args[0]) {
			case "-a":
				sendPhoto(args, out, in);
				break;
			case "-l":
				getList(args, out, in);
				break;
			case "-i":
				isFollower(args, out, in);
				break;
			case "-g":
				getPhotos(args, in, out);
				break;
			case "-f":
				follow(args, in, out);
				break;
			case "-r":
				unfollow(args, out, in);
				break;
			case "-q":
				quit = !quit;
				System.out.println("[" + LocalDateTime.now() + "] " + "Closing");
				break;
			default:
				System.out.println("[" + LocalDateTime.now() + "] " + "Invalid operation");
			}
		}

		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void unfollow(String[] args, ObjectOutputStream out, ObjectInputStream in) {
		// TODO Auto-generated method stub

	}

	private static void follow(String[] args, ObjectInputStream in, ObjectOutputStream out) {
		// TODO Auto-generated method stub

	}

	private static void getPhotos(String[] args, ObjectInputStream in, ObjectOutputStream out) {
		// TODO Auto-generated method stub

	}

	private static void isFollower(String[] args, ObjectOutputStream out, ObjectInputStream in) {
		// TODO Auto-generated method stub

	}

	private static void getList(String[] args, ObjectOutputStream out, ObjectInputStream in) {
		// TODO Auto-generated method stub

	}

	private static void sendPhoto(String[] args, ObjectOutputStream out, ObjectInputStream in) {
		// TODO Auto-generated method stub

	}

}