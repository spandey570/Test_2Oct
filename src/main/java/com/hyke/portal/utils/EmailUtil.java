package com.hyke.portal.utils;

import com.hyke.portal.utils.BaseUtil;
import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.util.Calendar;
import java.util.Properties;

public class EmailUtil {

    public final static Logger log = Logger.getLogger(EmailUtil.class);
    private String environment;

    public EmailUtil() {
        this.environment = BaseUtil.environment;
    }


    public Properties getMailProp() {
        log.info("Setting properties file to pick properties for the email config");
        Properties properties = new Properties();

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
        return properties;
    }

    public Folder connectEmailInbox(Store store) throws MessagingException {
        String host = "smtp.gmail.com";// change accordingly
        Properties config = BaseUtil.initPropFromFile("/src/main/resources/config.properties");

        store.connect(host, config.getProperty("EmailUsername"),
                config.getProperty("EmailPassword"));
        log.info("create the folder object and open the inbox");
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);
        return inbox;
    }

    public String getMessageBody(Message message) {
        try {
            if (message.isMimeType("text/plain")) {
                return message.getContent().toString();
            } else if (message.isMimeType("multipart/*")) {
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                return getMimeMultipartContent(mimeMultipart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getMimeMultipartContent(MimeMultipart mimeMultipart) throws Exception {
        String body = "";
        int count = mimeMultipart.getCount();

        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                body = body + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                body = body + "\n" + org.jsoup.Jsoup.parse(html);
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                body = body + getMimeMultipartContent((MimeMultipart) bodyPart.getContent());
            }
        }
        return body;
    }

    private String getMailContentsUsingTimeout(String orderId) {
        int polling = 1;

        try {
            log.info("Starting email session with given parameters");
            Session emailSession = Session.getDefaultInstance(getMailProp(), null);
            log.info("create the store object and connect with the server");
            Store store = emailSession.getStore("imaps");
            //connect to mailbox and fetch the inbox
            Folder inbox = connectEmailInbox(store);

            //each polling counter takes around 4sec to complete traverse of recent 10 mails
            while (polling < 500) {
                log.info("Retrieve the messages from the folder in an array. Polling " + polling);
                Message[] mailList = inbox.getMessages();

                for (int i = mailList.length - 1; i > mailList.length - 100; i--) {
                    Message message = mailList[i];
                    if (message.getSubject().contains(environment.toLowerCase()) && message.getSubject().contains(orderId) &&
                            message.getReceivedDate().toString().contains(Calendar.getInstance().getTime().toString().substring(0, 10)) && !message.getSubject().toString().contains("Submitted and In Process") && !message.getSubject().toString().contains("Westcon Services")) {

                        log.info("Got email successfully. " + message.getSubject());
                        System.out.println("Subject: " + message.getSubject());
                        if(message.getSubject().contains("automatically")){
                            return "auto approved orders";
                        }
                        
                        return getMessageBody(message);
                    }
                }
                Thread.sleep(2000);
                polling += 1;
            }
            log.info("close the store and folder objects");
            inbox.close(true);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("No email found, throwing exception: " + e.getMessage());
        }
        return null;
    }


    public String getLinkFromEmail(String orderId) {
        String msgBody = getMailContentsUsingTimeout(orderId);
        System.out.println("Email message body: " + msgBody);
        if(msgBody.equals("auto approved orders")){
            return "auto approved orders";
            
        }
        if (msgBody != null) {
            try {
                return org.jsoup.Jsoup.parse(msgBody).select("a").first().attr("href");
            } catch (NullPointerException npe) {
                try {
                    return org.jsoup.Jsoup.parse(msgBody).getElementsByAttributeValue("style", "margin-bottom:20px").first().text();
                } catch (NullPointerException np) {
                    return org.jsoup.Jsoup.parse(msgBody).select("p").first().text();
                }
            }
        } else {
            return "No Email Found Yet";
        }
    }

    // Method For Reset Password Email.
    private String checkPasswordEmail(String value, String user) {
        String msgbody = "";

        try {
            log.info("Starting email session with given parameters");
            Session emailSession = Session.getDefaultInstance(getMailProp(), null);
            log.info("create the store object and connect with the server");
            Store store = emailSession.getStore("imaps");
            //connect to mailbox and fetch the inbox
            Folder inbox = connectEmailInbox(store);
            log.info("retrieve the messages from the folder in an array");
            Message[] mailList = inbox.getMessages();

            for (int i = mailList.length - 1; i > mailList.length - 100; i--) {
                Message message = mailList[i];
                Message[] messages1 = inbox.getMessages();

                if (mailList.length == messages1.length) {
                    Flags flags = message.getFlags();
                    if (message.getReceivedDate().toString().contains(Calendar.getInstance().getTime().toString().substring(0, 10)) &&
                            getMessageBody(message).contains(value) && getMessageBody(message).contains(user)) {

                        System.out.println("Subject: " + message.getSubject());
                        System.out.println("Email is opened for " + user);
                        msgbody = getMessageBody(message);
                        break;
                    }
                    Thread.sleep(2000);
                } else {
                    mailList = messages1;
                    i = mailList.length;
                }
            }
            return msgbody;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPasswordLink(String value, String user) {
        String body, link;
        try {
            body = checkPasswordEmail(value, user);
            if (body != null) {
                link = org.jsoup.Jsoup.parse(body).select("a").first().attr("href");
                System.out.println("Link is " + link);
                return link;
            } else {
                return "No Email Found Yet";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "No Link found";
        }
    }

    public boolean checkOrderTypeStatusMails(String orderId, String text, String orderType) {
        int polling = 1;

        try {
            log.info("Starting email session with given parameters");
            Session emailSession = Session.getDefaultInstance(getMailProp(), null);
            log.info("create the store object and connect with the server");
            Store store = emailSession.getStore("imaps");
            //connect to mailbox and fetch the inbox
            Folder inbox = connectEmailInbox(store);

            while (polling < 500) {
                log.info("Retrieve the messages from the folder in an array. Polling " + polling);
                Message[] mailList = inbox.getMessages();

                for (int i = mailList.length - 1; i > mailList.length - 100; i--) {
                    Message message = mailList[i];
                    if (message.getReceivedDate().toString().contains(Calendar.getInstance().getTime().toString().substring(0, 10)) &&
                            message.getSubject().contains(orderId) && message.getSubject().contains(text) && message.getSubject().contains(orderType)) {

                        log.info("Got email successfully. " + message.getSubject());
                        System.out.println("Subject: " + message.getSubject());
                        return true;
                    }
                }
                Thread.sleep(2000);
                polling += 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("No email found, throwing exception: " + e.getMessage());
        }
        return false;
    }
}