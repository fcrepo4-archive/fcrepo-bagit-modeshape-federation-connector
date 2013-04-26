
package gov.loc.repository.bagit.utilities;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SimpleMessage {

    public static final String MESSAGE_TYPE_ERROR = "error";

    public static final String MESSAGE_TYPE_WARNING = "warning";

    public static final String MESSAGE_TYPE_INFO = "info";

    private String code = null;

    private String message = null;

    private String subject = null;

    private Set<String> objects = null;

    private String messageType = MESSAGE_TYPE_ERROR;

    public SimpleMessage() {
    }

    public SimpleMessage(final String message) {
        this.message = message;
    }

    public SimpleMessage(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public SimpleMessage(final String code, final String message,
            final String subject) {
        this.code = code;
        this.message = message;
        this.subject = subject;
    }

    public SimpleMessage(final String code, final String message,
            final String subject, final String object) {
        this.code = code;
        this.message = message;
        this.subject = subject;
        this.objects = new HashSet<String>();
        this.objects.add(object);
    }

    public SimpleMessage(final String code, final String message,
            final String subject, final String object, final String messageType) {
        this.code = code;
        this.message = message;
        this.subject = subject;
        this.objects = new HashSet<String>();
        this.objects.add(object);
        this.messageType = messageType;
    }

    public SimpleMessage(final String code, final String message,
            final String subject, final Collection<String> objects) {
        this.code = code;
        this.message = message;
        this.subject = subject;
        this.objects = new HashSet<String>();
        this.objects.addAll(objects);
    }

    public SimpleMessage(final String code, final String message,
            final String subject, final Collection<String> objects,
            final String messageType) {
        this.code = code;
        this.message = message;
        this.subject = subject;
        this.objects = new HashSet<String>();
        this.objects.addAll(objects);
        this.messageType = messageType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Set<String> getObjects() {
        return objects;
    }

    public void addObject(final String object) {
        if (this.objects == null) {
            this.objects = new HashSet<String>();
        }
        this.objects.add(object);
    }

    public void addObjects(final Collection<String> objects) {
        if (objects == null) {
            return;
        }

        if (this.objects == null) {
            this.objects = new HashSet<String>();
        }
        this.objects.addAll(objects);
    }

    public void setObjects(final Set<String> objects) {
        this.objects = objects;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        if (this.message != null) {
            if (this.subject == null) {
                return MessageFormat.format("({0}) ", this.messageType) +
                        this.message;
            }
            if (this.objects == null) {
                return MessageFormat.format("({0}) ", this.messageType) +
                        MessageFormat.format(this.message, this.subject);
            }
            return MessageFormat.format("({0}) ", this.messageType) +
                    MessageFormat.format(this.message, this.subject,
                            this.objects);
        }
        return super.toString();
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(final String messageType) {
        this.messageType = messageType;
    }
}