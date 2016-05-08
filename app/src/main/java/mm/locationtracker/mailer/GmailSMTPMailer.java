package mm.locationtracker.mailer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.WindowManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by Pradeep Mahato 007 on 08-May-16.
 */
public class GmailSMTPMailer {

    String username, password;
    Context context;

    public GmailSMTPMailer(String username, String password, Context context) {
        this.username = username;
        this.password = password;
        this.context = context;
    }

    public Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public Message createMessage(String email, String subject, String messageBody, String filePath, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("mmdevelopers9092@gmail.com", "MM test1"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setSubject(subject);
        message.setText(messageBody);

        Multipart mp = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(messageBody, "text/html");
        mp.addBodyPart(htmlPart);

        //Attach the file
        if (filePath != null && !filePath.isEmpty()) {
            File kmlFile = new File(filePath);
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            FileDataSource fileDataSource = new FileDataSource(kmlFile);
            messageBodyPart.setDataHandler(new DataHandler(fileDataSource));
            messageBodyPart.setFileName(kmlFile.getName());
            mp.addBodyPart(messageBodyPart);

            message.setContent(mp);
        }

        return message;
    }

    public void sendTheMail(Message message, Context context) {
        new SendMailTask(context).execute(message);
    }

    public class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;
        Context context;

        public SendMailTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                progressDialog = ProgressDialog.show(context, "Please wait", "Sending mail", true, false);
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
