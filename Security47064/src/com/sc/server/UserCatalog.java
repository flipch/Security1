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
import java.util.ArrayList;

import com.sc.utilities.Pair;
import com.sc.utilities.Utils;

/**
 * Catalog for users and manipulation logic over itself
 * 
 * @author Felipe
 *
 */
public class UserCatalog {

	private ArrayList<User> uc;

	public UserCatalog(String users) {
		populate(users);
	}

	/**
	 * Updates user catalog with most current database files
	 * @param users	Database path file
	 * @return updateSucceeded? true : false
	 */
	public boolean populate(String users) {
		try {
			File f = Utils.getFile(users);
			BufferedReader br = new BufferedReader( new FileReader(f));
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
	 * Checks if an equal user object exists in the database
	 * @param user
	 * @return exists? true : false
	 */
	private boolean find(User user) {
		return this.uc.contains(user);
	}

	/**
	 * Checks if given username exists on the database
	 * @param user username to be checked
	 * @return exists? true : false
	 */
	private boolean exists(String user) {
		for (User u : this.uc) {
			if (u.user == user)
				return true;
		}
		return false;
	}

	/**
	 * Checks if given username and password correspond to an account.
	 * @param inUser username
	 * @param inPasswd password
	 * @return Pair<LoggedIn,MessageOut>
	 */
	public Pair<Boolean, String> authUser(String user, String pwd) {
		User client = new User(user, pwd);

		if (!this.exists(client.user)) {
			this.add(client);
			return new Pair<Boolean, String>(true, "User doesn't exist.\nCreated a new one");
		}
		else {
			// Either password is wrong or right from this point
			if( !find(client) ) {
				return new Pair<Boolean, String>(false, "Wrong password.");
			}else {
				return new Pair<Boolean, String>(true, "Logged in, welcome " + client.user + ".");
			}
		}
	}

	/**
	 * Adds a client to the db
	 * 
	 * @param client
	 */
	private void add(User client) {
		this.uc.add(client);
		File f = Utils.getFile("Users/users.txt");
		try {
			BufferedWriter bw = new BufferedWriter( new FileWriter(f));
			bw.write(client.user.concat(":").concat(client.pw));
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
