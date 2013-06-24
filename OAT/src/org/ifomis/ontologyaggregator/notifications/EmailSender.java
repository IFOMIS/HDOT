package org.ifomis.ontologyaggregator.notifications;

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
	 */
	public void sendMail(String subject, String notification) {

		Email email = new SimpleEmail();
		email.setHostName("smtp.googlemail.com");
		email.setSmtpPort(465);
		email.setAuthenticator(new DefaultAuthenticator("ontology.aggregator",
				"solution12"));
		email.setSSLOnConnect(true);
		try {
			email.setFrom("ontology.aggregator@gmail.com");
			email.setSubject(subject);
			email.setMsg(notification);
			email.addTo("nikolina.koleva19@yahoo.de");
			email.send();
		} catch (EmailException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		EmailSender sender = new EmailSender();
		sender.sendMail("test", "this is a test mail");
	}
}
