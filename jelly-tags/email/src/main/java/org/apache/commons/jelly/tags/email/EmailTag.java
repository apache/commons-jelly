/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly.tags.email;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Basic tag for sending an email. Supports one attachment, multiple to addresses delimited by ";",
 * multiple cc addresses, etc.
 */

public class EmailTag extends TagSupport {
    private final Log logger = LogFactory.getLog(EmailTag.class);

    /** Smtp server */
    private Expression server       = null;

    /** Who to send the message as */
    private Expression from         = null;

    /** Who to send to */
    private Expression to           = null;

    /** Who to cc */
    private Expression cc           = null;

    /** Mail subject */
    private Expression subject      = null;

    /** Mail message */
    private Expression message      = null;

    /** File attachment */
    private File attachment     = null;

    /** Whether we should encode the XML body as text */
    private boolean encodeXML = false;

    /**
     * Execute the tag
     */
    @Override
    public void doTag(final XMLOutput xmlOutput) throws JellyTagException {
        final Properties props = new Properties();
        props.putAll(context.getVariables());

        // if a server was set then configure the system property
        if (server != null) {
            final Object serverInput = this.server.evaluate(context);
            props.put("mail.smtp.host", serverInput.toString());
        } else if (props.get("mail.smtp.host") == null) {
            throw new JellyTagException("no smtp server configured");
        }

        // check if there was no to address
        if (to == null) {
            throw new JellyTagException("no to address specified");
        }

        // check if there was no from
        if (from == null) {
            throw new JellyTagException("no from address specified");
        }

        String messageBody = null;
        if (this.message != null) {
            messageBody = this.message.evaluate(context).toString();
        }
        else {
            // get message from body
            messageBody = getBodyText(encodeXML);
        }

        final Object fromInput = this.from.evaluate(context);
        final Object toInput = this.to.evaluate(context);

        // configure the mail session
        final Session session = Session.getDefaultInstance(props, null);

        // construct the mime message
        final MimeMessage msg = new MimeMessage(session);

        try {
            // set the from address
            msg.setFrom(new InternetAddress(fromInput.toString()));

            // parse out the to addresses
            StringTokenizer st = new StringTokenizer(toInput.toString(), ";");
            final InternetAddress[] addresses = new InternetAddress[st.countTokens()];
            int addressCount = 0;
            while (st.hasMoreTokens()) {
                addresses[addressCount++] = new InternetAddress(st.nextToken().trim());
            }

            // set the recipients
            msg.setRecipients(Message.RecipientType.TO, addresses);

            // parse out the cc addresses
            if (cc != null) {
                final Object ccInput = this.cc.evaluate(context);
                st = new StringTokenizer(ccInput.toString(), ";");
                final InternetAddress[] ccAddresses = new InternetAddress[st.countTokens()];
                int ccAddressCount = 0;
                while (st.hasMoreTokens()) {
                    ccAddresses[ccAddressCount++] = new InternetAddress(st.nextToken().trim());
                }

                // set the cc recipients
                msg.setRecipients(Message.RecipientType.CC, ccAddresses);
            }
        }
        catch (final MessagingException e) {
            throw new JellyTagException(e);
        }

        try {
            // set the subject
            if (subject != null) {
                final Object subjectInput = this.subject.evaluate(context);
                msg.setSubject(subjectInput.toString());
            }

            // set a timestamp
            msg.setSentDate(new Date());

            // if there was no attachment just set the text/body of the message
            if (attachment == null) {

                msg.setText(messageBody);

            }
            else {

                // attach the multipart mime message
                // setup the body
                final MimeBodyPart mbp1 = new MimeBodyPart();
                mbp1.setText(messageBody);

                // setup the attachment
                final MimeBodyPart mbp2 = new MimeBodyPart();
                final FileDataSource fds = new FileDataSource(attachment);
                mbp2.setDataHandler(new DataHandler(fds));
                mbp2.setFileName(fds.getName());

                // create the multipart and add its parts
                final Multipart mp = new MimeMultipart();
                mp.addBodyPart(mbp1);
                mp.addBodyPart(mbp2);

                msg.setContent(mp);

            }

            logger.info("sending email to " + to + " using " + server);

            // send the email
            Transport.send(msg);
        }
        catch (final MessagingException e) {
            throw new JellyTagException(e);
        }

    }

    /**
     * Sets the email attachment for the message. Only 1 attachment is supported right now
     */
    public void setAttach(final File attachment) throws FileNotFoundException {
        if (!attachment.exists()) {
            throw new FileNotFoundException("attachment not found");
        }

        this.attachment = attachment;
    }

    /**
     * ";" separated list of people to cc
     */
    public void setCC(final Expression cc) {
        this.cc = cc;
    }

    /**
     * Sets whether we should encode the XML body as text or not. The default
     * is false so that the body will assumed to be valid XML
     */
    public void setEncodeXML(final boolean encodeXML) {
        this.encodeXML = encodeXML;
    }

    /**
     * Sets the from address for the message
     */
    public void setFrom(final Expression from) {
        this.from = from;
    }

    /**
     * Sets the message body. This will override the Jelly tag body
     */
    public void setMessage(final Expression message) {
        this.message = message;
    }

    /**
     * Sets the smtp server for the message. If not set the system
     * property "mail.smtp.host" will be used.
     */
    public void setServer(final Expression server) {
        this.server = server;
    }

    /**
     * Sets the email subject
     */
    public void setSubject(final Expression subject) {
        this.subject = subject;
    }

    /**
     * ";" separated list of people to send to
     */
    public void setTo(final Expression to) {
        this.to = to;
    }
}
