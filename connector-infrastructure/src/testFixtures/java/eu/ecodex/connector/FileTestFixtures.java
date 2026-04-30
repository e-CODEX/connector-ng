/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
public class FileTestFixtures {
    public static byte[] readAsBytes(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return StreamUtils.copyToByteArray(
                    resource.getInputStream()
            );
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public static String readAsString(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return StreamUtils.copyToString(
                    resource.getInputStream(), StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            return "";
        }
    }

    public static File readAsFile(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return resource.getFile();
        } catch (IOException e) {
            return null;
        }
    }
}
