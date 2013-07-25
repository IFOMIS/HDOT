package org.ifomis.ontologyaggregator.notifications;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.ifomis.ontologyaggregator.util.Configuration;

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
	 * @throws EmailException 
	 */
	public void sendMail(String subject, String notification)
			throws FileNotFoundException, IOException, EmailException {
		
		 Email email = new SimpleEmail();
		 email.setHostName(Configuration.SMTP_HOST);
		 email.setSmtpPort(Configuration.SMTP_PORT);
		
		 email.setAuthenticator(new
		 DefaultAuthenticator(Configuration.SMTP_USERNAME,
		 Configuration.SMTP_PASS));
		 email.setSSLOnConnect(true);
		
		 email.setFrom("ontology.aggregator@gmail.com");
		 email.setSubject(subject);
		 email.setMsg(notification);
		 for (int i = 0; i < Configuration.CURATORS.length; i++) {
		 System.out.println("email send to " + Configuration.CURATORS[i]);
		 email.addTo(Configuration.CURATORS[i]);
		 email.send();
		 }
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, EmailException {
		EmailSender sender = new EmailSender();

		sender.sendMail("test", "this is a test mail");

	}
}
