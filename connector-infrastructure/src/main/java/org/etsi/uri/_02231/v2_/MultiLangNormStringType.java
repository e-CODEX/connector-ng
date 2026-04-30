
package org.etsi.uri._02231.v2_;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for MultiLangNormStringType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="MultiLangNormStringType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://uri.etsi.org/02231/v2#&gt;NonEmptyNormalizedString"&gt;
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang use="required""/&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultiLangNormStringType", propOrder = {
    "value"
})
public class MultiLangNormStringType {

    @XmlValue
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String value;
    /**
     * Attempting to install the relevant ISO 2- and
     *                 3-letter
     *                 codes as the enumerated possible values is probably never
     *                 going to be a realistic possibility. See
     *                 RFC 3066 at
     *                 http://www.ietf.org/rfc/rfc3066.txt and the IANA registry
     *                 at
     *                 http://www.iana.org/assignments/lang-tag-apps.htm for
     *                 further
     *                 information.
     * 
     *                 The union allows for the 'un-declaration' of xml:lang
     *                 with
     *                 the empty string.
     * 
     */
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace", required = true)
    protected String lang;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Attempting to install the relevant ISO 2- and
     *                 3-letter
     *                 codes as the enumerated possible values is probably never
     *                 going to be a realistic possibility. See
     *                 RFC 3066 at
     *                 http://www.ietf.org/rfc/rfc3066.txt and the IANA registry
     *                 at
     *                 http://www.iana.org/assignments/lang-tag-apps.htm for
     *                 further
     *                 information.
     * 
     *                 The union allows for the 'un-declaration' of xml:lang
     *                 with
     *                 the empty string.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getLang()
     */
    public void setLang(String value) {
        this.lang = value;
    }

}
