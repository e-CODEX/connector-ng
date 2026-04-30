
package oasis.names.tc.saml._2_0.assertion;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AttributeStatementType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AttributeStatementType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:oasis:names:tc:SAML:2.0:assertion}StatementAbstractType"&gt;
 *       &lt;choice maxOccurs="unbounded"&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}Attribute"/&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}EncryptedAttribute"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributeStatementType", propOrder = {
    "attributeOrEncryptedAttribute"
})
public class AttributeStatementType
    extends StatementAbstractType
{

    @XmlElements({
        @XmlElement(name = "Attribute", type = AttributeType.class),
        @XmlElement(name = "EncryptedAttribute", type = EncryptedElementType.class)
    })
    protected List<Object> attributeOrEncryptedAttribute;

    /**
     * Gets the value of the attributeOrEncryptedAttribute property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the attributeOrEncryptedAttribute property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getAttributeOrEncryptedAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttributeType }
     * {@link EncryptedElementType }
     * </p>
     * 
     * 
     * @return
     *     The value of the attributeOrEncryptedAttribute property.
     */
    public List<Object> getAttributeOrEncryptedAttribute() {
        if (attributeOrEncryptedAttribute == null) {
            attributeOrEncryptedAttribute = new ArrayList<>();
        }
        return this.attributeOrEncryptedAttribute;
    }

}
