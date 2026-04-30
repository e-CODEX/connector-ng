
package oasis.names.tc.saml._2_0.assertion;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for SubjectType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="SubjectType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;sequence&gt;
 *           &lt;choice&gt;
 *             &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}BaseID"/&gt;
 *             &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}NameID"/&gt;
 *             &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}EncryptedID"/&gt;
 *           &lt;/choice&gt;
 *           &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}SubjectConfirmation" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}SubjectConfirmation" maxOccurs="unbounded"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubjectType", propOrder = {
    "content"
})
public class SubjectType {

    /**
     * Gets the rest of the content model. 
     * 
     * &lt;p&gt;
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "SubjectConfirmation" is used by two different parts of a schema. See: 
     * line 82 of file:/home/lisa/IdeaProjects/connector/domibusConnectorEvidencesToolkit/src/main/xsd/saml-schema-assertion-2.0.xsd
     * line 80 of file:/home/lisa/IdeaProjects/connector/domibusConnectorEvidencesToolkit/src/main/xsd/saml-schema-assertion-2.0.xsd
     * &lt;p&gt;
     * To get rid of this property, apply a property customization to one 
     * of both of the following declarations to change their names:
     * 
     */
    @XmlElementRefs({
        @XmlElementRef(name = "BaseID", namespace = "urn:oasis:names:tc:SAML:2.0:assertion", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "NameID", namespace = "urn:oasis:names:tc:SAML:2.0:assertion", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "EncryptedID", namespace = "urn:oasis:names:tc:SAML:2.0:assertion", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "SubjectConfirmation", namespace = "urn:oasis:names:tc:SAML:2.0:assertion", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> content;

    /**
     * Gets the rest of the content model. 
     * 
     * &lt;p&gt;
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "SubjectConfirmation" is used by two different parts of a schema. See: 
     * line 82 of file:/home/lisa/IdeaProjects/connector/domibusConnectorEvidencesToolkit/src/main/xsd/saml-schema-assertion-2.0.xsd
     * line 80 of file:/home/lisa/IdeaProjects/connector/domibusConnectorEvidencesToolkit/src/main/xsd/saml-schema-assertion-2.0.xsd
     * &lt;p&gt;
     * To get rid of this property, apply a property customization to one 
     * of both of the following declarations to change their names:
     * 
     * Gets the value of the content property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the content property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link BaseIDAbstractType }{@code >}
     * {@link JAXBElement }{@code <}{@link EncryptedElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link NameIDType }{@code >}
     * {@link JAXBElement }{@code <}{@link SubjectConfirmationType }{@code >}
     * </p>
     * 
     * 
     * @return
     *     The value of the content property.
     */
    public List<JAXBElement<?>> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

}
