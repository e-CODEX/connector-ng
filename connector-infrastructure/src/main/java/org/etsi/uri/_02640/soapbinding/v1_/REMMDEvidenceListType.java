
package org.etsi.uri._02640.soapbinding.v1_;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for REMMDEvidenceListType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="REMMDEvidenceListType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://uri.etsi.org/02640/soapbinding/v1#}REMMDEvidenceListType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}SubmissionAcceptanceRejection" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}RelayREMMDAcceptanceRejection" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}RelayREMMDFailure" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}DeliveryNonDeliveryToRecipient" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}RetrievalNonRetrievalByRecipient" minOccurs="0"/&gt;
 *         &lt;element ref="{http://uri.etsi.org/02640/v2#}AcceptanceRejectionByRecipient" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "REMMDEvidenceListType")
public class REMMDEvidenceListType
    extends OriginalREMMDEvidenceListType
{


}
