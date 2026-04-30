
package org.etsi.uri._02640.v2;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import oasis.names.tc.saml._2_0.assertion.AssertionType;
import org.etsi.uri._01903.v1_3.AnyType;


/**
 * &lt;p&gt;Java class for AuthenticationDetailsType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AuthenticationDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;sequence&gt;
 *             &lt;element name="AuthenticationTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *             &lt;element name="AuthenticationMethod" type="{http://www.w3.org/2001/XMLSchema}anyURI"/&gt;
 *           &lt;/sequence&gt;
 *           &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}Assertion"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="AdditionalDetails" type="{http://uri.etsi.org/01903/v1.3.2#}AnyType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthenticationDetailsType", propOrder = {
    "authenticationTime",
    "authenticationMethod",
    "assertion",
    "additionalDetails"
})
public class AuthenticationDetailsType {

    @XmlElement(name = "AuthenticationTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar authenticationTime;
    @XmlElement(name = "AuthenticationMethod")
    @XmlSchemaType(name = "anyURI")
    protected String authenticationMethod;
    @XmlElement(name = "Assertion", namespace = "urn:oasis:names:tc:SAML:2.0:assertion")
    protected AssertionType assertion;
    @XmlElement(name = "AdditionalDetails")
    protected AnyType additionalDetails;

    /**
     * Gets the value of the authenticationTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAuthenticationTime() {
        return authenticationTime;
    }

    /**
     * Sets the value of the authenticationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAuthenticationTime(XMLGregorianCalendar value) {
        this.authenticationTime = value;
    }

    /**
     * Gets the value of the authenticationMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    /**
     * Sets the value of the authenticationMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthenticationMethod(String value) {
        this.authenticationMethod = value;
    }

    /**
     * Gets the value of the assertion property.
     * 
     * @return
     *     possible object is
     *     {@link AssertionType }
     *     
     */
    public AssertionType getAssertion() {
        return assertion;
    }

    /**
     * Sets the value of the assertion property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssertionType }
     *     
     */
    public void setAssertion(AssertionType value) {
        this.assertion = value;
    }

    /**
     * Gets the value of the additionalDetails property.
     * 
     * @return
     *     possible object is
     *     {@link AnyType }
     *     
     */
    public AnyType getAdditionalDetails() {
        return additionalDetails;
    }

    /**
     * Sets the value of the additionalDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link AnyType }
     *     
     */
    public void setAdditionalDetails(AnyType value) {
        this.additionalDetails = value;
    }

}
