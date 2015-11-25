package com.vmg.soap.mo;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class VMGAuthenticator extends Authenticator {

	String user;
	String password;

	public VMGAuthenticator(String user, String password) {
		super();
		this.user = user;
		this.password = password;
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(user, password.toCharArray());
	}

}