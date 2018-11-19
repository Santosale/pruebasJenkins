
package services;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ContactService {

	public void send(final String to, final String subject, final String text) {
		JavaMailSenderImpl mailSender;
		Properties properties;
		MimeMessage message;
		MimeMessageHelper helper;

		mailSender = new JavaMailSenderImpl();

		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);
		mailSender.setUsername("aisscreamcontact@gmail.com");
		mailSender.setPassword("acas41997");
		properties = new Properties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");

		mailSender.setJavaMailProperties(properties);

		try {

			message = mailSender.createMimeMessage();

			helper = new MimeMessageHelper(message);

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(text);
			helper.setFrom("aisscreamcontact@gmail.com");

			mailSender.send(message);
		} catch (final MessagingException e) {
			e.printStackTrace();
		}
	}

}
