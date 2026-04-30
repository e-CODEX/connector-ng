
package org.etsi.uri._01903.v1_3;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for UnsignedSignaturePropertiesType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="UnsignedSignaturePropertiesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice maxOccurs="unbounded"&gt;
 *         &lt;element name="CounterSignature" type="{http://uri.etsi.org/01903/v1.3.2#}CounterSignatureType"/&gt;
 *         &lt;element name="SignatureTimeStamp" type="{http://uri.etsi.org/01903/v1.3.2#}XAdESTimeStampType"/&gt;
 *         &lt;element name="CompleteCertificateRefs" type="{http://uri.etsi.org/01903/v1.3.2#}CompleteCertificateRefsType"/&gt;
 *         &lt;element name="CompleteRevocationRefs" type="{http://uri.etsi.org/01903/v1.3.2#}CompleteRevocationRefsType"/&gt;
 *         &lt;element name="AttributeCertificateRefs" type="{http://uri.etsi.org/01903/v1.3.2#}CompleteCertificateRefsType"/&gt;
 *         &lt;element name="AttributeRevocationRefs" type="{http://uri.etsi.org/01903/v1.3.2#}CompleteRevocationRefsType"/&gt;
 *         &lt;element name="SigAndRefsTimeStamp" type="{http://uri.etsi.org/01903/v1.3.2#}XAdESTimeStampType"/&gt;
 *         &lt;element name="RefsOnlyTimeStamp" type="{http://uri.etsi.org/01903/v1.3.2#}XAdESTimeStampType"/&gt;
 *         &lt;element name="CertificateValues" type="{http://uri.etsi.org/01903/v1.3.2#}CertificateValuesType"/&gt;
 *         &lt;element name="RevocationValues" type="{http://uri.etsi.org/01903/v1.3.2#}RevocationValuesType"/&gt;
 *         &lt;element name="AttrAuthoritiesCertValues" type="{http://uri.etsi.org/01903/v1.3.2#}CertificateValuesType"/&gt;
 *         &lt;element name="AttributeRevocationValues" type="{http://uri.etsi.org/01903/v1.3.2#}RevocationValuesType"/&gt;
 *         &lt;element name="ArchiveTimeStamp" type="{http://uri.etsi.org/01903/v1.3.2#}XAdESTimeStampType"/&gt;
 *         &lt;any namespace='##other'/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="Id" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnsignedSignaturePropertiesType", propOrder = {
    "counterSignatureOrSignatureTimeStampOrCompleteCertificateRefs"
})
public class UnsignedSignaturePropertiesType {

    @XmlElementRefs({
        @XmlElementRef(name = "CounterSignature", namespace = "http://uri.etsi.org/01903/v1.3.2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "SignatureTimeStamp", namespace = "http://uri.etsi.org/01903/v1.3.2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "CompleteCertificateRefs", namespace = "http://uri.etsi.org/01903/v1.3.2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "CompleteRevocationRefs", namespace = "http://uri.etsi.org/01903/v1.3.2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "AttributeCertificateRefs", namespace = "http://uri.etsi.org/01903/v1.3.2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "AttributeRevocationRefs", namespace = "http://uri.etsi.org/01903/v1.3.2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "SigAndRefsTimeStamp", namespace = "http://uri.etsi.org/01903/v1.3.2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "RefsOnlyTimeStamp", namespace = "http://uri.etsi.org/01903/v1.3.2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "CertificateValues", namespace = "http://uri.etsi.org/01903/v1.3.2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "RevocationValues", namespace = "http://uri.etsi.org/01903/v1.3.2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "AttrAuthoritiesCertValues", namespace = "http://uri.etsi.org/01903/v1.3.2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "AttributeRevocationValues", namespace = "http://uri.etsi.org/01903/v1.3.2#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "ArchiveTimeStamp", namespace = "http://uri.etsi.org/01903/v1.3.2#", type = JAXBElement.class, required = false)
    })
    @XmlAnyElement(lax = true)
    protected List<Object> counterSignatureOrSignatureTimeStampOrCompleteCertificateRefs;
    @XmlAttribute(name = "Id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the counterSignatureOrSignatureTimeStampOrCompleteCertificateRefs property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the counterSignatureOrSignatureTimeStampOrCompleteCertificateRefs property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getCounterSignatureOrSignatureTimeStampOrCompleteCertificateRefs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link CertificateValuesType }{@code >}
     * {@link JAXBElement }{@code <}{@link CertificateValuesType }{@code >}
     * {@link JAXBElement }{@code <}{@link CompleteCertificateRefsType }{@code >}
     * {@link JAXBElement }{@code <}{@link CompleteCertificateRefsType }{@code >}
     * {@link JAXBElement }{@code <}{@link CompleteRevocationRefsType }{@code >}
     * {@link JAXBElement }{@code <}{@link CompleteRevocationRefsType }{@code >}
     * {@link JAXBElement }{@code <}{@link CounterSignatureType }{@code >}
     * {@link JAXBElement }{@code <}{@link RevocationValuesType }{@code >}
     * {@link JAXBElement }{@code <}{@link RevocationValuesType }{@code >}
     * {@link JAXBElement }{@code <}{@link XAdESTimeStampType }{@code >}
     * {@link JAXBElement }{@code <}{@link XAdESTimeStampType }{@code >}
     * {@link JAXBElement }{@code <}{@link XAdESTimeStampType }{@code >}
     * {@link JAXBElement }{@code <}{@link XAdESTimeStampType }{@code >}
     * {@link Object }
     * </p>
     * 
     * 
     * @return
     *     The value of the counterSignatureOrSignatureTimeStampOrCompleteCertificateRefs property.
     */
    public List<Object> getCounterSignatureOrSignatureTimeStampOrCompleteCertificateRefs() {
        if (counterSignatureOrSignatureTimeStampOrCompleteCertificateRefs == null) {
            counterSignatureOrSignatureTimeStampOrCompleteCertificateRefs = new ArrayList<>();
        }
        return this.counterSignatureOrSignatureTimeStampOrCompleteCertificateRefs;
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
