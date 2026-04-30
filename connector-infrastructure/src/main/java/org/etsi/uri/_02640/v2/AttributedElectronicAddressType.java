
package org.etsi.uri._02640.v2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;


/**
 * &lt;p&gt;Java class for AttributedElectronicAddressType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AttributedElectronicAddressType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://uri.etsi.org/02231/v2#&gt;NonEmptyURIType"&gt;
 *       &lt;attribute name="scheme" type="{http://www.w3.org/2001/XMLSchema}string" default="mailto" /&gt;
 *       &lt;attribute name="DisplayName" type="{http://uri.etsi.org/02231/v2#}NonEmptyString" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributedElectronicAddressType", propOrder = {
    "value"
})
public class AttributedElectronicAddressType {

    @XmlValue
    protected String value;
    /**
     * Defaults to mailto, if not present
     * 
     */
    @XmlAttribute(name = "scheme")
    protected String scheme;
    @XmlAttribute(name = "DisplayName")
    protected String displayName;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Defaults to mailto, if not present
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScheme() {
        if (scheme == null) {
            return "mailto";
        } else {
            return scheme;
        }
    }

    /**
     * Sets the value of the scheme property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getScheme()
     */
    public void setScheme(String value) {
        this.scheme = value;
    }

    /**
     * Gets the value of the displayName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

}
