/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 16-Feb-2004
 * Time: 17:37:19
 */
package com.thoughtworks.sise2004;

public interface EmailSender {
    EmailSender NULL_SENDER = new EmailSender() {
        public void sendMail(String emailAddress, String subject) {

        }
    };
    void sendMail(String emailAddress, String subject);
}