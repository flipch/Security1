/**
 * 
 */
package com.sc.server;

import java.time.LocalDateTime;

/**
 * @author Felipe
 *
 */
public class Photo {

	public String photo;
	public LocalDateTime dateCreated;

	public Photo(String photo, String dateTime) {
		this.photo = photo;
		this.dateCreated = LocalDateTime.parse(dateTime);
	}

}
