package eu.ecodex.connector.infrastructure.helper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import javax.crypto.SecretKey;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SecretKeyGeneratorHelper {

    public String generate() {
        SecretKey key = Jwts.SIG.HS256.key().build();
        return Encoders.BASE64.encode(key.getEncoded());
    }
}
