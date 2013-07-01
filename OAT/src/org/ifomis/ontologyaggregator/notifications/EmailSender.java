package org.ifomis.ontologyaggregator.notifications;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * The EmailSender notifies the ontology curators about difficulties with the
 * recommendation generation.
 * 
 * @author Nikolina
 * 
 */
public class EmailSender {

	/**
	 * @param subject
	 *            the subject of the mail to be sent
	 * @param notification
	 *            the content of the mail to be sent
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void sendMail(String subject, String notification) throws FileNotFoundException, IOException {
//TODO find out why it works on the local machine but not on the remote server
//		Properties properties = new Properties();
//
//
//    	properties.load(new FileInputStream("config/aggregator.properties"));
//
//    	String[] curators = properties.getProperty("curatorsMailAddresses").split(";");
//
//    	Email email = new SimpleEmail();
//    	
//    	email.setHostName("smtp.googlemail.com");
//		email.setSmtpPort(465);
//
//		email.setAuthenticator(new DefaultAuthenticator("ontology.aggregator",
//				"solution12"));
//		email.setSSLOnConnect(true);
//
//		try {
//			email.setFrom("ontology.aggregator@gmail.com");
//			email.setSubject(subject);
//			email.setMsg(notification);
//			for (int i = 0; i < curators.length; i++) {
//				System.out.println("email send to " + curators[i]);
//				email.addTo(curators[i]);
//				email.send();
//			}
////			email.addTo("nikolina.koleva19@yahoo.de");
//
//		} catch (EmailException e) {
//			e.printStackTrace();
//		}
	}
	public static void main(String[] args) {
		EmailSender sender = new EmailSender();
		try {
			sender.sendMail("test", "this is a test mail");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
