package soap;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class ICOMAuthenticator extends Authenticator {

	String user;
	String password;

	public ICOMAuthenticator(String user, String password) {
		super();
		this.user = user;
		this.password = password;
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(user, password.toCharArray());
	}

}