
package org.etsi.uri._01903.v1_3;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for SignedDataObjectPropertiesType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="SignedDataObjectPropertiesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DataObjectFormat" type="{http://uri.etsi.org/01903/v1.3.2#}DataObjectFormatType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="CommitmentTypeIndication" type="{http://uri.etsi.org/01903/v1.3.2#}CommitmentTypeIndicationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="AllDataObjectsTimeStamp" type="{http://uri.etsi.org/01903/v1.3.2#}XAdESTimeStampType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="IndividualDataObjectsTimeStamp" type="{http://uri.etsi.org/01903/v1.3.2#}XAdESTimeStampType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "SignedDataObjectPropertiesType", propOrder = {
    "dataObjectFormat",
    "commitmentTypeIndication",
    "allDataObjectsTimeStamp",
    "individualDataObjectsTimeStamp"
})
public class SignedDataObjectPropertiesType {

    @XmlElement(name = "DataObjectFormat")
    protected List<DataObjectFormatType> dataObjectFormat;
    @XmlElement(name = "CommitmentTypeIndication")
    protected List<CommitmentTypeIndicationType> commitmentTypeIndication;
    @XmlElement(name = "AllDataObjectsTimeStamp")
    protected List<XAdESTimeStampType> allDataObjectsTimeStamp;
    @XmlElement(name = "IndividualDataObjectsTimeStamp")
    protected List<XAdESTimeStampType> individualDataObjectsTimeStamp;
    @XmlAttribute(name = "Id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the dataObjectFormat property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the dataObjectFormat property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getDataObjectFormat().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataObjectFormatType }
     * </p>
     * 
     * 
     * @return
     *     The value of the dataObjectFormat property.
     */
    public List<DataObjectFormatType> getDataObjectFormat() {
        if (dataObjectFormat == null) {
            dataObjectFormat = new ArrayList<>();
        }
        return this.dataObjectFormat;
    }

    /**
     * Gets the value of the commitmentTypeIndication property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the commitmentTypeIndication property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getCommitmentTypeIndication().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CommitmentTypeIndicationType }
     * </p>
     * 
     * 
     * @return
     *     The value of the commitmentTypeIndication property.
     */
    public List<CommitmentTypeIndicationType> getCommitmentTypeIndication() {
        if (commitmentTypeIndication == null) {
            commitmentTypeIndication = new ArrayList<>();
        }
        return this.commitmentTypeIndication;
    }

    /**
     * Gets the value of the allDataObjectsTimeStamp property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the allDataObjectsTimeStamp property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getAllDataObjectsTimeStamp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XAdESTimeStampType }
     * </p>
     * 
     * 
     * @return
     *     The value of the allDataObjectsTimeStamp property.
     */
    public List<XAdESTimeStampType> getAllDataObjectsTimeStamp() {
        if (allDataObjectsTimeStamp == null) {
            allDataObjectsTimeStamp = new ArrayList<>();
        }
        return this.allDataObjectsTimeStamp;
    }

    /**
     * Gets the value of the individualDataObjectsTimeStamp property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the individualDataObjectsTimeStamp property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getIndividualDataObjectsTimeStamp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XAdESTimeStampType }
     * </p>
     * 
     * 
     * @return
     *     The value of the individualDataObjectsTimeStamp property.
     */
    public List<XAdESTimeStampType> getIndividualDataObjectsTimeStamp() {
        if (individualDataObjectsTimeStamp == null) {
            individualDataObjectsTimeStamp = new ArrayList<>();
        }
        return this.individualDataObjectsTimeStamp;
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
