package mm.locationtracker.utility;

import android.content.Context;

import java.io.UnsupportedEncodingException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

import mm.locationtracker.mailer.GmailSMTPMailer;

/**
 * Created by Pradeep Mahato 007 on 08-May-16.
 */
public class SendMailInvoker {

    Context context;

    public SendMailInvoker(Context context) {
        this.context = context;
    }

    public void sendMail() {
        DumpKMLFileInvoker dumpKMLFileInvoker = new DumpKMLFileInvoker(context);
        String filePath = dumpKMLFileInvoker.dumpTheFile();
        if (!filePath.isEmpty()) {
            GmailSMTPMailer gmailSMTPMailer = new GmailSMTPMailer("mmdevelopers9092@gmail.com", "whiteboard", context);
            Session session = gmailSMTPMailer.createSessionObject();
            try {
                String timeStamp = CustomDate.getCurrentFormattedDate();
                Message message = gmailSMTPMailer.createMessage("mmdevelopers9092@gmail.com", "History till " + timeStamp, "Location history till " + timeStamp, filePath, session);
                gmailSMTPMailer.sendTheMail(message, context);
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

}