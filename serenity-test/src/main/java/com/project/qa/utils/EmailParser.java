package com.project.qa.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Evgeny.Gurinovich on 11/15/2018.
 */
public class EmailParser {

    private final Logger LOGGER = LoggerFactory.getLogger(EmailParser.class);
    private static EmailParser emailParserOjb;

    private EmailParser() {

    }

    public static EmailParser getInstance() {
        if (emailParserOjb == null) {
            emailParserOjb = new EmailParser();
        }
        return emailParserOjb;
    }

    public String parsePasswordFromEmail(String emailText) {
        if (emailText.isEmpty() || emailText == null) throw new RuntimeException("Email text is empty");
        String result;
        Pattern pattern = Pattern.compile("[pP]assword: (.*)</b>");
        Matcher matcher = pattern.matcher(emailText);
        result = this.applyMatcherToText(matcher, emailText);
        if (!result.isEmpty()) {
            LOGGER.info("Parsed password is: =========== " + result + " ===============");
        }
        return result;
    }

    public String parseTemporaryLoginLink(String emailText) {
        if (emailText.isEmpty() || emailText == null) throw new RuntimeException("Email text is empty");
        String result;
        Pattern pattern = Pattern.compile("<a href=\"(.*)\">Click here");
        Matcher matcher = pattern.matcher(emailText);
        result = this.applyMatcherToText(matcher, emailText);
        if (!result.isEmpty()) {
            LOGGER.info("Parsed link is: =========== " + result + " ===============");
        }
        return result;
    }

    private String applyMatcherToText(Matcher matcher, String text) {
        String result;
        if (matcher.find()) {
            result = matcher.group(1).replace("<b>", "");
        } else {
            LOGGER.info("======================== Email text started: ========================");
            LOGGER.info(text);
            throw new RuntimeException("There was unable to parse the password.");
        }
        return result;
    }
}