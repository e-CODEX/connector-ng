/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */


package eu.ecodex.connector.infrastructure.outbound.evidence.spocseu.exception;

import java.io.Serial;

/**
 * This exception will be thrown in the case of wrong input data. For example, if the input data
 * could not be parsed with jaxb. In this case the user has to change the input parameter and try it
 * then again.
 *
 * @author Lindemann
 */
public class SpocsWrongInputDataException extends Exception {
    @Serial
    private static final long serialVersionUID = -3191051364594522380L;

    public SpocsWrongInputDataException(String message) {
        super(message);
    }

    public SpocsWrongInputDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
