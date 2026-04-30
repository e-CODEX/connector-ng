
package org.etsi.uri._02640.soapbinding.v1_;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import org.etsi.uri._02640.v2.EntityDetailsType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.etsi.uri._02640.soapbinding.v1_ package. 
 * <p>An ObjectFactory allows you to programmatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _Recipient_QNAME = new QName("http://uri.etsi.org/02640/soapbinding/v1#", "Recipient");
    private static final QName _Attachment_QNAME = new QName("http://uri.etsi.org/02640/soapbinding/v1#", "Attachment");
    private static final QName _OriginalMsg_QNAME = new QName("http://uri.etsi.org/02640/soapbinding/v1#", "OriginalMsg");
    private static final QName _REMDispatch_QNAME = new QName("http://uri.etsi.org/02640/soapbinding/v1#", "REMDispatch");
    private static final QName _REMMDMessage_QNAME = new QName("http://uri.etsi.org/02640/soapbinding/v1#", "REMMDMessage");
    private static final QName _REMMDEvidenceList_QNAME = new QName("http://uri.etsi.org/02640/soapbinding/v1#", "REMMDEvidenceList");
    private static final QName _REMEvidenceList_QNAME = new QName("http://uri.etsi.org/02640/soapbinding/v1#", "REMEvidenceList");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.etsi.uri._02640.soapbinding.v1_
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Destinations }
     * 
     * @return
     *     the new instance of {@link Destinations }
     */
    public Destinations createDestinations() {
        return new Destinations();
    }

    /**
     * Create an instance of {@link NormalizedMsg }
     * 
     * @return
     *     the new instance of {@link NormalizedMsg }
     */
    public NormalizedMsg createNormalizedMsg() {
        return new NormalizedMsg();
    }

    /**
     * Create an instance of {@link DeliveryConstraints }
     * 
     * @return
     *     the new instance of {@link DeliveryConstraints }
     */
    public DeliveryConstraints createDeliveryConstraints() {
        return new DeliveryConstraints();
    }

    /**
     * Create an instance of {@link Originators }
     * 
     * @return
     *     the new instance of {@link Originators }
     */
    public Originators createOriginators() {
        return new Originators();
    }

    /**
     * Create an instance of {@link Destinations.OtherRecipients }
     * 
     * @return
     *     the new instance of {@link Destinations.OtherRecipients }
     */
    public Destinations.OtherRecipients createDestinationsOtherRecipients() {
        return new Destinations.OtherRecipients();
    }

    /**
     * Create an instance of {@link MsgIdentification }
     * 
     * @return
     *     the new instance of {@link MsgIdentification }
     */
    public MsgIdentification createMsgIdentification() {
        return new MsgIdentification();
    }

    /**
     * Create an instance of {@link MsgMetaData }
     * 
     * @return
     *     the new instance of {@link MsgMetaData }
     */
    public MsgMetaData createMsgMetaData() {
        return new MsgMetaData();
    }

    /**
     * Create an instance of {@link AttachmentType }
     * 
     * @return
     *     the new instance of {@link AttachmentType }
     */
    public AttachmentType createAttachmentType() {
        return new AttachmentType();
    }

    /**
     * Create an instance of {@link Informational }
     * 
     * @return
     *     the new instance of {@link Informational }
     */
    public Informational createInformational() {
        return new Informational();
    }

    /**
     * Create an instance of {@link KeywordType }
     * 
     * @return
     *     the new instance of {@link KeywordType }
     */
    public KeywordType createKeywordType() {
        return new KeywordType();
    }

    /**
     * Create an instance of {@link NormalizedMsg.Text }
     * 
     * @return
     *     the new instance of {@link NormalizedMsg.Text }
     */
    public NormalizedMsg.Text createNormalizedMsgText() {
        return new NormalizedMsg.Text();
    }

    /**
     * Create an instance of {@link OriginalMsgType }
     * 
     * @return
     *     the new instance of {@link OriginalMsgType }
     */
    public OriginalMsgType createOriginalMsgType() {
        return new OriginalMsgType();
    }

    /**
     * Create an instance of {@link REMDispatchType }
     * 
     * @return
     *     the new instance of {@link REMDispatchType }
     */
    public REMDispatchType createREMDispatchType() {
        return new REMDispatchType();
    }

    /**
     * Create an instance of {@link REMMDMessageType }
     * 
     * @return
     *     the new instance of {@link REMMDMessageType }
     */
    public REMMDMessageType createREMMDMessageType() {
        return new REMMDMessageType();
    }

    /**
     * Create an instance of {@link REMMDEvidenceListType }
     * 
     * @return
     *     the new instance of {@link REMMDEvidenceListType }
     */
    public REMMDEvidenceListType createREMMDEvidenceListType() {
        return new REMMDEvidenceListType();
    }

    /**
     * Create an instance of {@link OriginalREMMDEvidenceListType }
     * 
     * @return
     *     the new instance of {@link OriginalREMMDEvidenceListType }
     */
    public OriginalREMMDEvidenceListType createOriginalREMMDEvidenceListType() {
        return new OriginalREMMDEvidenceListType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EntityDetailsType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EntityDetailsType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02640/soapbinding/v1#", name = "Recipient")
    public JAXBElement<EntityDetailsType> createRecipient(EntityDetailsType value) {
        return new JAXBElement<>(_Recipient_QNAME, EntityDetailsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttachmentType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AttachmentType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02640/soapbinding/v1#", name = "Attachment")
    public JAXBElement<AttachmentType> createAttachment(AttachmentType value) {
        return new JAXBElement<>(_Attachment_QNAME, AttachmentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OriginalMsgType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OriginalMsgType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02640/soapbinding/v1#", name = "OriginalMsg")
    public JAXBElement<OriginalMsgType> createOriginalMsg(OriginalMsgType value) {
        return new JAXBElement<>(_OriginalMsg_QNAME, OriginalMsgType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link REMDispatchType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link REMDispatchType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02640/soapbinding/v1#", name = "REMDispatch")
    public JAXBElement<REMDispatchType> createREMDispatch(REMDispatchType value) {
        return new JAXBElement<>(_REMDispatch_QNAME, REMDispatchType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link REMMDMessageType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link REMMDMessageType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02640/soapbinding/v1#", name = "REMMDMessage")
    public JAXBElement<REMMDMessageType> createREMMDMessage(REMMDMessageType value) {
        return new JAXBElement<>(_REMMDMessage_QNAME, REMMDMessageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link REMMDEvidenceListType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link REMMDEvidenceListType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02640/soapbinding/v1#", name = "REMMDEvidenceList")
    public JAXBElement<REMMDEvidenceListType> createREMMDEvidenceList(REMMDEvidenceListType value) {
        return new JAXBElement<>(_REMMDEvidenceList_QNAME, REMMDEvidenceListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link REMMDEvidenceListType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link REMMDEvidenceListType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02640/soapbinding/v1#", name = "REMEvidenceList")
    public JAXBElement<REMMDEvidenceListType> createREMEvidenceList(REMMDEvidenceListType value) {
        return new JAXBElement<>(_REMEvidenceList_QNAME, REMMDEvidenceListType.class, null, value);
    }

}
