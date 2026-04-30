
package org.etsi.uri._02640.v2;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for NamesPostalAddressListType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="NamesPostalAddressListType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}NamePostalAddress" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NamesPostalAddressListType", propOrder = {
    "namePostalAddress"
})
public class NamesPostalAddressListType {

    @XmlElement(name = "NamePostalAddress", required = true)
    protected List<NamePostalAddressType> namePostalAddress;

    /**
     * Gets the value of the namePostalAddress property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the namePostalAddress property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getNamePostalAddress().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NamePostalAddressType }
     * </p>
     * 
     * 
     * @return
     *     The value of the namePostalAddress property.
     */
    public List<NamePostalAddressType> getNamePostalAddress() {
        if (namePostalAddress == null) {
            namePostalAddress = new ArrayList<>();
        }
        return this.namePostalAddress;
    }

}
