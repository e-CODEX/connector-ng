
package org.etsi.uri._02231.v2_;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.w3._2000._09.xmldsig_.KeyValueType;


/**
 * &lt;p&gt;Java class for DigitalIdentityType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="DigitalIdentityType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="X509Certificate" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/&gt;
 *         &lt;element name="X509SubjectName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}KeyValue"/&gt;
 *         &lt;element name="X509SKI" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/&gt;
 *         &lt;element name="Other" type="{http://uri.etsi.org/02231/v2#}AnyType"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DigitalIdentityType", propOrder = {
    "x509Certificate",
    "x509SubjectName",
    "keyValue",
    "x509SKI",
    "other"
})
public class DigitalIdentityType {

    @XmlElement(name = "X509Certificate")
    protected byte[] x509Certificate;
    @XmlElement(name = "X509SubjectName")
    protected String x509SubjectName;
    @XmlElement(name = "KeyValue", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected KeyValueType keyValue;
    @XmlElement(name = "X509SKI")
    protected byte[] x509SKI;
    @XmlElement(name = "Other")
    protected AnyType other;

    /**
     * Gets the value of the x509Certificate property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getX509Certificate() {
        return x509Certificate;
    }

    /**
     * Sets the value of the x509Certificate property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setX509Certificate(byte[] value) {
        this.x509Certificate = value;
    }

    /**
     * Gets the value of the x509SubjectName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getX509SubjectName() {
        return x509SubjectName;
    }

    /**
     * Sets the value of the x509SubjectName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setX509SubjectName(String value) {
        this.x509SubjectName = value;
    }

    /**
     * Gets the value of the keyValue property.
     * 
     * @return
     *     possible object is
     *     {@link KeyValueType }
     *     
     */
    public KeyValueType getKeyValue() {
        return keyValue;
    }

    /**
     * Sets the value of the keyValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link KeyValueType }
     *     
     */
    public void setKeyValue(KeyValueType value) {
        this.keyValue = value;
    }

    /**
     * Gets the value of the x509SKI property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getX509SKI() {
        return x509SKI;
    }

    /**
     * Sets the value of the x509SKI property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setX509SKI(byte[] value) {
        this.x509SKI = value;
    }

    /**
     * Gets the value of the other property.
     * 
     * @return
     *     possible object is
     *     {@link AnyType }
     *     
     */
    public AnyType getOther() {
        return other;
    }

    /**
     * Sets the value of the other property.
     * 
     * @param value
     *     allowed object is
     *     {@link AnyType }
     *     
     */
    public void setOther(AnyType value) {
        this.other = value;
    }

}
