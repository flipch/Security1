/**
 * 
 */
package com.sc.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.sc.utilities.Pair;
import com.sc.utilities.Utils;

/**
 * Catalog for users and smanipulation logic over itself
 * 
 * @author Felipe
 *
 */
public class UserCatalog {

	private ArrayList<User> uc;

	public UserCatalog(String users) {
		this.uc = new ArrayList<User>();
		populate(users);
	}

	/**
	 * Updates user catalog with most current database files
	 * 
	 * @param users
	 *            Database path file
	 * @return updateSucceeded? true : false
	 */
	public boolean populate(String users) {
		try {
			File f = Utils.getFile(users);
			BufferedReader br = new BufferedReader(new FileReader(f));
			br.lines().forEach(s -> {
				String user[] = s.split(":");
				this.uc.add(new User(user[0], user[1]));
			});
			br.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Checks if an equal user object exists in the database and returns it.
	 * 
	 * @param user
	 *            user to be searched
	 * @return found user
	 */
	public User find(User user) {
		for (User u : this.uc) {
			if (u.username.equals(user.username) && u.pw.equals(user.pw))
				return u;
		}
		return null;
	}
	
	/**
	 * Checks if an equal user object exists in the database and returns it.
	 * 
	 * @param username
	 *            user to be searched
	 * @return found user
	 */
	public User find(String username) {
		for (User u : this.uc) {
			if (u.username.equals(username))
				return u;
		}
		return null;
	}

	/**
	 * Checks if given username exists on the database
	 * 
	 * @param user
	 *            username to be checked
	 * @return exists? true : false
	 */
	public boolean exists(String user) {
		for (User u : this.uc) {
			if (u.username.equals(user))
				return true;
		}
		return false;
	}

	/**
	 * Checks if given username and password correspond to an account.
	 * 
	 * @param inUser
	 *            username
	 * @param inPasswd
	 *            password
	 * @return Pair<LoggedIn,MessageOut>
	 */
	public Pair<Boolean, String> authUser(String user, String pwd) {
		User client = new User(user, pwd);

		if (!this.exists(client.username)) {
			this.add(client);
			return new Pair<Boolean, String>(true, "User doesn't exist.\nCreated a new one");
		} else {
			// Either password is wrong or right from this point
			if (find(client) == null) {
				return new Pair<Boolean, String>(false, "Wrong password.");
			} else {
				return new Pair<Boolean, String>(true, "Logged in, welcome " + client.username + ".");
			}
		}
	}

	/**
	 * Adds a client to the db
	 * 
	 * @param client
	 */
	public void add(User client) {
		this.uc.add(client);
		File f = Utils.getFile("Users.txt");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			bw.write(client.username.concat(":").concat(client.pw));
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Pair<Boolean, String> checkFollower(User user, String userCheck) {
		User check = this.find(userCheck);
		if (check != null) {
			// Now check if it contains our user check
			if (check.followers.contains(user.username)) {
				return new Pair<Boolean, String>(true, userCheck + " follows " + user.username);
			} else {
				return new Pair<Boolean, String>(false, userCheck + " doesn't follow " + user.username);
			}
		} else {
			return new Pair<Boolean, String>(false, "User not found");
		}

	}

	public User get(String userCheck) {

		for (User u : this.uc) {
			if (u.username.equals(userCheck))
				return u;
		}
		return null;
	}

	public Pair<Boolean, String> addPhoto(User user, ObjectInputStream clientIn, ObjectOutputStream clientOut) {
		// Find our user in the array
		User test = this.get(user.username);

		if (test != null) {
			return test.addPhoto(clientIn, clientOut);
		}
		return new Pair<Boolean, String>(false, "Erro a adicionar foto");
	}

}
