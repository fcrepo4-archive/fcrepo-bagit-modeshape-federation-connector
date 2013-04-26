
package gov.loc.repository.bagit.utilities.namevalue;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;

public interface NameValueReader extends Iterator<NameValueReader.NameValue> {

    public class NameValue implements Map.Entry<String, String> {

        private String name;

        private String value;

        public NameValue(final String name, final String value) {
            assert name != null;
            this.name = name;
            this.value = value;
        }

        public NameValue() {
        }

        public void setName(final String name) {
            assert name != null;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String setValue(final String value) {
            this.value = value;
            return value;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return MessageFormat.format("Name is {0}. Value is {1}.",
                    this.name, this.value);
        }

        @Override
        public String getKey() {
            return this.name;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof NameValue)) {
                return false;
            }
            final NameValue that = (NameValue) obj;
            if (!this.name.equals(that.getName())) {
                return false;
            }
            if ((this.value != null && that.getValue() == null) ||
                    (this.value == null && that.getValue() != null) ||
                    (!this.value.equals(that.getValue()))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return 42 + this.name.hashCode() +
                    (this.value != null ? this.value.hashCode() : 0);
        }
    }
}
