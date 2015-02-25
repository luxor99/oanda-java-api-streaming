package com.oanda.stream;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class Mailer {
  

    public static final String host = "smtp.gmail.com";
    public static final String senderMail = "";
    public static final String senderPass = "";
    public static final String receiver = "";

    public static void send(String text) {

        //System.out.println("com.oanda.stream.Mailer.send() not implemented");

        //usual example from Internet but may need SSL certificate
        try {
            Email email = new SimpleEmail();
            email.setHostName(host);
            email.setSmtpPort(587);
            email.setAuthenticator(new DefaultAuthenticator(senderMail,
                    senderPass));
            email.setSSLOnConnect(true);
            email.setFrom(senderMail);
            email.setSubject("Notification from JavaApiStream");
            email.setMsg(text);
            email.addTo(receiver);
            email.send();

        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        send("hello");

    }
}
