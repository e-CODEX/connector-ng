
package org.etsi.uri._02640.v2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for RecipientsDelegatesType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="RecipientsDelegatesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence maxOccurs="unbounded"&gt;
 *         &lt;element name="DelegateDetails" type="{http://uri.etsi.org/02640/v2#}EntityDetailsType"/&gt;
 *         &lt;element name="DelegatingRecipients" type="{http://uri.etsi.org/02640/v2#}ListOfIntegers"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecipientsDelegatesType", propOrder = {
    "delegateDetailsAndDelegatingRecipients"
})
public class RecipientsDelegatesType {

    @XmlElementRefs({
        @XmlElementRef(name = "DelegateDetails", namespace = "http://uri.etsi.org/02640/v2#", type = JAXBElement.class),
        @XmlElementRef(name = "DelegatingRecipients", namespace = "http://uri.etsi.org/02640/v2#", type = JAXBElement.class)
    })
    protected List<JAXBElement<?>> delegateDetailsAndDelegatingRecipients;

    /**
     * Gets the value of the delegateDetailsAndDelegatingRecipients property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the delegateDetailsAndDelegatingRecipients property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getDelegateDetailsAndDelegatingRecipients().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link List }{@code <}{@link BigInteger }{@code >}{@code >}
     * {@link JAXBElement }{@code <}{@link EntityDetailsType }{@code >}
     * </p>
     * 
     * 
     * @return
     *     The value of the delegateDetailsAndDelegatingRecipients property.
     */
    public List<JAXBElement<?>> getDelegateDetailsAndDelegatingRecipients() {
        if (delegateDetailsAndDelegatingRecipients == null) {
            delegateDetailsAndDelegatingRecipients = new ArrayList<>();
        }
        return this.delegateDetailsAndDelegatingRecipients;
    }

}
