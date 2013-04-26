
package gov.loc.repository.bagit;

import gov.loc.repository.bagit.utilities.namevalue.NameValueMapList;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/*
 * A bag-info.txt file containing name-value pairs.
 * 
 * According to the BagIt Spec, the bag-info.txt is a set of name-value pairs.
 * It does not specify whether ordering is significant, capitalization is
 * significant, or if fields can be repeated.
 * 
 * Prior to version 3.7, this implementation treated ordering as significant and
 * capitalization as insignificant, but did not allow fields to be repeated.
 * Not allowing fields to be repeated if reflected in the use of the Map interface.
 * 
 * Starting with version 3.7, this implementation supports repeated fields, but still maintains
 * the Map interface.  For getters which only return a single value for a field, if there
 * are multiple instances of the field, only the first value is set.  For setters in the
 * Map interface or setters for standard fields, the new value replaces the value of the first instance. 
 */
public interface BagInfoTxt extends BagFile, NameValueMapList {

    static final int UNKNOWN_TOTAL_BAGS_IN_GROUP = -1;

    static final String UNKNOWN_TOTAL_BAGS_IN_GROUP_MARKER = "?";

    static final String TYPE = "BagInfoTxt";

    void setSourceOrganization(final String sourceOrganization);

    void addSourceOrganization(final String sourceOrganization);

    void setOrganizationAddress(final String organizationAddress);

    void addOrganizationAddress(final String organizationAddress);

    void setContactName(final String contactName);

    void addContactName(final String contactName);

    void setContactPhone(final String contactPhone);

    void addContactPhone(final String contactPhone);

    void setContactEmail(final String contactEmail);

    void addContactEmail(final String contactEmail);

    void setExternalDescription(final String externalDescription);

    void addExternalDescription(final String externalDescription);

    void setBaggingDate(final String baggingDate);

    void setBaggingDate(final int year, final int month, final int day);

    void setBaggingDate(final Date date);

    void setExternalIdentifier(final String externalIdentifier);

    void addExternalIdentifier(final String externalIdentifier);

    void setBagSize(final String bagSize);

    void generateBagSize(final Bag bag);

    void setPayloadOxum(final String payloadOxum);

    void setPayloadOxum(final long octetCount, final long streamCount);

    void generatePayloadOxum(final Bag bag);

    void setBagGroupIdentifier(final String bagGroupIdentifier);

    void addBagGroupIdentifier(final String bagGroupIdentifier);

    void setBagCount(final String bagCount);

    void setBagCount(final int bagInGroup, final int totalBagsInGroup);

    void setInternalSenderIdentifier(final String internalSenderIdentifier);

    void addInternalSenderIdentifier(final String internalSenderIdentifier);

    void setInternalSenderDescription(final String internalSenderDescription);

    void addInternalSenderDescription(final String internalSenderDescription);

    String getSourceOrganization();

    List<String> getSourceOrganizationList();

    String getOrganizationAddress();

    List<String> getOrganizationAddressList();

    String getContactName();

    List<String> getContactNameList();

    String getContactPhone();

    List<String> getContactPhoneList();

    String getContactEmail();

    List<String> getContactEmailList();

    String getExternalDescription();

    List<String> getExternalDescriptionList();

    String getBaggingDate();

    Date getBaggingDateObj() throws ParseException;

    String getExternalIdentifier();

    List<String> getExternalIdentifierList();

    String getBagSize();

    String getPayloadOxum();

    Long getOctetCount() throws ParseException;

    Long getStreamCount() throws ParseException;

    String getBagGroupIdentifier();

    List<String> getBagGroupIdentifierList();

    String getBagCount();

    Integer getBagInGroup() throws ParseException;

    Integer getTotalBagsInGroup() throws ParseException;

    String getInternalSenderIdentifier();

    List<String> getInternalSenderIdentifierList();

    String getInternalSenderDescription();

    List<String> getInternalSenderDescriptionList();

    List<String> getStandardFields();

    List<String> getNonstandardFields();

    List<String> getListCaseInsensitive(final String key);

    String getCaseInsensitive(final String key);

    boolean containsKeyCaseInsensitive(final String key);

}
