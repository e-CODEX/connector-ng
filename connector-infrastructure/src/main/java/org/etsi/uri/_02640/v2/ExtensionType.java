
package org.etsi.uri._02640.v2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import org.etsi.uri._01903.v1_3.AnyType;


/**
 * &lt;p&gt;Java class for ExtensionType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ExtensionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://uri.etsi.org/01903/v1.3.2#}AnyType"&gt;
 *       &lt;attribute name="isCritical" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;anyAttribute/&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtensionType")
public class ExtensionType
    extends AnyType
{

    @XmlAttribute(name = "isCritical")
    protected Boolean isCritical;

    /**
     * Gets the value of the isCritical property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsCritical() {
        return isCritical;
    }

    /**
     * Sets the value of the isCritical property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsCritical(Boolean value) {
        this.isCritical = value;
    }

}
