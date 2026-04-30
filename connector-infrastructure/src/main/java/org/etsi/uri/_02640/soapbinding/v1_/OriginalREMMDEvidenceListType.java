
package org.etsi.uri._02640.soapbinding.v1_;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.etsi.uri._02640.v2.REMEvidenceType;


/**
 * &lt;p&gt;Java class for REMMDEvidenceListType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="REMMDEvidenceListType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence maxOccurs="unbounded"&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}SubmissionAcceptanceRejection" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}RelayREMMDAcceptanceRejection" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}RelayREMMDFailure" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}DeliveryNonDeliveryToRecipient" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}RetrievalNonRetrievalByRecipient" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}AcceptanceRejectionByRecipient" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}DownloadNonDownloadByRecipient" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}RelayToNonREMSystem" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}ReceivedFromNonREMSystem" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Id" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "submissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure"
})
@XmlSeeAlso({
    REMMDEvidenceListType.class
})
public class OriginalREMMDEvidenceListType {

    @XmlElementRefs({
        @XmlElementRef(name = "SubmissionAcceptanceRejection", namespace = "http://uri.etsi.org/02640/v2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "RelayREMMDAcceptanceRejection", namespace = "http://uri.etsi.org/02640/v2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "RelayREMMDFailure", namespace = "http://uri.etsi.org/02640/v2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "DeliveryNonDeliveryToRecipient", namespace = "http://uri.etsi.org/02640/v2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "RetrievalNonRetrievalByRecipient", namespace = "http://uri.etsi.org/02640/v2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "AcceptanceRejectionByRecipient", namespace = "http://uri.etsi.org/02640/v2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "DownloadNonDownloadByRecipient", namespace = "http://uri.etsi.org/02640/v2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "RelayToNonREMSystem", namespace = "http://uri.etsi.org/02640/v2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ReceivedFromNonREMSystem", namespace = "http://uri.etsi.org/02640/v2#", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<REMEvidenceType>> submissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure;
    @XmlAttribute(name = "Id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the submissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the submissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getSubmissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link REMEvidenceType }{@code >}
     * {@link JAXBElement }{@code <}{@link REMEvidenceType }{@code >}
     * {@link JAXBElement }{@code <}{@link REMEvidenceType }{@code >}
     * {@link JAXBElement }{@code <}{@link REMEvidenceType }{@code >}
     * {@link JAXBElement }{@code <}{@link REMEvidenceType }{@code >}
     * {@link JAXBElement }{@code <}{@link REMEvidenceType }{@code >}
     * {@link JAXBElement }{@code <}{@link REMEvidenceType }{@code >}
     * {@link JAXBElement }{@code <}{@link REMEvidenceType }{@code >}
     * {@link JAXBElement }{@code <}{@link REMEvidenceType }{@code >}
     * </p>
     * 
     * 
     * @return
     *     The value of the submissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure property.
     */
    public List<JAXBElement<REMEvidenceType>> getSubmissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure() {
        if (submissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure == null) {
            submissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure = new ArrayList<>();
        }
        return this.submissionAcceptanceRejectionAndRelayREMMDAcceptanceRejectionAndRelayREMMDFailure;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
