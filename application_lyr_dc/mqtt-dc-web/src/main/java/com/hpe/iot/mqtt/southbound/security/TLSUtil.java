package com.hpe.iot.mqtt.southbound.security;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TLSUtil {

	private static final Logger logger = LoggerFactory.getLogger(TLSUtil.class);

	public static SSLSocketFactory getDynamicSocketFactory(final String caCrtFile, final String crtFile,
			final String keyFile, final String password) throws Exception {
		Security.addProvider(new BouncyCastleProvider());

		// load CA certificate
		PEMReader reader = new PEMReader(
				new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(caCrtFile)))));
		X509Certificate caCert = (X509Certificate) reader.readObject();
		reader.close();

		// load client certificate
		reader = new PEMReader(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(crtFile)))));
		X509Certificate cert = (X509Certificate) reader.readObject();
		reader.close();

		// load client private key
		reader = new PEMReader(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(keyFile)))),
				new PasswordFinder() {
					@Override
					public char[] getPassword() {
						return password.toCharArray();
					}
				});
		KeyPair key = (KeyPair) reader.readObject();
		reader.close();

		// CA certificate is used to authenticate server
		KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
		caKs.load(null, null);
		caKs.setCertificateEntry("ca-certificate", caCert);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(caKs);

		// client key and certificates are sent to server so it can authenticate
		// us
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, null);
		ks.setCertificateEntry("certificate", cert);
		ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(),
				new java.security.cert.Certificate[] { cert });
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, password.toCharArray());

		// finally, create SSL socket factory
		SSLContext context = SSLContext.getInstance("TLSv1");
		context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		return context.getSocketFactory();
	}

	public static SSLSocketFactory getStaticSocketFactory(final String trustStorePath, final String trustStorePassword,
			final String keyStorePath, final String keyStorePassword) throws Exception {
		logger.debug("TrustStore path used for connecting to Mqtt server is " + trustStorePath);
		logger.debug("KeyStore path used for connecting to Mqtt server is " + keyStorePath);
		KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
		try (InputStream trustStoreStream = new java.io.FileInputStream(trustStorePath)) {
			caKs.load(trustStoreStream, trustStorePassword.toCharArray());
		}
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(caKs);
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		try (InputStream keyStoreStream = new java.io.FileInputStream(keyStorePath)) {
			ks.load(keyStoreStream, keyStorePassword.toCharArray());
		}
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, keyStorePassword.toCharArray());
		SSLContext context = SSLContext.getInstance("TLSv1.2");
		context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
		return context.getSocketFactory();
	}

}