/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.evidence.spocseu.model;

import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail;
import lombok.Getter;
import lombok.Setter;

/**
 * This Class represents the holds the <code>Configuration</code> of the eDelivery Project.
 */
@Getter
@Setter
public class EDeliveryDetails {
    private String gatewayName;
    private String streetAddress;
    private String locality;
    private String postalCode;
    private String country;
    private boolean checkSignature = false;
    private boolean checkMessage = false;
    private String gatewayAddress;
    private String gatewayDomain;
    private int defaultCitizenQAALevel = 1;
    private boolean synchronGatewayMD = true;

    /**
     * Creates a new instance of EDeliveryDetails using data from the provided EDeliveryDetail
     * object.
     *
     * @param deliveryDetail The EDeliveryDetail object containing the data.
     */
    public EDeliveryDetails(EDeliveryDetail deliveryDetail) {
        setGatewayAddress(deliveryDetail.getServer().getGatewayAddress());
        setGatewayName(deliveryDetail.getServer().getGatewayName());
        // PostalAdress
        if (deliveryDetail.getPostalAdress() != null) {
            setStreetAddress(deliveryDetail.getPostalAdress().getStreetAddress());
            setLocality(deliveryDetail.getPostalAdress().getLocality());
            setPostalCode(deliveryDetail.getPostalAdress().getPostalCode());
            setCountry(deliveryDetail.getPostalAdress().getCountry());
        }

        var defaultCitizenQAALevel = deliveryDetail.getServer().getDefaultCitizenQAAlevel();

        if (defaultCitizenQAALevel != null) {
            setDefaultCitizenQAALevel(defaultCitizenQAALevel);
        }
    }
}
