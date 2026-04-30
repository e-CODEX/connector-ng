
package org.etsi.uri._02640.v2;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;
import org.etsi.uri._01903.v1_3.AnyType;
import org.etsi.uri._02231.v2_.ElectronicAddressType;


/**
 * &lt;p&gt;Java class for EntityDetailsType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="EntityDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}NamesPostalAddresses" minOccurs="0"/&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element ref="{http://uri.etsi.org/02640/v2#}AttributedElectronicAddress"/&gt;
 *           &lt;element ref="{http://uri.etsi.org/02231/v2#}ElectronicAddress"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}CertificateDetails" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/01903/v1.3.2#}Any" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EntityDetailsType", propOrder = {
    "namesPostalAddresses",
    "attributedElectronicAddressOrElectronicAddress",
    "certificateDetails",
    "any"
})
public class EntityDetailsType {

    @XmlElement(name = "NamesPostalAddresses")
    protected NamesPostalAddressListType namesPostalAddresses;
    @XmlElements({
        @XmlElement(name = "AttributedElectronicAddress", type = AttributedElectronicAddressType.class),
        @XmlElement(name = "ElectronicAddress", namespace = "http://uri.etsi.org/02231/v2#", type = ElectronicAddressType.class)
    })
    protected List<Object> attributedElectronicAddressOrElectronicAddress;
    @XmlElement(name = "CertificateDetails")
    protected CertificateDetailsType certificateDetails;
    @XmlElement(name = "Any", namespace = "http://uri.etsi.org/01903/v1.3.2#")
    protected AnyType any;

    /**
     * Gets the value of the namesPostalAddresses property.
     * 
     * @return
     *     possible object is
     *     {@link NamesPostalAddressListType }
     *     
     */
    public NamesPostalAddressListType getNamesPostalAddresses() {
        return namesPostalAddresses;
    }

    /**
     * Sets the value of the namesPostalAddresses property.
     * 
     * @param value
     *     allowed object is
     *     {@link NamesPostalAddressListType }
     *     
     */
    public void setNamesPostalAddresses(NamesPostalAddressListType value) {
        this.namesPostalAddresses = value;
    }

    /**
     * Gets the value of the attributedElectronicAddressOrElectronicAddress property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the attributedElectronicAddressOrElectronicAddress property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getAttributedElectronicAddressOrElectronicAddress().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ElectronicAddressType }
     * {@link AttributedElectronicAddressType }
     * </p>
     * 
     * 
     * @return
     *     The value of the attributedElectronicAddressOrElectronicAddress property.
     */
    public List<Object> getAttributedElectronicAddressOrElectronicAddress() {
        if (attributedElectronicAddressOrElectronicAddress == null) {
            attributedElectronicAddressOrElectronicAddress = new ArrayList<>();
        }
        return this.attributedElectronicAddressOrElectronicAddress;
    }

    /**
     * Gets the value of the certificateDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CertificateDetailsType }
     *     
     */
    public CertificateDetailsType getCertificateDetails() {
        return certificateDetails;
    }

    /**
     * Sets the value of the certificateDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CertificateDetailsType }
     *     
     */
    public void setCertificateDetails(CertificateDetailsType value) {
        this.certificateDetails = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * @return
     *     possible object is
     *     {@link AnyType }
     *     
     */
    public AnyType getAny() {
        return any;
    }

    /**
     * Sets the value of the any property.
     * 
     * @param value
     *     allowed object is
     *     {@link AnyType }
     *     
     */
    public void setAny(AnyType value) {
        this.any = value;
    }

}
