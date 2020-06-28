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

  protected static void sendMail(String recipient, String subjectText, String bodyText)
      throws Exception {
    setCredentials();
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

  private static void setCredentials() {
    email = "simple.emailer.d@gmail.com";
    pass = "Zadkuc101!k";
  }
} 

/*
// email ID of Recipient. 
      String recipient = "recipient@gmail.com"; 

      // email ID of  Sender. 
      String sender = "sender@gmail.com"; 

      // using host as localhost 
      String host = "127.0.0.1"; 

      // Getting system properties 
      Properties properties = System.getProperties(); 

      // Setting up mail server 
      properties.setProperty("mail.smtp.host", host); 

      // creating session object to get properties 
      Session session = Session.getDefaultInstance(properties); 

      try 
      { 
         // MimeMessage object. 
         MimeMessage message = new MimeMessage(session); 

         // Set From Field: adding senders email to from field. 
         message.setFrom(new InternetAddress(sender)); 

         // Set To Field: adding recipient's email to from field. 
         message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient)); 

         // Set Subject: subject of the email 
         message.setSubject("This is Suject"); 

         // set body of the email. 
         message.setText("This is a test mail"); 

         // Send email. 
         Transport.send(message); 
         System.out.println("Mail successfully sent"); 
      } 
      catch (MessagingException mex)  
      { 
         mex.printStackTrace(); 
      } 


 */