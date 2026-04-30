
package org.etsi.uri._02640.soapbinding.v1_;

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
 *         &lt;element name="From" type="{http://uri.etsi.org/02640/v2#}EntityDetailsType"/&gt;
 *         &lt;element name="Sender" type="{http://uri.etsi.org/02640/v2#}EntityDetailsType" minOccurs="0"/&gt;
 *         &lt;element name="ReplyTo" type="{http://uri.etsi.org/02640/v2#}EntityDetailsType" minOccurs="0"/&gt;
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
    "from",
    "sender",
    "replyTo"
})
@XmlRootElement(name = "Originators")
public class Originators {

    @XmlElement(name = "From", required = true)
    protected EntityDetailsType from;
    @XmlElement(name = "Sender")
    protected EntityDetailsType sender;
    @XmlElement(name = "ReplyTo")
    protected EntityDetailsType replyTo;

    /**
     * Gets the value of the from property.
     * 
     * @return
     *     possible object is
     *     {@link EntityDetailsType }
     *     
     */
    public EntityDetailsType getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityDetailsType }
     *     
     */
    public void setFrom(EntityDetailsType value) {
        this.from = value;
    }

    /**
     * Gets the value of the sender property.
     * 
     * @return
     *     possible object is
     *     {@link EntityDetailsType }
     *     
     */
    public EntityDetailsType getSender() {
        return sender;
    }

    /**
     * Sets the value of the sender property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityDetailsType }
     *     
     */
    public void setSender(EntityDetailsType value) {
        this.sender = value;
    }

    /**
     * Gets the value of the replyTo property.
     * 
     * @return
     *     possible object is
     *     {@link EntityDetailsType }
     *     
     */
    public EntityDetailsType getReplyTo() {
        return replyTo;
    }

    /**
     * Sets the value of the replyTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityDetailsType }
     *     
     */
    public void setReplyTo(EntityDetailsType value) {
        this.replyTo = value;
    }

}
