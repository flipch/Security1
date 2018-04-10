/**
 * 
 */
package com.sc.server;

import java.io.File;
import java.util.ArrayList;

import com.sc.utilities.Pair;

/**
 * Catalog for users and manipulation logic over itself
 * 
 * @author Felipe
 *
 */
public class UserCatalog {

	private ArrayList<User> uc;

	public UserCatalog(File users) {
		populate(users);
	}

	/**
	 * Updates user catalog with most current database files
	 * @param users	Database path file
	 * @return updateSucceeded? true : false
	 */
	public boolean populate(File users) {
		// TODO Auto-generated method stub
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
	public Pair<Boolean, String> authUser(String inUser, String inPasswd) {
		User client = new User(inUser, inPasswd);

		if (!this.exists(inUser)) {
			return new Pair<Boolean, String>(false, "User doesn't exist.");
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

}
