/**
 * 
 */
package com.sc.utilities;

import java.io.File;
import java.io.IOException;

/**
 * @author Felipe
 *
 */
public class Utils {

	/**
	 * Gets file in said path and creates if doesn't exist.
	 * 
	 * @param p
	 *            path of file
	 * @return File object
	 */
	public static File getFile(String p) {
		File f = new File(p);
		if (!(f.exists()))
			try {
				f.createNewFile();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}

		return f;
	}
}
