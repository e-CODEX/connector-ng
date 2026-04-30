
package eu.spocseu.edeliverygw.configuration;

import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.spocseu.edeliverygw.configuration package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.spocseu.edeliverygw.configuration
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EDeliveryDetail }
     * 
     * @return
     *     the new instance of {@link EDeliveryDetail }
     */
    public EDeliveryDetail createEDeliveryDetail() {
        return new EDeliveryDetail();
    }

    /**
     * Create an instance of {@link EDeliveryDetail.Server }
     * 
     * @return
     *     the new instance of {@link EDeliveryDetail.Server }
     */
    public EDeliveryDetail.Server createEDeliveryDetailServer() {
        return new EDeliveryDetail.Server();
    }

    /**
     * Create an instance of {@link EDeliveryDetail.PostalAdress }
     * 
     * @return
     *     the new instance of {@link EDeliveryDetail.PostalAdress }
     */
    public EDeliveryDetail.PostalAdress createEDeliveryDetailPostalAdress() {
        return new EDeliveryDetail.PostalAdress();
    }

}
