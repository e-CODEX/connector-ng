
package org.etsi.uri._02640.v2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.etsi.uri._01903.v1_3.DigestAlgAndValueType;


/**
 * &lt;p&gt;Java class for CertIDAndSignatureType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CertIDAndSignatureType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IssuerSerial" type="{http://uri.etsi.org/01903/v1.3.2#}DigestAlgAndValueType"/&gt;
 *         &lt;element name="tbsCertificateDigestDetails" type="{http://uri.etsi.org/01903/v1.3.2#}DigestAlgAndValueType"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}CertSignatureDetails"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertIDAndSignatureType", propOrder = {
    "issuerSerial",
    "tbsCertificateDigestDetails",
    "certSignatureDetails"
})
public class CertIDAndSignatureType {

    @XmlElement(name = "IssuerSerial", required = true)
    protected DigestAlgAndValueType issuerSerial;
    @XmlElement(required = true)
    protected DigestAlgAndValueType tbsCertificateDigestDetails;
    @XmlElement(name = "CertSignatureDetails", required = true)
    protected CertSignatureDetailsType certSignatureDetails;

    /**
     * Gets the value of the issuerSerial property.
     * 
     * @return
     *     possible object is
     *     {@link DigestAlgAndValueType }
     *     
     */
    public DigestAlgAndValueType getIssuerSerial() {
        return issuerSerial;
    }

    /**
     * Sets the value of the issuerSerial property.
     * 
     * @param value
     *     allowed object is
     *     {@link DigestAlgAndValueType }
     *     
     */
    public void setIssuerSerial(DigestAlgAndValueType value) {
        this.issuerSerial = value;
    }

    /**
     * Gets the value of the tbsCertificateDigestDetails property.
     * 
     * @return
     *     possible object is
     *     {@link DigestAlgAndValueType }
     *     
     */
    public DigestAlgAndValueType getTbsCertificateDigestDetails() {
        return tbsCertificateDigestDetails;
    }

    /**
     * Sets the value of the tbsCertificateDigestDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link DigestAlgAndValueType }
     *     
     */
    public void setTbsCertificateDigestDetails(DigestAlgAndValueType value) {
        this.tbsCertificateDigestDetails = value;
    }

    /**
     * Gets the value of the certSignatureDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CertSignatureDetailsType }
     *     
     */
    public CertSignatureDetailsType getCertSignatureDetails() {
        return certSignatureDetails;
    }

    /**
     * Sets the value of the certSignatureDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CertSignatureDetailsType }
     *     
     */
    public void setCertSignatureDetails(CertSignatureDetailsType value) {
        this.certSignatureDetails = value;
    }

}
