package com.cobee.portal.vo;

import java.io.Serializable;

public class Ticket implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1061212991916932393L;

	private String id;
	private User user;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
