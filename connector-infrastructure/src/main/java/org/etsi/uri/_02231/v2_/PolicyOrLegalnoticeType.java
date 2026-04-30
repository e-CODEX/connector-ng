
package org.etsi.uri._02231.v2_;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PolicyOrLegalnoticeType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PolicyOrLegalnoticeType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="TSLPolicy" type="{http://uri.etsi.org/02231/v2#}NonEmptyMultiLangURIType" maxOccurs="unbounded"/&gt;
 *         &lt;element name="TSLLegalNotice" type="{http://uri.etsi.org/02231/v2#}MultiLangStringType" maxOccurs="unbounded"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolicyOrLegalnoticeType", propOrder = {
    "tslPolicy",
    "tslLegalNotice"
})
public class PolicyOrLegalnoticeType {

    @XmlElement(name = "TSLPolicy")
    protected List<NonEmptyMultiLangURIType> tslPolicy;
    @XmlElement(name = "TSLLegalNotice")
    protected List<MultiLangStringType> tslLegalNotice;

    /**
     * Gets the value of the tslPolicy property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the tslPolicy property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getTSLPolicy().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NonEmptyMultiLangURIType }
     * </p>
     * 
     * 
     * @return
     *     The value of the tslPolicy property.
     */
    public List<NonEmptyMultiLangURIType> getTSLPolicy() {
        if (tslPolicy == null) {
            tslPolicy = new ArrayList<>();
        }
        return this.tslPolicy;
    }

    /**
     * Gets the value of the tslLegalNotice property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the tslLegalNotice property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getTSLLegalNotice().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MultiLangStringType }
     * </p>
     * 
     * 
     * @return
     *     The value of the tslLegalNotice property.
     */
    public List<MultiLangStringType> getTSLLegalNotice() {
        if (tslLegalNotice == null) {
            tslLegalNotice = new ArrayList<>();
        }
        return this.tslLegalNotice;
    }

}
