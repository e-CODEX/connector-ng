
package org.etsi.uri._02231.v2_;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ServiceHistoryType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ServiceHistoryType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://uri.etsi.org/02231/v2#}ServiceHistoryInstance" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceHistoryType", propOrder = {
    "serviceHistoryInstance"
})
public class ServiceHistoryType {

    @XmlElement(name = "ServiceHistoryInstance")
    protected List<ServiceHistoryInstanceType> serviceHistoryInstance;

    /**
     * Gets the value of the serviceHistoryInstance property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the serviceHistoryInstance property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getServiceHistoryInstance().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServiceHistoryInstanceType }
     * </p>
     * 
     * 
     * @return
     *     The value of the serviceHistoryInstance property.
     */
    public List<ServiceHistoryInstanceType> getServiceHistoryInstance() {
        if (serviceHistoryInstance == null) {
            serviceHistoryInstance = new ArrayList<>();
        }
        return this.serviceHistoryInstance;
    }

}
