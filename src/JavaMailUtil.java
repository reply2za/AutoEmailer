// Java program to send email 

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class JavaMailUtil {


  private static String email;
  private static String pass;

  protected static void setCredentials(String e, String p) {
    email = e;
    pass = p;
  }

  protected static void sendMail(String recipient, String subjectText, String bodyText)
      throws Exception {
    //System.out.println("Sending message...");
    Properties p = new Properties();

    p.put("mail.smtp.auth", "true");
    p.put("mail.smtp.starttls.enable", "true");
    p.put("mail.smtp.host", "smtp.gmail.com");
    p.put("mail.smtp.port", "587");

    Session session = Session.getInstance(p, new Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(email, pass);
      }
    });

    Message message = prepareMessage(session, email, recipient, subjectText, bodyText);

    if (message != null) {
      Transport.send(message);
    }
    //System.out.println("Message sent.");
  }

  private static Message prepareMessage(Session session, String email, String recipient,
      String subjectText, String bodyText) {

    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(email));
      message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
      message.setSubject(subjectText);
      message.setText(bodyText);
      return message;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
