
package org.etsi.uri._02640.v2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for NamePostalAddressType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="NamePostalAddressType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}EntityName" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}PostalAddress" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NamePostalAddressType", propOrder = {
    "entityName",
    "postalAddress"
})
public class NamePostalAddressType {

    @XmlElement(name = "EntityName")
    protected EntityNameType entityName;
    @XmlElement(name = "PostalAddress")
    protected PostalAddressType postalAddress;

    /**
     * Gets the value of the entityName property.
     * 
     * @return
     *     possible object is
     *     {@link EntityNameType }
     *     
     */
    public EntityNameType getEntityName() {
        return entityName;
    }

    /**
     * Sets the value of the entityName property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityNameType }
     *     
     */
    public void setEntityName(EntityNameType value) {
        this.entityName = value;
    }

    /**
     * Gets the value of the postalAddress property.
     * 
     * @return
     *     possible object is
     *     {@link PostalAddressType }
     *     
     */
    public PostalAddressType getPostalAddress() {
        return postalAddress;
    }

    /**
     * Sets the value of the postalAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostalAddressType }
     *     
     */
    public void setPostalAddress(PostalAddressType value) {
        this.postalAddress = value;
    }

}
