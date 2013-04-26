
package gov.loc.repository.bagit.utilities.namevalue.impl;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;
import gov.loc.repository.bagit.utilities.namevalue.NameValueMapList;
import gov.loc.repository.bagit.utilities.namevalue.NameValueReader;
import gov.loc.repository.bagit.utilities.namevalue.NameValueReader.NameValue;
import gov.loc.repository.bagit.utilities.namevalue.NameValueWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;

public abstract class AbstractNameValueMapListBagFile extends
        AbstractMap<String, String> implements BagFile, NameValueMapList {

    String filepath;

    BagFile sourceBagFile = null;

    String originalFixity = null;

    String encoding;

    protected List<NameValue> nameValueList = new ArrayList<NameValue>();

    public AbstractNameValueMapListBagFile(final String filepath,
            final BagFile bagFile, final String encoding) {
        this.filepath = filepath;
        this.sourceBagFile = bagFile;
        this.encoding = encoding;
        final NameValueReader reader =
                new NameValueReaderImpl(encoding, sourceBagFile
                        .newInputStream(), this.getType());
        while (reader.hasNext()) {
            this.nameValueList.add(reader.next());
        }
        //Generate original fixity
        this.originalFixity =
                MessageDigestHelper.generateFixity(this.generatedInputStream(),
                        Algorithm.MD5);
    }

    public AbstractNameValueMapListBagFile(final String filepath,
            final String encoding) {
        this.filepath = filepath;
        this.encoding = encoding;
    }

    @Override
    public String getFilepath() {
        return this.filepath;
    }

    @Override
    public InputStream newInputStream() {
        //If this hasn't changed, then return sourceBagFile's inputstream
        //Otherwise, generate a new inputstream
        //This is to account for junk in the file, e.g., LF/CRs that might effect the fixity of this manifest
        if (MessageDigestHelper.fixityMatches(this.generatedInputStream(),
                Algorithm.MD5, this.originalFixity)) {
            return this.sourceBagFile.newInputStream();
        }
        return this.generatedInputStream();
    }

    public InputStream generatedInputStream() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final NameValueWriter writer =
                new NameValueWriterImpl(out, this.encoding, this.getType());
        try {
            for (final NameValue nameValue : this.nameValueList) {
                writer.write(nameValue.getName(), nameValue.getValue());
            }
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public long getSize() {
        final InputStream in = this.newInputStream();
        long size = 0L;
        try {
            while (in.read() != -1) {
                size++;
            }
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            IOUtils.closeQuietly(in);
        }
        return size;
    }

    public abstract String getType();

    public boolean containsKeyCaseInsensitive(final String key) {
        if (this.getCaseInsensitive(key) != null) {
            return true;
        }
        return false;
    }

    public String getActualKey(final String key) {
        for (final String name : this.keySet()) {
            if (name.equalsIgnoreCase(key)) {
                return name;
            }
        }
        return null;
    }

    public String getCaseInsensitive(final String key) {
        if (key == null) {
            return this.get(key);
        }
        for (final String name : this.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                return this.get(name);
            }
        }
        return null;
    }

    @Override
    public Set<java.util.Map.Entry<String, String>> entrySet() {
        final List<String> keys = new ArrayList<String>();
        final Set<java.util.Map.Entry<String, String>> entrySet =
                new HashSet<Entry<String, String>>();
        for (final NameValue nameValue : this.nameValueList) {
            //Only take the first
            if (!keys.contains(nameValue.getName())) {
                entrySet.add(nameValue);
                keys.add(nameValue.getName());
            }
        }
        return entrySet;
    }

    @Override
    public String put(final String key, final String value) {
        return this.put(new NameValue(key, value));
    }

    @Override
    public String put(final NameValue nameValue) {
        for (int i = 0; i < this.nameValueList.size(); i++) {
            if (this.nameValueList.get(i).getName().equals(nameValue.getName())) {
                this.nameValueList.set(i, nameValue);
                return nameValue.getValue();
            }
        }
        this.nameValueList.add(nameValue);
        return nameValue.getValue();
    }

    @Override
    public String remove(final Object key) {
        for (final NameValue nameValue : this.nameValueList) {
            if (nameValue.getName().equals(key)) {
                this.nameValueList.remove(nameValue);
                return nameValue.getValue();
            }
        }
        return null;
    }

    @Override
    public List<String> getList(final String key) {
        final List<String> values = new ArrayList<String>();
        for (final NameValue nameValue : this.nameValueList) {
            if (nameValue.getName().equals(key)) {
                values.add(nameValue.getValue());
            }
        }
        return values;
    }

    @Override
    public void putList(final String key, final Collection<String> values) {
        for (final String value : values) {
            this.putList(key, value);
        }
    }

    @Override
    public void putListAll(final Collection<NameValue> nameValues) {
        this.nameValueList.addAll(nameValues);

    }

    @Override
    public boolean removeList(final NameValue nameValue) {
        return this.nameValueList.remove(nameValue);
    }

    @Override
    public boolean removeList(final String key, final String value) {
        return this.removeList(new NameValue(key, value));
    }

    @Override
    public Iterator<NameValue> iterator() {
        return this.nameValueList.iterator();
    }

    @Override
    public List<NameValue> asList() {
        return this.nameValueList;
    }

    @Override
    public int sizeList() {
        return this.nameValueList.size();
    }

    @Override
    public void putList(final NameValue nameValue) {
        this.nameValueList.add(nameValue);
    }

    @Override
    public void putList(final String key, final String value) {
        this.nameValueList.add(new NameValue(key, value));
    }

    @Override
    public boolean removeAllList(final String key) {
        final List<NameValue> toRemove = new ArrayList<NameValue>();
        for (final NameValue nameValue : this.nameValueList) {
            if (nameValue.getName().equals(key)) {
                toRemove.add(nameValue);
            }
        }
        return this.nameValueList.removeAll(toRemove);
    }

    @Override
    public void clear() {
        this.nameValueList.clear();
    }
}
