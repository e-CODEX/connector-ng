
package org.etsi.uri._02640.soapbinding.v1_;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2000._09.xmldsig_.SignatureType;


/**
 * &lt;p&gt;Java class for REMDispatchType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="REMDispatchType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/soapbinding/v1#}MsgMetaData"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/soapbinding/v1#}OriginalMsg"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/soapbinding/v1#}NormalizedMsg" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/soapbinding/v1#}REMMDEvidenceList" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Signature" minOccurs="0"/&gt;
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
@XmlType(name = "REMDispatchType", propOrder = {
    "msgMetaData",
    "originalMsg",
    "normalizedMsg",
    "remmdEvidenceList",
    "signature"
})
public class REMDispatchType {

    @XmlElement(name = "MsgMetaData", required = true)
    protected MsgMetaData msgMetaData;
    @XmlElement(name = "OriginalMsg", required = true)
    protected OriginalMsgType originalMsg;
    @XmlElement(name = "NormalizedMsg")
    protected NormalizedMsg normalizedMsg;
    @XmlElement(name = "REMMDEvidenceList")
    protected REMMDEvidenceListType remmdEvidenceList;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected SignatureType signature;
    @XmlAttribute(name = "Id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the msgMetaData property.
     * 
     * @return
     *     possible object is
     *     {@link MsgMetaData }
     *     
     */
    public MsgMetaData getMsgMetaData() {
        return msgMetaData;
    }

    /**
     * Sets the value of the msgMetaData property.
     * 
     * @param value
     *     allowed object is
     *     {@link MsgMetaData }
     *     
     */
    public void setMsgMetaData(MsgMetaData value) {
        this.msgMetaData = value;
    }

    /**
     * Gets the value of the originalMsg property.
     * 
     * @return
     *     possible object is
     *     {@link OriginalMsgType }
     *     
     */
    public OriginalMsgType getOriginalMsg() {
        return originalMsg;
    }

    /**
     * Sets the value of the originalMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link OriginalMsgType }
     *     
     */
    public void setOriginalMsg(OriginalMsgType value) {
        this.originalMsg = value;
    }

    /**
     * Gets the value of the normalizedMsg property.
     * 
     * @return
     *     possible object is
     *     {@link NormalizedMsg }
     *     
     */
    public NormalizedMsg getNormalizedMsg() {
        return normalizedMsg;
    }

    /**
     * Sets the value of the normalizedMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link NormalizedMsg }
     *     
     */
    public void setNormalizedMsg(NormalizedMsg value) {
        this.normalizedMsg = value;
    }

    /**
     * Gets the value of the remmdEvidenceList property.
     * 
     * @return
     *     possible object is
     *     {@link REMMDEvidenceListType }
     *     
     */
    public REMMDEvidenceListType getREMMDEvidenceList() {
        return remmdEvidenceList;
    }

    /**
     * Sets the value of the remmdEvidenceList property.
     * 
     * @param value
     *     allowed object is
     *     {@link REMMDEvidenceListType }
     *     
     */
    public void setREMMDEvidenceList(REMMDEvidenceListType value) {
        this.remmdEvidenceList = value;
    }

    /**
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     {@link SignatureType }
     *     
     */
    public SignatureType getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureType }
     *     
     */
    public void setSignature(SignatureType value) {
        this.signature = value;
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
