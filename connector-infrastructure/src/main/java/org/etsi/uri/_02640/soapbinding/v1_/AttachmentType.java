
package org.etsi.uri._02640.soapbinding.v1_;

import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for AttachmentType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AttachmentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="Content-ID-Ref" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Embedded" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="Id" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="Size" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;attribute ref="{http://www.w3.org/2005/05/xmlmime}contentType use="required""/&gt;
 *       &lt;attribute name="Filename" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;minLength value="1"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="Content_Description"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;minLength value="1"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="Encoding"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="1"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang"/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttachmentType", propOrder = {
    "contentIDRef",
    "embedded"
})
public class AttachmentType {

    @XmlElement(name = "Content-ID-Ref")
    protected String contentIDRef;
    @XmlElement(name = "Embedded")
    protected byte[] embedded;
    @XmlAttribute(name = "Id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    /**
     * Size in bytes
     * 
     */
    @XmlAttribute(name = "Size", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger size;
    @XmlAttribute(name = "contentType", namespace = "http://www.w3.org/2005/05/xmlmime", required = true)
    protected String contentType;
    @XmlAttribute(name = "Filename", required = true)
    protected String filename;
    /**
     * i.e. offer, billl
     * 
     */
    @XmlAttribute(name = "Content_Description")
    protected String contentDescription;
    @XmlAttribute(name = "Encoding")
    protected String encoding;
    /**
     * Attempting to install the relevant ISO 2- and
     *                 3-letter
     *                 codes as the enumerated possible values is probably never
     *                 going to be a realistic possibility. See
     *                 RFC 3066 at
     *                 http://www.ietf.org/rfc/rfc3066.txt and the IANA registry
     *                 at
     *                 http://www.iana.org/assignments/lang-tag-apps.htm for
     *                 further
     *                 information.
     * 
     *                 The union allows for the 'un-declaration' of xml:lang
     *                 with
     *                 the empty string.
     * 
     */
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    protected String lang;

    /**
     * Gets the value of the contentIDRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentIDRef() {
        return contentIDRef;
    }

    /**
     * Sets the value of the contentIDRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentIDRef(String value) {
        this.contentIDRef = value;
    }

    /**
     * Gets the value of the embedded property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getEmbedded() {
        return embedded;
    }

    /**
     * Sets the value of the embedded property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setEmbedded(byte[] value) {
        this.embedded = value;
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

    /**
     * Size in bytes
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     * @see #getSize()
     */
    public void setSize(BigInteger value) {
        this.size = value;
    }

    /**
     * Gets the value of the contentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the value of the contentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentType(String value) {
        this.contentType = value;
    }

    /**
     * Gets the value of the filename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilename(String value) {
        this.filename = value;
    }

    /**
     * i.e. offer, billl
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentDescription() {
        return contentDescription;
    }

    /**
     * Sets the value of the contentDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getContentDescription()
     */
    public void setContentDescription(String value) {
        this.contentDescription = value;
    }

    /**
     * Gets the value of the encoding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncoding(String value) {
        this.encoding = value;
    }

    /**
     * Attempting to install the relevant ISO 2- and
     *                 3-letter
     *                 codes as the enumerated possible values is probably never
     *                 going to be a realistic possibility. See
     *                 RFC 3066 at
     *                 http://www.ietf.org/rfc/rfc3066.txt and the IANA registry
     *                 at
     *                 http://www.iana.org/assignments/lang-tag-apps.htm for
     *                 further
     *                 information.
     * 
     *                 The union allows for the 'un-declaration' of xml:lang
     *                 with
     *                 the empty string.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getLang()
     */
    public void setLang(String value) {
        this.lang = value;
    }

}
