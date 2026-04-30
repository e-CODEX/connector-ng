
package org.etsi.uri._02640.soapbinding.v1_;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import org.etsi.uri._01903.v1_3.AnyType;


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
 *         &lt;element name="Origin" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="InitialSend" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="ObsoleteAfter" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
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
@XmlType(name = "", propOrder = {
    "origin",
    "initialSend",
    "obsoleteAfter",
    "any"
})
@XmlRootElement(name = "DeliveryConstraints")
public class DeliveryConstraints {

    @XmlElement(name = "Origin")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar origin;
    @XmlElement(name = "InitialSend", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar initialSend;
    @XmlElement(name = "ObsoleteAfter")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar obsoleteAfter;
    @XmlElement(name = "Any", namespace = "http://uri.etsi.org/01903/v1.3.2#")
    protected AnyType any;

    /**
     * Gets the value of the origin property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOrigin() {
        return origin;
    }

    /**
     * Sets the value of the origin property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOrigin(XMLGregorianCalendar value) {
        this.origin = value;
    }

    /**
     * Gets the value of the initialSend property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getInitialSend() {
        return initialSend;
    }

    /**
     * Sets the value of the initialSend property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInitialSend(XMLGregorianCalendar value) {
        this.initialSend = value;
    }

    /**
     * Gets the value of the obsoleteAfter property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getObsoleteAfter() {
        return obsoleteAfter;
    }

    /**
     * Sets the value of the obsoleteAfter property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setObsoleteAfter(XMLGregorianCalendar value) {
        this.obsoleteAfter = value;
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
