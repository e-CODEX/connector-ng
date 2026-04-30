
package org.etsi.uri._02640.v2;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2000._09.xmldsig_.SignatureType;


/**
 * &lt;p&gt;Java class for REMEvidenceType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="REMEvidenceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}EventCode" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}EventReasons" minOccurs="0"/&gt;
 *         &lt;element name="EvidenceIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}EvidenceIssuerPolicyID" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}EvidenceIssuerDetails"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}SenderAuthenticationDetails" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}RecipientAuthenticationDetails" minOccurs="0"/&gt;
 *         &lt;element name="EventTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="SubmissionTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="ReplyTo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *           &lt;element name="ReplyToAddress" type="{http://uri.etsi.org/02640/v2#}AttributedElectronicAddressType"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}SenderDetails"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}RecipientsDetails"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}RecipientsDelegatesDetails" minOccurs="0"/&gt;
 *         &lt;element name="EvidenceRefersToRecipient" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}SenderMessageDetails" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}NotificationMessageDetails" minOccurs="0"/&gt;
 *         &lt;element name="ForwardedToExternalSystem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}TransactionLogInformation" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}Extensions" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Signature" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="Id" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "REMEvidenceType", propOrder = {
    "eventCode",
    "eventReasons",
    "evidenceIdentifier",
    "evidenceIssuerPolicyID",
    "evidenceIssuerDetails",
    "senderAuthenticationDetails",
    "recipientAuthenticationDetails",
    "eventTime",
    "submissionTime",
    "replyTo",
    "replyToAddress",
    "senderDetails",
    "recipientsDetails",
    "recipientsDelegatesDetails",
    "evidenceRefersToRecipient",
    "senderMessageDetails",
    "notificationMessageDetails",
    "forwardedToExternalSystem",
    "transactionLogInformation",
    "extensions",
    "signature"
})
public class REMEvidenceType {

    @XmlElement(name = "EventCode")
    @XmlSchemaType(name = "anyURI")
    protected String eventCode;
    @XmlElement(name = "EventReasons")
    protected EventReasonsType eventReasons;
    @XmlElement(name = "EvidenceIdentifier", required = true)
    protected String evidenceIdentifier;
    @XmlElement(name = "EvidenceIssuerPolicyID")
    protected EvidenceIssuerPolicyIDType evidenceIssuerPolicyID;
    @XmlElement(name = "EvidenceIssuerDetails", required = true)
    protected EntityDetailsType evidenceIssuerDetails;
    @XmlElement(name = "SenderAuthenticationDetails")
    protected AuthenticationDetailsType senderAuthenticationDetails;
    @XmlElement(name = "RecipientAuthenticationDetails")
    protected AuthenticationDetailsType recipientAuthenticationDetails;
    @XmlElement(name = "EventTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar eventTime;
    @XmlElement(name = "SubmissionTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar submissionTime;
    @XmlElement(name = "ReplyTo")
    protected String replyTo;
    @XmlElement(name = "ReplyToAddress")
    protected AttributedElectronicAddressType replyToAddress;
    @XmlElement(name = "SenderDetails", required = true)
    protected EntityDetailsType senderDetails;
    @XmlElement(name = "RecipientsDetails", required = true)
    protected EntityDetailsListType recipientsDetails;
    @XmlElement(name = "RecipientsDelegatesDetails")
    protected RecipientsDelegatesType recipientsDelegatesDetails;
    @XmlElement(name = "EvidenceRefersToRecipient")
    protected BigInteger evidenceRefersToRecipient;
    @XmlElement(name = "SenderMessageDetails")
    protected MessageDetailsType senderMessageDetails;
    @XmlElement(name = "NotificationMessageDetails")
    protected MessageDetailsType notificationMessageDetails;
    @XmlElement(name = "ForwardedToExternalSystem")
    protected String forwardedToExternalSystem;
    @XmlElement(name = "TransactionLogInformation")
    protected TransactionLogInformationType transactionLogInformation;
    @XmlElement(name = "Extensions")
    protected ExtensionsListType extensions;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected SignatureType signature;
    @XmlAttribute(name = "version", required = true)
    protected String version;
    @XmlAttribute(name = "Id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the eventCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventCode() {
        return eventCode;
    }

    /**
     * Sets the value of the eventCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventCode(String value) {
        this.eventCode = value;
    }

    /**
     * Gets the value of the eventReasons property.
     * 
     * @return
     *     possible object is
     *     {@link EventReasonsType }
     *     
     */
    public EventReasonsType getEventReasons() {
        return eventReasons;
    }

    /**
     * Sets the value of the eventReasons property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventReasonsType }
     *     
     */
    public void setEventReasons(EventReasonsType value) {
        this.eventReasons = value;
    }

    /**
     * Gets the value of the evidenceIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEvidenceIdentifier() {
        return evidenceIdentifier;
    }

    /**
     * Sets the value of the evidenceIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEvidenceIdentifier(String value) {
        this.evidenceIdentifier = value;
    }

    /**
     * Gets the value of the evidenceIssuerPolicyID property.
     * 
     * @return
     *     possible object is
     *     {@link EvidenceIssuerPolicyIDType }
     *     
     */
    public EvidenceIssuerPolicyIDType getEvidenceIssuerPolicyID() {
        return evidenceIssuerPolicyID;
    }

    /**
     * Sets the value of the evidenceIssuerPolicyID property.
     * 
     * @param value
     *     allowed object is
     *     {@link EvidenceIssuerPolicyIDType }
     *     
     */
    public void setEvidenceIssuerPolicyID(EvidenceIssuerPolicyIDType value) {
        this.evidenceIssuerPolicyID = value;
    }

    /**
     * Gets the value of the evidenceIssuerDetails property.
     * 
     * @return
     *     possible object is
     *     {@link EntityDetailsType }
     *     
     */
    public EntityDetailsType getEvidenceIssuerDetails() {
        return evidenceIssuerDetails;
    }

    /**
     * Sets the value of the evidenceIssuerDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityDetailsType }
     *     
     */
    public void setEvidenceIssuerDetails(EntityDetailsType value) {
        this.evidenceIssuerDetails = value;
    }

    /**
     * Gets the value of the senderAuthenticationDetails property.
     * 
     * @return
     *     possible object is
     *     {@link AuthenticationDetailsType }
     *     
     */
    public AuthenticationDetailsType getSenderAuthenticationDetails() {
        return senderAuthenticationDetails;
    }

    /**
     * Sets the value of the senderAuthenticationDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthenticationDetailsType }
     *     
     */
    public void setSenderAuthenticationDetails(AuthenticationDetailsType value) {
        this.senderAuthenticationDetails = value;
    }

    /**
     * Gets the value of the recipientAuthenticationDetails property.
     * 
     * @return
     *     possible object is
     *     {@link AuthenticationDetailsType }
     *     
     */
    public AuthenticationDetailsType getRecipientAuthenticationDetails() {
        return recipientAuthenticationDetails;
    }

    /**
     * Sets the value of the recipientAuthenticationDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthenticationDetailsType }
     *     
     */
    public void setRecipientAuthenticationDetails(AuthenticationDetailsType value) {
        this.recipientAuthenticationDetails = value;
    }

    /**
     * Gets the value of the eventTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEventTime() {
        return eventTime;
    }

    /**
     * Sets the value of the eventTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEventTime(XMLGregorianCalendar value) {
        this.eventTime = value;
    }

    /**
     * Gets the value of the submissionTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSubmissionTime() {
        return submissionTime;
    }

    /**
     * Sets the value of the submissionTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSubmissionTime(XMLGregorianCalendar value) {
        this.submissionTime = value;
    }

    /**
     * Gets the value of the replyTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReplyTo() {
        return replyTo;
    }

    /**
     * Sets the value of the replyTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReplyTo(String value) {
        this.replyTo = value;
    }

    /**
     * Gets the value of the replyToAddress property.
     * 
     * @return
     *     possible object is
     *     {@link AttributedElectronicAddressType }
     *     
     */
    public AttributedElectronicAddressType getReplyToAddress() {
        return replyToAddress;
    }

    /**
     * Sets the value of the replyToAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributedElectronicAddressType }
     *     
     */
    public void setReplyToAddress(AttributedElectronicAddressType value) {
        this.replyToAddress = value;
    }

    /**
     * Gets the value of the senderDetails property.
     * 
     * @return
     *     possible object is
     *     {@link EntityDetailsType }
     *     
     */
    public EntityDetailsType getSenderDetails() {
        return senderDetails;
    }

    /**
     * Sets the value of the senderDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityDetailsType }
     *     
     */
    public void setSenderDetails(EntityDetailsType value) {
        this.senderDetails = value;
    }

    /**
     * Gets the value of the recipientsDetails property.
     * 
     * @return
     *     possible object is
     *     {@link EntityDetailsListType }
     *     
     */
    public EntityDetailsListType getRecipientsDetails() {
        return recipientsDetails;
    }

    /**
     * Sets the value of the recipientsDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityDetailsListType }
     *     
     */
    public void setRecipientsDetails(EntityDetailsListType value) {
        this.recipientsDetails = value;
    }

    /**
     * Gets the value of the recipientsDelegatesDetails property.
     * 
     * @return
     *     possible object is
     *     {@link RecipientsDelegatesType }
     *     
     */
    public RecipientsDelegatesType getRecipientsDelegatesDetails() {
        return recipientsDelegatesDetails;
    }

    /**
     * Sets the value of the recipientsDelegatesDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecipientsDelegatesType }
     *     
     */
    public void setRecipientsDelegatesDetails(RecipientsDelegatesType value) {
        this.recipientsDelegatesDetails = value;
    }

    /**
     * Gets the value of the evidenceRefersToRecipient property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getEvidenceRefersToRecipient() {
        return evidenceRefersToRecipient;
    }

    /**
     * Sets the value of the evidenceRefersToRecipient property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setEvidenceRefersToRecipient(BigInteger value) {
        this.evidenceRefersToRecipient = value;
    }

    /**
     * Gets the value of the senderMessageDetails property.
     * 
     * @return
     *     possible object is
     *     {@link MessageDetailsType }
     *     
     */
    public MessageDetailsType getSenderMessageDetails() {
        return senderMessageDetails;
    }

    /**
     * Sets the value of the senderMessageDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageDetailsType }
     *     
     */
    public void setSenderMessageDetails(MessageDetailsType value) {
        this.senderMessageDetails = value;
    }

    /**
     * Gets the value of the notificationMessageDetails property.
     * 
     * @return
     *     possible object is
     *     {@link MessageDetailsType }
     *     
     */
    public MessageDetailsType getNotificationMessageDetails() {
        return notificationMessageDetails;
    }

    /**
     * Sets the value of the notificationMessageDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageDetailsType }
     *     
     */
    public void setNotificationMessageDetails(MessageDetailsType value) {
        this.notificationMessageDetails = value;
    }

    /**
     * Gets the value of the forwardedToExternalSystem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForwardedToExternalSystem() {
        return forwardedToExternalSystem;
    }

    /**
     * Sets the value of the forwardedToExternalSystem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForwardedToExternalSystem(String value) {
        this.forwardedToExternalSystem = value;
    }

    /**
     * Gets the value of the transactionLogInformation property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionLogInformationType }
     *     
     */
    public TransactionLogInformationType getTransactionLogInformation() {
        return transactionLogInformation;
    }

    /**
     * Sets the value of the transactionLogInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionLogInformationType }
     *     
     */
    public void setTransactionLogInformation(TransactionLogInformationType value) {
        this.transactionLogInformation = value;
    }

    /**
     * Gets the value of the extensions property.
     * 
     * @return
     *     possible object is
     *     {@link ExtensionsListType }
     *     
     */
    public ExtensionsListType getExtensions() {
        return extensions;
    }

    /**
     * Sets the value of the extensions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtensionsListType }
     *     
     */
    public void setExtensions(ExtensionsListType value) {
        this.extensions = value;
    }

    /**
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     {@link SignatureType }
     *     
     */
    public SignatureType getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureType }
     *     
     */
    public void setSignature(SignatureType value) {
        this.signature = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
