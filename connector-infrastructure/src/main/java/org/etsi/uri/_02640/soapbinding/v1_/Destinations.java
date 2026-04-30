
package org.etsi.uri._02640.soapbinding.v1_;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.etsi.uri._02640.v2.EntityDetailsType;


/**
 * &lt;p&gt;Java class for anonymous complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/soapbinding/v1#}Recipient"/&gt;
 *         &lt;element name="OtherRecipients"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="To" type="{http://uri.etsi.org/02640/v2#}EntityDetailsType" maxOccurs="unbounded"/&gt;
 *                   &lt;element name="Cc" type="{http://uri.etsi.org/02640/v2#}EntityDetailsType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "recipient",
    "otherRecipients"
})
@XmlRootElement(name = "Destinations")
public class Destinations {

    @XmlElement(name = "Recipient", required = true)
    protected EntityDetailsType recipient;
    @XmlElement(name = "OtherRecipients", required = true)
    protected Destinations.OtherRecipients otherRecipients;

    /**
     * Gets the value of the recipient property.
     * 
     * @return
     *     possible object is
     *     {@link EntityDetailsType }
     *     
     */
    public EntityDetailsType getRecipient() {
        return recipient;
    }

    /**
     * Sets the value of the recipient property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityDetailsType }
     *     
     */
    public void setRecipient(EntityDetailsType value) {
        this.recipient = value;
    }

    /**
     * Gets the value of the otherRecipients property.
     * 
     * @return
     *     possible object is
     *     {@link Destinations.OtherRecipients }
     *     
     */
    public Destinations.OtherRecipients getOtherRecipients() {
        return otherRecipients;
    }

    /**
     * Sets the value of the otherRecipients property.
     * 
     * @param value
     *     allowed object is
     *     {@link Destinations.OtherRecipients }
     *     
     */
    public void setOtherRecipients(Destinations.OtherRecipients value) {
        this.otherRecipients = value;
    }


    /**
     * &lt;p&gt;Java class for anonymous complex type&lt;/p&gt;.
     * 
     * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
     * 
     * &lt;pre&gt;{&#064;code
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="To" type="{http://uri.etsi.org/02640/v2#}EntityDetailsType" maxOccurs="unbounded"/&gt;
     *         &lt;element name="Cc" type="{http://uri.etsi.org/02640/v2#}EntityDetailsType" maxOccurs="unbounded" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * }&lt;/pre&gt;
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "to",
        "cc"
    })
    public static class OtherRecipients {

        @XmlElement(name = "To", required = true)
        protected List<EntityDetailsType> to;
        @XmlElement(name = "Cc")
        protected List<EntityDetailsType> cc;

        /**
         * Gets the value of the to property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the to property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getTo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link EntityDetailsType }
         * </p>
         * 
         * 
         * @return
         *     The value of the to property.
         */
        public List<EntityDetailsType> getTo() {
            if (to == null) {
                to = new ArrayList<>();
            }
            return this.to;
        }

        /**
         * Gets the value of the cc property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the cc property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getCc().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link EntityDetailsType }
         * </p>
         * 
         * 
         * @return
         *     The value of the cc property.
         */
        public List<EntityDetailsType> getCc() {
            if (cc == null) {
                cc = new ArrayList<>();
            }
            return this.cc;
        }

    }

}
