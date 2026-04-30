
package org.etsi.uri._02231.v2_;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.etsi.uri._02231.v2_ package. 
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

    private static final QName _PostalAddresses_QNAME = new QName("http://uri.etsi.org/02231/v2#", "PostalAddresses");
    private static final QName _PostalAddress_QNAME = new QName("http://uri.etsi.org/02231/v2#", "PostalAddress");
    private static final QName _ElectronicAddress_QNAME = new QName("http://uri.etsi.org/02231/v2#", "ElectronicAddress");
    private static final QName _Extension_QNAME = new QName("http://uri.etsi.org/02231/v2#", "Extension");
    private static final QName _TrustServiceStatusList_QNAME = new QName("http://uri.etsi.org/02231/v2#", "TrustServiceStatusList");
    private static final QName _TrustServiceProviderList_QNAME = new QName("http://uri.etsi.org/02231/v2#", "TrustServiceProviderList");
    private static final QName _SchemeInformation_QNAME = new QName("http://uri.etsi.org/02231/v2#", "SchemeInformation");
    private static final QName _TSLType_QNAME = new QName("http://uri.etsi.org/02231/v2#", "TSLType");
    private static final QName _SchemeOperatorName_QNAME = new QName("http://uri.etsi.org/02231/v2#", "SchemeOperatorName");
    private static final QName _SchemeName_QNAME = new QName("http://uri.etsi.org/02231/v2#", "SchemeName");
    private static final QName _SchemeInformationURI_QNAME = new QName("http://uri.etsi.org/02231/v2#", "SchemeInformationURI");
    private static final QName _SchemeTypeCommunityRules_QNAME = new QName("http://uri.etsi.org/02231/v2#", "SchemeTypeCommunityRules");
    private static final QName _SchemeTerritory_QNAME = new QName("http://uri.etsi.org/02231/v2#", "SchemeTerritory");
    private static final QName _PolicyOrLegalNotice_QNAME = new QName("http://uri.etsi.org/02231/v2#", "PolicyOrLegalNotice");
    private static final QName _NextUpdate_QNAME = new QName("http://uri.etsi.org/02231/v2#", "NextUpdate");
    private static final QName _PointersToOtherTSL_QNAME = new QName("http://uri.etsi.org/02231/v2#", "PointersToOtherTSL");
    private static final QName _OtherTSLPointer_QNAME = new QName("http://uri.etsi.org/02231/v2#", "OtherTSLPointer");
    private static final QName _ServiceDigitalIdentities_QNAME = new QName("http://uri.etsi.org/02231/v2#", "ServiceDigitalIdentities");
    private static final QName _AdditionalInformation_QNAME = new QName("http://uri.etsi.org/02231/v2#", "AdditionalInformation");
    private static final QName _DistributionPoints_QNAME = new QName("http://uri.etsi.org/02231/v2#", "DistributionPoints");
    private static final QName _TrustServiceProvider_QNAME = new QName("http://uri.etsi.org/02231/v2#", "TrustServiceProvider");
    private static final QName _TSPInformation_QNAME = new QName("http://uri.etsi.org/02231/v2#", "TSPInformation");
    private static final QName _TSPServices_QNAME = new QName("http://uri.etsi.org/02231/v2#", "TSPServices");
    private static final QName _TSPService_QNAME = new QName("http://uri.etsi.org/02231/v2#", "TSPService");
    private static final QName _ServiceInformation_QNAME = new QName("http://uri.etsi.org/02231/v2#", "ServiceInformation");
    private static final QName _ServiceStatus_QNAME = new QName("http://uri.etsi.org/02231/v2#", "ServiceStatus");
    private static final QName _ServiceSupplyPoints_QNAME = new QName("http://uri.etsi.org/02231/v2#", "ServiceSupplyPoints");
    private static final QName _ServiceTypeIdentifier_QNAME = new QName("http://uri.etsi.org/02231/v2#", "ServiceTypeIdentifier");
    private static final QName _ServiceDigitalIdentity_QNAME = new QName("http://uri.etsi.org/02231/v2#", "ServiceDigitalIdentity");
    private static final QName _ServiceHistory_QNAME = new QName("http://uri.etsi.org/02231/v2#", "ServiceHistory");
    private static final QName _ServiceHistoryInstance_QNAME = new QName("http://uri.etsi.org/02231/v2#", "ServiceHistoryInstance");
    private static final QName _ExpiredCertsRevocationInfo_QNAME = new QName("http://uri.etsi.org/02231/v2#", "ExpiredCertsRevocationInfo");
    private static final QName _AdditionalServiceInformation_QNAME = new QName("http://uri.etsi.org/02231/v2#", "AdditionalServiceInformation");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.etsi.uri._02231.v2_
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PostalAddressListType }
     * 
     * @return
     *     the new instance of {@link PostalAddressListType }
     */
    public PostalAddressListType createPostalAddressListType() {
        return new PostalAddressListType();
    }

    /**
     * Create an instance of {@link PostalAddressType }
     * 
     * @return
     *     the new instance of {@link PostalAddressType }
     */
    public PostalAddressType createPostalAddressType() {
        return new PostalAddressType();
    }

    /**
     * Create an instance of {@link ElectronicAddressType }
     * 
     * @return
     *     the new instance of {@link ElectronicAddressType }
     */
    public ElectronicAddressType createElectronicAddressType() {
        return new ElectronicAddressType();
    }

    /**
     * Create an instance of {@link ExtensionType }
     * 
     * @return
     *     the new instance of {@link ExtensionType }
     */
    public ExtensionType createExtensionType() {
        return new ExtensionType();
    }

    /**
     * Create an instance of {@link TrustStatusListType }
     * 
     * @return
     *     the new instance of {@link TrustStatusListType }
     */
    public TrustStatusListType createTrustStatusListType() {
        return new TrustStatusListType();
    }

    /**
     * Create an instance of {@link TrustServiceProviderListType }
     * 
     * @return
     *     the new instance of {@link TrustServiceProviderListType }
     */
    public TrustServiceProviderListType createTrustServiceProviderListType() {
        return new TrustServiceProviderListType();
    }

    /**
     * Create an instance of {@link TSLSchemeInformationType }
     * 
     * @return
     *     the new instance of {@link TSLSchemeInformationType }
     */
    public TSLSchemeInformationType createTSLSchemeInformationType() {
        return new TSLSchemeInformationType();
    }

    /**
     * Create an instance of {@link InternationalNamesType }
     * 
     * @return
     *     the new instance of {@link InternationalNamesType }
     */
    public InternationalNamesType createInternationalNamesType() {
        return new InternationalNamesType();
    }

    /**
     * Create an instance of {@link NonEmptyMultiLangURIListType }
     * 
     * @return
     *     the new instance of {@link NonEmptyMultiLangURIListType }
     */
    public NonEmptyMultiLangURIListType createNonEmptyMultiLangURIListType() {
        return new NonEmptyMultiLangURIListType();
    }

    /**
     * Create an instance of {@link NonEmptyURIListType }
     * 
     * @return
     *     the new instance of {@link NonEmptyURIListType }
     */
    public NonEmptyURIListType createNonEmptyURIListType() {
        return new NonEmptyURIListType();
    }

    /**
     * Create an instance of {@link PolicyOrLegalnoticeType }
     * 
     * @return
     *     the new instance of {@link PolicyOrLegalnoticeType }
     */
    public PolicyOrLegalnoticeType createPolicyOrLegalnoticeType() {
        return new PolicyOrLegalnoticeType();
    }

    /**
     * Create an instance of {@link NextUpdateType }
     * 
     * @return
     *     the new instance of {@link NextUpdateType }
     */
    public NextUpdateType createNextUpdateType() {
        return new NextUpdateType();
    }

    /**
     * Create an instance of {@link OtherTSLPointersType }
     * 
     * @return
     *     the new instance of {@link OtherTSLPointersType }
     */
    public OtherTSLPointersType createOtherTSLPointersType() {
        return new OtherTSLPointersType();
    }

    /**
     * Create an instance of {@link OtherTSLPointerType }
     * 
     * @return
     *     the new instance of {@link OtherTSLPointerType }
     */
    public OtherTSLPointerType createOtherTSLPointerType() {
        return new OtherTSLPointerType();
    }

    /**
     * Create an instance of {@link ServiceDigitalIdentityListType }
     * 
     * @return
     *     the new instance of {@link ServiceDigitalIdentityListType }
     */
    public ServiceDigitalIdentityListType createServiceDigitalIdentityListType() {
        return new ServiceDigitalIdentityListType();
    }

    /**
     * Create an instance of {@link AdditionalInformationType }
     * 
     * @return
     *     the new instance of {@link AdditionalInformationType }
     */
    public AdditionalInformationType createAdditionalInformationType() {
        return new AdditionalInformationType();
    }

    /**
     * Create an instance of {@link TSPType }
     * 
     * @return
     *     the new instance of {@link TSPType }
     */
    public TSPType createTSPType() {
        return new TSPType();
    }

    /**
     * Create an instance of {@link TSPInformationType }
     * 
     * @return
     *     the new instance of {@link TSPInformationType }
     */
    public TSPInformationType createTSPInformationType() {
        return new TSPInformationType();
    }

    /**
     * Create an instance of {@link TSPServicesListType }
     * 
     * @return
     *     the new instance of {@link TSPServicesListType }
     */
    public TSPServicesListType createTSPServicesListType() {
        return new TSPServicesListType();
    }

    /**
     * Create an instance of {@link TSPServiceType }
     * 
     * @return
     *     the new instance of {@link TSPServiceType }
     */
    public TSPServiceType createTSPServiceType() {
        return new TSPServiceType();
    }

    /**
     * Create an instance of {@link TSPServiceInformationType }
     * 
     * @return
     *     the new instance of {@link TSPServiceInformationType }
     */
    public TSPServiceInformationType createTSPServiceInformationType() {
        return new TSPServiceInformationType();
    }

    /**
     * Create an instance of {@link ServiceSupplyPointsType }
     * 
     * @return
     *     the new instance of {@link ServiceSupplyPointsType }
     */
    public ServiceSupplyPointsType createServiceSupplyPointsType() {
        return new ServiceSupplyPointsType();
    }

    /**
     * Create an instance of {@link DigitalIdentityListType }
     * 
     * @return
     *     the new instance of {@link DigitalIdentityListType }
     */
    public DigitalIdentityListType createDigitalIdentityListType() {
        return new DigitalIdentityListType();
    }

    /**
     * Create an instance of {@link ServiceHistoryType }
     * 
     * @return
     *     the new instance of {@link ServiceHistoryType }
     */
    public ServiceHistoryType createServiceHistoryType() {
        return new ServiceHistoryType();
    }

    /**
     * Create an instance of {@link ServiceHistoryInstanceType }
     * 
     * @return
     *     the new instance of {@link ServiceHistoryInstanceType }
     */
    public ServiceHistoryInstanceType createServiceHistoryInstanceType() {
        return new ServiceHistoryInstanceType();
    }

    /**
     * Create an instance of {@link AdditionalServiceInformationType }
     * 
     * @return
     *     the new instance of {@link AdditionalServiceInformationType }
     */
    public AdditionalServiceInformationType createAdditionalServiceInformationType() {
        return new AdditionalServiceInformationType();
    }

    /**
     * Create an instance of {@link MultiLangNormStringType }
     * 
     * @return
     *     the new instance of {@link MultiLangNormStringType }
     */
    public MultiLangNormStringType createMultiLangNormStringType() {
        return new MultiLangNormStringType();
    }

    /**
     * Create an instance of {@link MultiLangStringType }
     * 
     * @return
     *     the new instance of {@link MultiLangStringType }
     */
    public MultiLangStringType createMultiLangStringType() {
        return new MultiLangStringType();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     * @return
     *     the new instance of {@link AddressType }
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link AnyType }
     * 
     * @return
     *     the new instance of {@link AnyType }
     */
    public AnyType createAnyType() {
        return new AnyType();
    }

    /**
     * Create an instance of {@link ExtensionsListType }
     * 
     * @return
     *     the new instance of {@link ExtensionsListType }
     */
    public ExtensionsListType createExtensionsListType() {
        return new ExtensionsListType();
    }

    /**
     * Create an instance of {@link NonEmptyMultiLangURIType }
     * 
     * @return
     *     the new instance of {@link NonEmptyMultiLangURIType }
     */
    public NonEmptyMultiLangURIType createNonEmptyMultiLangURIType() {
        return new NonEmptyMultiLangURIType();
    }

    /**
     * Create an instance of {@link DigitalIdentityType }
     * 
     * @return
     *     the new instance of {@link DigitalIdentityType }
     */
    public DigitalIdentityType createDigitalIdentityType() {
        return new DigitalIdentityType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PostalAddressListType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link PostalAddressListType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "PostalAddresses")
    public JAXBElement<PostalAddressListType> createPostalAddresses(PostalAddressListType value) {
        return new JAXBElement<>(_PostalAddresses_QNAME, PostalAddressListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PostalAddressType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link PostalAddressType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "PostalAddress")
    public JAXBElement<PostalAddressType> createPostalAddress(PostalAddressType value) {
        return new JAXBElement<>(_PostalAddress_QNAME, PostalAddressType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElectronicAddressType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ElectronicAddressType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "ElectronicAddress")
    public JAXBElement<ElectronicAddressType> createElectronicAddress(ElectronicAddressType value) {
        return new JAXBElement<>(_ElectronicAddress_QNAME, ElectronicAddressType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtensionType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ExtensionType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "Extension")
    public JAXBElement<ExtensionType> createExtension(ExtensionType value) {
        return new JAXBElement<>(_Extension_QNAME, ExtensionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrustStatusListType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TrustStatusListType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "TrustServiceStatusList")
    public JAXBElement<TrustStatusListType> createTrustServiceStatusList(TrustStatusListType value) {
        return new JAXBElement<>(_TrustServiceStatusList_QNAME, TrustStatusListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrustServiceProviderListType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TrustServiceProviderListType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "TrustServiceProviderList")
    public JAXBElement<TrustServiceProviderListType> createTrustServiceProviderList(TrustServiceProviderListType value) {
        return new JAXBElement<>(_TrustServiceProviderList_QNAME, TrustServiceProviderListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TSLSchemeInformationType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TSLSchemeInformationType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "SchemeInformation")
    public JAXBElement<TSLSchemeInformationType> createSchemeInformation(TSLSchemeInformationType value) {
        return new JAXBElement<>(_SchemeInformation_QNAME, TSLSchemeInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "TSLType")
    public JAXBElement<String> createTSLType(String value) {
        return new JAXBElement<>(_TSLType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InternationalNamesType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InternationalNamesType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "SchemeOperatorName")
    public JAXBElement<InternationalNamesType> createSchemeOperatorName(InternationalNamesType value) {
        return new JAXBElement<>(_SchemeOperatorName_QNAME, InternationalNamesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InternationalNamesType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link InternationalNamesType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "SchemeName")
    public JAXBElement<InternationalNamesType> createSchemeName(InternationalNamesType value) {
        return new JAXBElement<>(_SchemeName_QNAME, InternationalNamesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NonEmptyMultiLangURIListType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NonEmptyMultiLangURIListType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "SchemeInformationURI")
    public JAXBElement<NonEmptyMultiLangURIListType> createSchemeInformationURI(NonEmptyMultiLangURIListType value) {
        return new JAXBElement<>(_SchemeInformationURI_QNAME, NonEmptyMultiLangURIListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NonEmptyURIListType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NonEmptyURIListType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "SchemeTypeCommunityRules")
    public JAXBElement<NonEmptyURIListType> createSchemeTypeCommunityRules(NonEmptyURIListType value) {
        return new JAXBElement<>(_SchemeTypeCommunityRules_QNAME, NonEmptyURIListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "SchemeTerritory")
    public JAXBElement<String> createSchemeTerritory(String value) {
        return new JAXBElement<>(_SchemeTerritory_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolicyOrLegalnoticeType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link PolicyOrLegalnoticeType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "PolicyOrLegalNotice")
    public JAXBElement<PolicyOrLegalnoticeType> createPolicyOrLegalNotice(PolicyOrLegalnoticeType value) {
        return new JAXBElement<>(_PolicyOrLegalNotice_QNAME, PolicyOrLegalnoticeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NextUpdateType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NextUpdateType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "NextUpdate")
    public JAXBElement<NextUpdateType> createNextUpdate(NextUpdateType value) {
        return new JAXBElement<>(_NextUpdate_QNAME, NextUpdateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OtherTSLPointersType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OtherTSLPointersType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "PointersToOtherTSL")
    public JAXBElement<OtherTSLPointersType> createPointersToOtherTSL(OtherTSLPointersType value) {
        return new JAXBElement<>(_PointersToOtherTSL_QNAME, OtherTSLPointersType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OtherTSLPointerType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OtherTSLPointerType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "OtherTSLPointer")
    public JAXBElement<OtherTSLPointerType> createOtherTSLPointer(OtherTSLPointerType value) {
        return new JAXBElement<>(_OtherTSLPointer_QNAME, OtherTSLPointerType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceDigitalIdentityListType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ServiceDigitalIdentityListType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "ServiceDigitalIdentities")
    public JAXBElement<ServiceDigitalIdentityListType> createServiceDigitalIdentities(ServiceDigitalIdentityListType value) {
        return new JAXBElement<>(_ServiceDigitalIdentities_QNAME, ServiceDigitalIdentityListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AdditionalInformationType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AdditionalInformationType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "AdditionalInformation")
    public JAXBElement<AdditionalInformationType> createAdditionalInformation(AdditionalInformationType value) {
        return new JAXBElement<>(_AdditionalInformation_QNAME, AdditionalInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElectronicAddressType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ElectronicAddressType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "DistributionPoints")
    public JAXBElement<ElectronicAddressType> createDistributionPoints(ElectronicAddressType value) {
        return new JAXBElement<>(_DistributionPoints_QNAME, ElectronicAddressType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TSPType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TSPType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "TrustServiceProvider")
    public JAXBElement<TSPType> createTrustServiceProvider(TSPType value) {
        return new JAXBElement<>(_TrustServiceProvider_QNAME, TSPType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TSPInformationType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TSPInformationType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "TSPInformation")
    public JAXBElement<TSPInformationType> createTSPInformation(TSPInformationType value) {
        return new JAXBElement<>(_TSPInformation_QNAME, TSPInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TSPServicesListType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TSPServicesListType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "TSPServices")
    public JAXBElement<TSPServicesListType> createTSPServices(TSPServicesListType value) {
        return new JAXBElement<>(_TSPServices_QNAME, TSPServicesListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TSPServiceType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TSPServiceType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "TSPService")
    public JAXBElement<TSPServiceType> createTSPService(TSPServiceType value) {
        return new JAXBElement<>(_TSPService_QNAME, TSPServiceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TSPServiceInformationType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TSPServiceInformationType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "ServiceInformation")
    public JAXBElement<TSPServiceInformationType> createServiceInformation(TSPServiceInformationType value) {
        return new JAXBElement<>(_ServiceInformation_QNAME, TSPServiceInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "ServiceStatus")
    public JAXBElement<String> createServiceStatus(String value) {
        return new JAXBElement<>(_ServiceStatus_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceSupplyPointsType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ServiceSupplyPointsType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "ServiceSupplyPoints")
    public JAXBElement<ServiceSupplyPointsType> createServiceSupplyPoints(ServiceSupplyPointsType value) {
        return new JAXBElement<>(_ServiceSupplyPoints_QNAME, ServiceSupplyPointsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "ServiceTypeIdentifier")
    public JAXBElement<String> createServiceTypeIdentifier(String value) {
        return new JAXBElement<>(_ServiceTypeIdentifier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DigitalIdentityListType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DigitalIdentityListType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "ServiceDigitalIdentity")
    public JAXBElement<DigitalIdentityListType> createServiceDigitalIdentity(DigitalIdentityListType value) {
        return new JAXBElement<>(_ServiceDigitalIdentity_QNAME, DigitalIdentityListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceHistoryType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ServiceHistoryType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "ServiceHistory")
    public JAXBElement<ServiceHistoryType> createServiceHistory(ServiceHistoryType value) {
        return new JAXBElement<>(_ServiceHistory_QNAME, ServiceHistoryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceHistoryInstanceType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ServiceHistoryInstanceType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "ServiceHistoryInstance")
    public JAXBElement<ServiceHistoryInstanceType> createServiceHistoryInstance(ServiceHistoryInstanceType value) {
        return new JAXBElement<>(_ServiceHistoryInstance_QNAME, ServiceHistoryInstanceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "ExpiredCertsRevocationInfo")
    public JAXBElement<XMLGregorianCalendar> createExpiredCertsRevocationInfo(XMLGregorianCalendar value) {
        return new JAXBElement<>(_ExpiredCertsRevocationInfo_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AdditionalServiceInformationType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AdditionalServiceInformationType }{@code >}
     */
    @XmlElementDecl(namespace = "http://uri.etsi.org/02231/v2#", name = "AdditionalServiceInformation")
    public JAXBElement<AdditionalServiceInformationType> createAdditionalServiceInformation(AdditionalServiceInformationType value) {
        return new JAXBElement<>(_AdditionalServiceInformation_QNAME, AdditionalServiceInformationType.class, null, value);
    }

}
