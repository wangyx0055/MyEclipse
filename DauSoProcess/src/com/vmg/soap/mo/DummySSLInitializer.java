package com.vmg.soap.mo;

import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;

import java.security.SecureRandom;
import java.security.cert.CertificateException;

public class DummySSLInitializer {

	public static void initDummySsl() {
		final javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[] { new javax.net.ssl.X509TrustManager() {
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] x509Certificates,
					String string) throws CertificateException {

			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] x509Certificates,
					String string) throws CertificateException {

			}

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

		} };

		try {

			final javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
					.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
			javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
					.getSocketFactory());
			javax.net.ssl.HttpsURLConnection
					.setDefaultHostnameVerifier(new DummyHostnameVerifier());

			com.sun.net.ssl.SSLContext sunSSLcontext = SSLContext
					.getInstance("TLS");
			sunSSLcontext.init(null,
					new TrustManager[] { new DummyTrustManager() },
					new SecureRandom());
			com.sun.net.ssl.HttpsURLConnection
					.setDefaultSSLSocketFactory(sunSSLcontext
							.getSocketFactory());
			com.sun.net.ssl.HttpsURLConnection
					.setDefaultHostnameVerifier(new DummyHostnameVerifier());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
