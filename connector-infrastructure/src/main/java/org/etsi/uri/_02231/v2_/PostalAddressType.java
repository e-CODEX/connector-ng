
package org.etsi.uri._02231.v2_;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PostalAddressType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PostalAddressType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="StreetAddress" type="{http://uri.etsi.org/02231/v2#}NonEmptyString"/&gt;
 *         &lt;element name="Locality" type="{http://uri.etsi.org/02231/v2#}NonEmptyString"/&gt;
 *         &lt;element name="StateOrProvince" type="{http://uri.etsi.org/02231/v2#}NonEmptyString" minOccurs="0"/&gt;
 *         &lt;element name="PostalCode" type="{http://uri.etsi.org/02231/v2#}NonEmptyString" minOccurs="0"/&gt;
 *         &lt;element name="CountryName" type="{http://uri.etsi.org/02231/v2#}NonEmptyString"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang use="required""/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PostalAddressType", propOrder = {
    "streetAddress",
    "locality",
    "stateOrProvince",
    "postalCode",
    "countryName"
})
public class PostalAddressType {

    @XmlElement(name = "StreetAddress", required = true)
    protected String streetAddress;
    @XmlElement(name = "Locality", required = true)
    protected String locality;
    @XmlElement(name = "StateOrProvince")
    protected String stateOrProvince;
    @XmlElement(name = "PostalCode")
    protected String postalCode;
    @XmlElement(name = "CountryName", required = true)
    protected String countryName;
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
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace", required = true)
    protected String lang;

    /**
     * Gets the value of the streetAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * Sets the value of the streetAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreetAddress(String value) {
        this.streetAddress = value;
    }

    /**
     * Gets the value of the locality property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocality() {
        return locality;
    }

    /**
     * Sets the value of the locality property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocality(String value) {
        this.locality = value;
    }

    /**
     * Gets the value of the stateOrProvince property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStateOrProvince() {
        return stateOrProvince;
    }

    /**
     * Sets the value of the stateOrProvince property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStateOrProvince(String value) {
        this.stateOrProvince = value;
    }

    /**
     * Gets the value of the postalCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the value of the postalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostalCode(String value) {
        this.postalCode = value;
    }

    /**
     * Gets the value of the countryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Sets the value of the countryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryName(String value) {
        this.countryName = value;
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
