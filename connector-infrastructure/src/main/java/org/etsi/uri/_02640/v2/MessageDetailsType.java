
package org.etsi.uri._02640.v2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.w3._2000._09.xmldsig_.DigestMethodType;


/**
 * &lt;p&gt;Java class for MessageDetailsType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="MessageDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MessageSubject" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="UAMessageIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MessageIdentifierByREMMD" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}DigestMethod" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}DigestValue" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="isNotification" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MessageDetailsType", propOrder = {
    "messageSubject",
    "uaMessageIdentifier",
    "messageIdentifierByREMMD",
    "digestMethod",
    "digestValue"
})
public class MessageDetailsType {

    @XmlElement(name = "MessageSubject", required = true)
    protected String messageSubject;
    @XmlElement(name = "UAMessageIdentifier")
    protected String uaMessageIdentifier;
    @XmlElement(name = "MessageIdentifierByREMMD", required = true)
    protected String messageIdentifierByREMMD;
    @XmlElement(name = "DigestMethod", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected DigestMethodType digestMethod;
    @XmlElement(name = "DigestValue", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected byte[] digestValue;
    @XmlAttribute(name = "isNotification", required = true)
    protected boolean isNotification;

    /**
     * Gets the value of the messageSubject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageSubject() {
        return messageSubject;
    }

    /**
     * Sets the value of the messageSubject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageSubject(String value) {
        this.messageSubject = value;
    }

    /**
     * Gets the value of the uaMessageIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUAMessageIdentifier() {
        return uaMessageIdentifier;
    }

    /**
     * Sets the value of the uaMessageIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUAMessageIdentifier(String value) {
        this.uaMessageIdentifier = value;
    }

    /**
     * Gets the value of the messageIdentifierByREMMD property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageIdentifierByREMMD() {
        return messageIdentifierByREMMD;
    }

    /**
     * Sets the value of the messageIdentifierByREMMD property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageIdentifierByREMMD(String value) {
        this.messageIdentifierByREMMD = value;
    }

    /**
     * Gets the value of the digestMethod property.
     * 
     * @return
     *     possible object is
     *     {@link DigestMethodType }
     *     
     */
    public DigestMethodType getDigestMethod() {
        return digestMethod;
    }

    /**
     * Sets the value of the digestMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link DigestMethodType }
     *     
     */
    public void setDigestMethod(DigestMethodType value) {
        this.digestMethod = value;
    }

    /**
     * Gets the value of the digestValue property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getDigestValue() {
        return digestValue;
    }

    /**
     * Sets the value of the digestValue property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setDigestValue(byte[] value) {
        this.digestValue = value;
    }

    /**
     * Gets the value of the isNotification property.
     * 
     */
    public boolean isIsNotification() {
        return isNotification;
    }

    /**
     * Sets the value of the isNotification property.
     * 
     */
    public void setIsNotification(boolean value) {
        this.isNotification = value;
    }

}
