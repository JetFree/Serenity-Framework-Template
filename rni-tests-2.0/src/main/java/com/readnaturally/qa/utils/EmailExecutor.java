package com.readnaturally.qa.utils;

import com.sun.mail.imap.IMAPMessage;
import net.serenitybdd.core.environment.WebDriverConfiguredEnvironment;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.steps.ScenarioSteps;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Evgeny.Gurinovich on 11/13/2018.
 */
public class EmailExecutor extends ScenarioSteps {

    int attemptIterator = 0;
    private static Logger logger = LoggerFactory.getLogger(EmailExecutor.class);
    private static EmailExecutor emailConnector;
    private static String host;
    private static String email;
    private static String password;
    private static String protocol;
    private Session session;
    private Store store;
    private Folder folder;

    private EmailExecutor(String host, String email, String password, String protocol) {
        this.host = host;
        this.email = email;
        this.password = password;
        this.protocol = protocol;
    }

    public static EmailExecutor getInstance() {
        PropertiesLoader propertiesLoader = new PropertiesLoader("serenity.properties");
        if (emailConnector == null) {
            emailConnector = new EmailExecutor(
                    propertiesLoader.getProperty("rni.email.host"),
                    propertiesLoader.getProperty("rni.email.batch" +
                            ThucydidesSystemProperty.SERENITY_BATCH_NUMBER.integerFrom(
                                    WebDriverConfiguredEnvironment.getDriverConfiguration()
                                            .getEnvironmentVariables(), 1)),
                    propertiesLoader.getProperty("rni.email.password"),
                    propertiesLoader.getProperty("rni.email.protocol")
            );
        }
        return emailConnector;
    }

    @Step
    public EmailExecutor connect() {
        logger.info(String.format("Connecting to email box: %s:%s using user: %s:%s", protocol, host, email, password));
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", protocol);
        properties.setProperty("mail.imaps.ssl.trust", host);
        properties.setProperty("mail.imaps.timeout", "20000");
        properties.setProperty("mail.imaps.connectiontimeout", "10000");
        this.session = Session.getDefaultInstance(properties, null);
        try {
            if (store == null) {
                logger.info("store is null");
                this.store = this.session.getStore();
                store.connect(host, 993, email, password);
                logger.info("store.connect is successful");
            } else if (!store.isConnected()) {
                logger.info("store is not null, but store isn't connected");
                store.connect(host, email, password);
            }
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            return null;
        } catch (MessagingException e) {
            logger.error(e.toString());
            e.printStackTrace();
            attemptIterator++;
            if (attemptIterator < 3) {
                logger.info("Reconnecting... Attempt: " + attemptIterator);
                connect();
            } else {
                logger.info("Max attempts tried. Returning null.");
                return null;
            }
        }
        logger.info("Store is already connected");
        attemptIterator = 0;
        return this;
    }

    private void refreshSession() {
        logger.info("Refreshing connection ...");
        try {
            logger.info("Checking if store is connected");
            if (store.isConnected()) {
                store.close();
            }
            logger.info("Trying to get store");
            this.store = this.session.getStore();
            logger.info("Trying to connect");
            store.connect(host, 993, email, password);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
            refreshSession(attemptIterator++);
        }
    }

    private void refreshSession(int i) {
        if (i < 3) {
            refreshSession();
        }
    }

    public void closeSession() throws MessagingException {
        if (this.store != null) {
            this.store.close();
        }
    }

