
package org.etsi.uri._02640.soapbinding.v1_;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{http://uri.etsi.org/02640/soapbinding/v1#}DeliveryConstraints"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/soapbinding/v1#}Originators"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/soapbinding/v1#}Destinations"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/soapbinding/v1#}MsgIdentification"/&gt;
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
    "deliveryConstraints",
    "originators",
    "destinations",
    "msgIdentification"
})
@XmlRootElement(name = "MsgMetaData")
public class MsgMetaData {

    /**
     * Message time instants
     * 
     */
    @XmlElement(name = "DeliveryConstraints", required = true)
    protected DeliveryConstraints deliveryConstraints;
    @XmlElement(name = "Originators", required = true)
    protected Originators originators;
    @XmlElement(name = "Destinations", required = true)
    protected Destinations destinations;
    @XmlElement(name = "MsgIdentification", required = true)
    protected MsgIdentification msgIdentification;

    /**
     * Message time instants
     * 
     * @return
     *     possible object is
     *     {@link DeliveryConstraints }
     *     
     */
    public DeliveryConstraints getDeliveryConstraints() {
        return deliveryConstraints;
    }

    /**
     * Sets the value of the deliveryConstraints property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeliveryConstraints }
     *     
     * @see #getDeliveryConstraints()
     */
    public void setDeliveryConstraints(DeliveryConstraints value) {
        this.deliveryConstraints = value;
    }

    /**
     * Gets the value of the originators property.
     * 
     * @return
     *     possible object is
     *     {@link Originators }
     *     
     */
    public Originators getOriginators() {
        return originators;
    }

    /**
     * Sets the value of the originators property.
     * 
     * @param value
     *     allowed object is
     *     {@link Originators }
     *     
     */
    public void setOriginators(Originators value) {
        this.originators = value;
    }

    /**
     * Gets the value of the destinations property.
     * 
     * @return
     *     possible object is
     *     {@link Destinations }
     *     
     */
    public Destinations getDestinations() {
        return destinations;
    }

    /**
     * Sets the value of the destinations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Destinations }
     *     
     */
    public void setDestinations(Destinations value) {
        this.destinations = value;
    }

    /**
     * Gets the value of the msgIdentification property.
     * 
     * @return
     *     possible object is
     *     {@link MsgIdentification }
     *     
     */
    public MsgIdentification getMsgIdentification() {
        return msgIdentification;
    }

    /**
     * Sets the value of the msgIdentification property.
     * 
     * @param value
     *     allowed object is
     *     {@link MsgIdentification }
     *     
     */
    public void setMsgIdentification(MsgIdentification value) {
        this.msgIdentification = value;
    }

}
