
package org.etsi.uri._02640.v2;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for EntityNameType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="EntityNameType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Name" type="{http://uri.etsi.org/02231/v2#}NonEmptyString" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang"/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntityNameType", propOrder = {
    "name"
})
public class EntityNameType {

    @XmlElement(name = "Name", required = true)
    protected List<String> name;
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
     * Gets the value of the name property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the name property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * </p>
     * 
     * 
     * @return
     *     The value of the name property.
     */
    public List<String> getName() {
        if (name == null) {
            name = new ArrayList<>();
        }
        return this.name;
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