    public EmailExecutor openFolder(String folderName) throws MessagingException {
        logger.info("Opening folder with name: " + folderName);
        this.folder = store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);
        return this;
    }

    @Step
    public EmailExecutor openFolder(String folderName, int mode) throws MessagingException {
        logger.info("Opening folder with name: " + folderName);
        this.folder = store.getFolder(folderName);
        folder.open(mode);
        return this;
    }

    @StepGroup
    public Message[] getAllMessagesFrom(String folderName) {
        Message[] messages = null;
        try {
            this.folder = store.getFolder(folderName);
            folder.open(Folder.READ_WRITE);
            messages = folder.getMessages();
            Message lastMessage = folder.getMessage(folder.getMessageCount());
            ((IMAPMessage) lastMessage).setPeek(true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        logger.info("Amount of found messages: " + messages.length);
        return messages;
    }

    @Step
    public Message[] getAllUnreadMessagesFrom() throws FolderNotFoundException {
        logger.info("Trying to retrieve unread messages");
        Message[] unreadMessages = new Message[0];
        if (this.folder == null) {
            throw new FolderNotFoundException("Attempt to interact with messages without specifying a folder", folder);
        }
        try {
            unreadMessages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            if (unreadMessages.length > 0) {
                return unreadMessages;
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return unreadMessages;
    }

    @Step
    public Message getLastUnreadMessage() throws FolderNotFoundException {
        logger.info("Trying to retrieve last unread message");
        if (this.folder == null) {
            throw new FolderNotFoundException("Attempt to interact with messages without specifying a folder", folder);
        }
        Message message = null;
        try {
            Message[] undreadMessages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            if (undreadMessages.length > 0) {
                message = undreadMessages[undreadMessages.length - 1];
                this.markMessageAsSeen(message);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return message;
    }

    // copy-paste from http://www.oracle.com/technetwork/java/javamail/faq/index.html#mainbody
    @Step
    public String getText(Part p) throws
            MessagingException, IOException {
        if (p == null) throw new RuntimeException("Message can't be null");
        boolean textIsHtml = false;
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            textIsHtml = p.isMimeType("text/html");
            return s;
        }
        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }
        return null;
    }

    @Step
    public EmailExecutor markMessageAsSeen(Message m) throws MessagingException {
        logger.info("Doing message " + m.getSubject() + " as seen");
        if (this.folder == null) {
            throw new FolderNotFoundException("Attempt to interact with messages without specifying a folder", folder);
        }
        folder.setFlags(new Message[] {m}, new Flags(Flags.Flag.SEEN), true);
        return this;
    }

    @StepGroup
    public EmailExecutor markAllMessagesAsSeen() {
        try {
            Message[] unreadMsgs = this.getAllUnreadMessagesFrom();
            for (Message msg : unreadMsgs) {
                this.markMessageAsSeen(msg);
            }
        } catch (FolderNotFoundException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return this;
    }

        /**
     * Wait until at least 1 message in email box will be unread
     * @param sec - amount of seconds
     * @return null if time is over but there are no unread messages appeared
     */
    @StepGroup
    public Message waitUntilAnyNewMessageReceived(long sec, String folderName) throws TimeoutException {
        logger.info("Waiting for unread messages ...");
        Message msg = null;
        try {
            msg = getLastUnreadMessage();
            if (null == msg)  {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                while (stopWatch.getTime(TimeUnit.SECONDS) < sec && null == msg) {
                    TimeUnit.SECONDS.sleep(3);
                    this.refreshSession();
                    this.openFolder(folderName, Folder.READ_WRITE);
                    msg = this.getLastUnreadMessage();
                }
                stopWatch.stop();
                logger.info("Waited time: " + stopWatch.getTime(TimeUnit.SECONDS) + " seconds.");
                if (msg == null) throw new TimeoutException("Time is up, no message received!");
            }
        } catch (FolderNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return msg;
    }

    /**
     * Wait until unread message with particular subject in email box will appear
     * @param sec - amount of seconds
     * @param subject - subject of message
     * @return null if time is over but there are no unread messages with specified subject found
     */
    @StepGroup
    public Message waitUntilNewMessageWithSubjectReceived(long sec, String folderName, String subject) throws TimeoutException {
        logger.info(String.format("Waiting for unread message with subject <%s> ...", subject));
        Message foundMsg = null;
        boolean flag = true;
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            while (flag) {
                Message[] msgs = this.getAllUnreadMessagesFrom();
                if (msgs.length != 0) {
                    for (Message message : msgs) {
                        try {
                            if (message.getSubject() != null) {
                                if (message.getSubject().equals(subject)) {
                                    foundMsg = message;
                                    flag = false;
                                } else this.markMessageAsSeen(message);
                            } else this.markMessageAsSeen(message);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            logger.info("Message subject: " + message.getSubject());
                        }
                    }
                }
                if (foundMsg == null) {
                    if (stopWatch.getTime(TimeUnit.SECONDS) > sec) {
                        stopWatch.stop();
                        logger.info("Waited time: " + stopWatch.getTime(TimeUnit.SECONDS) + " seconds.");
                        throw new TimeoutException("Time is up, no message received!");
                    } else {
                        refreshSession();
                        this.openFolder(folderName, Folder.READ_WRITE);
                    }
                }
            }
        } catch (FolderNotFoundException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return foundMsg;
    }

    /**
     * Wait until unread message with particular subject in email box will appear
     * @param sec - amount of seconds
     * @param subject - subject of message
     * @param searchStr - email text have to contains this string
     * @return null if time is over but there are no unread messages with specified subject found
     */
    @StepGroup
    public Message waitForNewMessageWithTitleAndText(long sec, String folderName, String subject, String searchStr) throws TimeoutException {
        logger.info(String.format("Waiting for unread message with subject <%s> and text <%s> ...", subject, searchStr));
        Message foundMsg = null;
        boolean flag = true;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        while (flag) {
            Message message = null;
            try {
                message = this.waitUntilNewMessageWithSubjectReceived(sec - stopWatch.getTime(TimeUnit.SECONDS),
                        folderName, subject);
                if (EmailExecutor.getInstance().getText(message).contains(searchStr)) {
                    flag = false;
                    foundMsg = message;
                }
            } catch (TimeoutException e) {
                if (stopWatch.getTime(TimeUnit.SECONDS) > sec) {
                    stopWatch.stop();
                    logger.info("Waited time: " + stopWatch.getTime(TimeUnit.SECONDS) + " seconds.");
                    throw new TimeoutException("Time is up, no message received!");
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return foundMsg;
    }
}
