
package org.etsi.uri._02231.v2_;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AdditionalInformationType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AdditionalInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice maxOccurs="unbounded"&gt;
 *         &lt;element name="TextualInformation" type="{http://uri.etsi.org/02231/v2#}MultiLangStringType"/&gt;
 *         &lt;element name="OtherInformation" type="{http://uri.etsi.org/02231/v2#}AnyType"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdditionalInformationType", propOrder = {
    "textualInformationOrOtherInformation"
})
public class AdditionalInformationType {

    @XmlElements({
        @XmlElement(name = "TextualInformation", type = MultiLangStringType.class),
        @XmlElement(name = "OtherInformation", type = AnyType.class)
    })
    protected List<Object> textualInformationOrOtherInformation;

    /**
     * Gets the value of the textualInformationOrOtherInformation property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the textualInformationOrOtherInformation property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getTextualInformationOrOtherInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AnyType }
     * {@link MultiLangStringType }
     * </p>
     * 
     * 
     * @return
     *     The value of the textualInformationOrOtherInformation property.
     */
    public List<Object> getTextualInformationOrOtherInformation() {
        if (textualInformationOrOtherInformation == null) {
            textualInformationOrOtherInformation = new ArrayList<>();
        }
        return this.textualInformationOrOtherInformation;
    }

}
