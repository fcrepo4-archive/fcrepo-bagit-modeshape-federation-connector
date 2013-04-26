
package gov.loc.repository.bagit.utilities;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleResult {

    protected boolean isSuccess = false;

    //Map<code, Map<subject, List<SimpleMessage>>
    protected Map<String, Map<String, SimpleMessage>> messages =
            new HashMap<String, Map<String, SimpleMessage>>();

    public static Integer DEFAULT_MAX_MESSAGES = 100;

    public static String DEFAULT_DELIM = " ";

    public SimpleResult() {
    }

    public SimpleResult(final boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public SimpleResult(final boolean isSuccess, final String message) {
        this.isSuccess = isSuccess;
        this.addSimpleMessage(new SimpleMessage(message));
    }

    public SimpleResult(final boolean isSuccess, final SimpleMessage message) {
        this.isSuccess = isSuccess;
        this.addSimpleMessage(message);
    }

    public SimpleResult(final boolean isSuccess,
            final Collection<String> messages) {
        this.isSuccess = isSuccess;
        this.addMessages(messages);
    }

    public void addMessage(final String message) {
        this.addSimpleMessage(new SimpleMessage(message));
    }

    public void addMessage(final String code, final String message) {
        this.addSimpleMessage(new SimpleMessage(code, message));
    }

    public void addMessage(final String code, final String message,
            final String subject) {
        this.addSimpleMessage(new SimpleMessage(code, message, subject));
    }

    public void addMessage(final String code, final String message,
            final String subject, final String object) {
        this.addSimpleMessage(new SimpleMessage(code, message, subject, object));
    }

    public void addMessage(final String code, final String message,
            final String subject, final Collection<String> objects) {
        this.addSimpleMessage(new SimpleMessage(code, message, subject, objects));
    }

    public void addWarningMessage(final String message) {
        final SimpleMessage simpleMessage = new SimpleMessage(message);
        simpleMessage.setMessageType(SimpleMessage.MESSAGE_TYPE_WARNING);
        this.addSimpleMessage(simpleMessage);
    }

    public void addWarningMessage(final String code, final String message) {
        final SimpleMessage simpleMessage = new SimpleMessage(message, code);
        simpleMessage.setMessageType(SimpleMessage.MESSAGE_TYPE_WARNING);
        this.addSimpleMessage(simpleMessage);
    }

    public void addWarningMessage(final String code, final String message,
            final String subject) {
        final SimpleMessage simpleMessage =
                new SimpleMessage(message, code, subject);
        simpleMessage.setMessageType(SimpleMessage.MESSAGE_TYPE_WARNING);
        this.addSimpleMessage(simpleMessage);
    }

    public void addWarningMessage(final String code, final String message,
            final String subject, final String object) {
        final SimpleMessage simpleMessage =
                new SimpleMessage(message, code, subject, object);
        simpleMessage.setMessageType(SimpleMessage.MESSAGE_TYPE_WARNING);
        this.addSimpleMessage(simpleMessage);
    }

    public void addWarningMessage(final String code, final String message,
            final String subject, final Collection<String> objects) {
        final SimpleMessage simpleMessage =
                new SimpleMessage(message, code, subject, objects);
        simpleMessage.setMessageType(SimpleMessage.MESSAGE_TYPE_WARNING);
        this.addSimpleMessage(simpleMessage);
    }

    public void addInfoMessage(final String message) {
        final SimpleMessage simpleMessage = new SimpleMessage(message);
        simpleMessage.setMessageType(SimpleMessage.MESSAGE_TYPE_INFO);
        this.addSimpleMessage(simpleMessage);
    }

    public void addInfoMessage(final String code, final String message) {
        final SimpleMessage simpleMessage = new SimpleMessage(message, code);
        simpleMessage.setMessageType(SimpleMessage.MESSAGE_TYPE_INFO);
        this.addSimpleMessage(simpleMessage);
    }

    public void addInfoMessage(final String code, final String message,
            final String subject) {
        final SimpleMessage simpleMessage =
                new SimpleMessage(message, code, subject);
        simpleMessage.setMessageType(SimpleMessage.MESSAGE_TYPE_INFO);
        this.addSimpleMessage(simpleMessage);
    }

    public void addInfoMessage(final String code, final String message,
            final String subject, final String object) {
        final SimpleMessage simpleMessage =
                new SimpleMessage(message, code, subject, object);
        simpleMessage.setMessageType(SimpleMessage.MESSAGE_TYPE_INFO);
        this.addSimpleMessage(simpleMessage);
    }

    public void addInfoWarningMessage(final String code, final String message,
            final String subject, final Collection<String> objects) {
        final SimpleMessage simpleMessage =
                new SimpleMessage(message, code, subject, objects);
        simpleMessage.setMessageType(SimpleMessage.MESSAGE_TYPE_INFO);
        this.addSimpleMessage(simpleMessage);
    }

    public void addSimpleMessage(final SimpleMessage message) {
        Map<String, SimpleMessage> subjectMap =
                this.messages.get(message.getCode());
        if (subjectMap == null) {
            subjectMap = new HashMap<String, SimpleMessage>();
            this.messages.put(message.getCode(), subjectMap);
        }
        final SimpleMessage existingMessage =
                subjectMap.get(message.getSubject());
        if (existingMessage != null) {
            existingMessage.addObjects(message.getObjects());
        } else {
            subjectMap.put(message.getSubject(), message);
        }

    }

    public void addMessages(final Collection<String> messages) {
        for (final String message : messages) {
            this.addSimpleMessage(new SimpleMessage(message));
        }
    }

    public void addSimpleMessages(final Collection<SimpleMessage> messages) {
        for (final SimpleMessage message : messages) {
            this.addSimpleMessage(message);
        }
    }

    public boolean isSuccess() {
        return this.isSuccess;
    }

    public String messagesToString() {
        return this.messagesToString(DEFAULT_MAX_MESSAGES, DEFAULT_DELIM);
    }

    public String messagesToString(final int maxMessages) {
        return this.messagesToString(maxMessages, DEFAULT_DELIM);
    }

    public String messagesToString(final int maxMessages, final String delim) {
        final StringBuffer buf = new StringBuffer();
        int count = 0;
        for (final SimpleMessage message : this.getSimpleMessages()) {
            count++;
            if (count > maxMessages) {
                buf.append(delim + "And others.");
                break;
            }
            if (buf.length() > 0) {
                buf.append(delim);
            }
            buf.append(message.toString());
        }
        final String messageString = buf.toString();
        return messageString;
    }

    public void setSuccess(final boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    @Override
    public String toString() {
        return this.toString(DEFAULT_MAX_MESSAGES, DEFAULT_DELIM);
    }

    public String toString(final int maxMessages) {
        return this.toString(maxMessages, DEFAULT_DELIM);
    }

    public String toString(final int maxMessages, String delim) {
        if (this.messages.isEmpty()) {
            delim = "";
        }
        final String msg =
                MessageFormat.format("Result is {0}.{1}{2}", this.isSuccess,
                        delim, this.messagesToString(maxMessages, delim));
        return msg;
    }

    public void merge(final SimpleResult result) {
        if (result == null) {
            return;
        }
        if (!(this.isSuccess() && result.isSuccess)) {
            this.isSuccess = false;
        }
        this.addSimpleMessages(result.getSimpleMessages());
    }

    public List<String> getMessages() {
        final List<String> messageStrings = new ArrayList<String>();
        for (final SimpleMessage message : getSimpleMessages()) {
            messageStrings.add(message.toString());
        }
        return messageStrings;
    }

    public List<String> getErrorMessages() {
        final List<String> messageStrings = new ArrayList<String>();
        for (final SimpleMessage message : getSimpleMessages()) {
            if (SimpleMessage.MESSAGE_TYPE_ERROR.equals(message
                    .getMessageType())) {
                messageStrings.add(message.toString());
            }
        }
        return messageStrings;
    }

    public List<String> getWarningMessages() {
        final List<String> messageStrings = new ArrayList<String>();
        for (final SimpleMessage message : getSimpleMessages()) {
            if (SimpleMessage.MESSAGE_TYPE_WARNING.equals(message
                    .getMessageType())) {
                messageStrings.add(message.toString());
            }
        }
        return messageStrings;
    }

    public List<String> getInfoMessages() {
        final List<String> messageStrings = new ArrayList<String>();
        for (final SimpleMessage message : getSimpleMessages()) {
            if (SimpleMessage.MESSAGE_TYPE_INFO
                    .equals(message.getMessageType())) {
                messageStrings.add(message.toString());
            }
        }
        return messageStrings;
    }

    public List<SimpleMessage> getSimpleMessages() {
        final List<SimpleMessage> messages = new ArrayList<SimpleMessage>();
        for (final Map<String, SimpleMessage> subjectMap : this.messages
                .values()) {
            messages.addAll(subjectMap.values());
        }
        return messages;
    }

    public void setSimpleMessages(final List<SimpleMessage> simpleMessages) {
        for (final SimpleMessage message : simpleMessages) {
            addSimpleMessage(message);
        }
    }

    public List<SimpleMessage> getSimpleMessagesByCode(final String code) {
        final List<SimpleMessage> messages = new ArrayList<SimpleMessage>();
        final Map<String, SimpleMessage> subjectMap = this.messages.get(code);
        if (subjectMap != null) {
            messages.addAll(subjectMap.values());
        }
        return messages;
    }

    public SimpleMessage getSimpleMessagesByCodeAndSubject(final String code,
            final String subject) {
        final Map<String, SimpleMessage> subjectMap = this.messages.get(code);
        if (subjectMap != null) {
            return subjectMap.get(subject);
        }
        return null;
    }

    public List<SimpleMessage> getSimpleMessagesByMessageType(
            final String messageType) {
        assert messageType != null;
        final List<SimpleMessage> messages = new ArrayList<SimpleMessage>();
        for (final SimpleMessage message : this.getSimpleMessages()) {
            if (messageType.equals(message.getMessageType())) {
                messages.add(message);
            }
        }
        return messages;
    }

    public List<SimpleMessage> getSimpleMessagesByMessageTypeAndCode(
            final String messageType, final String code) {
        assert messageType != null;
        final List<SimpleMessage> messages = new ArrayList<SimpleMessage>();
        for (final SimpleMessage message : this.getSimpleMessagesByCode(code)) {
            if (messageType.equals(message.getMessageType())) {
                messages.add(message);
            }
        }
        return messages;
    }

    public boolean hasSimpleMessage(final String code) {
        return !this.getSimpleMessagesByCode(code).isEmpty();
    }

    public boolean hasSimpleMessage(final String code, final String object) {
        return this.getSimpleMessagesByCodeAndSubject(code, object) != null;
    }
}
