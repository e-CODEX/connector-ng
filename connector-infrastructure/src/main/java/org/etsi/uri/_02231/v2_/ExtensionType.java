
package org.etsi.uri._02231.v2_;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ExtensionType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ExtensionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://uri.etsi.org/02231/v2#}AnyType"&gt;
 *       &lt;attribute name="Critical" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
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

    @XmlAttribute(name = "Critical", required = true)
    protected boolean critical;

    /**
     * Gets the value of the critical property.
     * 
     */
    public boolean isCritical() {
        return critical;
    }

    /**
     * Sets the value of the critical property.
     * 
     */
    public void setCritical(boolean value) {
        this.critical = value;
    }

}
