package win.worldismine.common.jwt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

class JWTTest {
    public static final String privateKeyContent = """
            -----BEGIN PRIVATE KEY-----
            MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAL/b3ejW3dGOmka+
            9CjG29UJU9uka5yJksn1jrybU9F9xwxwXKfFky53PP+U44TECrRGha/KmfQNsmCK
            i7coFF1rLzqqRJdB1CwZvdjxsc48IrCkxtAFQa21ImS9hOGl6x+q0t2Q1LyuDQ1L
            Gylo5UtBxyr8gBPguXZw7lx2oscRAgMBAAECgYBa90M6341ii2toeadIIike1AJX
            lJiVKsFXUj8v+7F9FVan9ipoPYwwLsbUI2LQkybEHN/L3pKqHUhi762Y1L76S/LB
            lwCby5x7l4osKdLZv6tIoAPb+4ZqE43GpcWzvvOlGbWsgGcqiC4pCseh5KjNu1Iz
            ioGzrDwLf1ZAD9EMcQJBAP4pDkOtABFauzADsHRSgQ1Vhb3ntdECBsaGnusruBw1
            8dIgymlJ6f1SwwtXMZLzVDMf1rcpJN7x3/c5Cvg3wJ0CQQDBP167lKBXN/En5eht
            CO5NVWqLBdtn5L/8cUJzoCA28FtjCoX7/LkbP3dZGAWK3mUf8FubDXnpTg2gyU1T
            EtQFAkEAi/wQ3zVp1CUk9E0T4v2OFsoD35S/j16OXMnIvb5WqTMM/a8RzOvo9J2g
            z542r7lVptLib/85T5HoyUGhyRTsAQJBAIxQF9e5bcaGTup6S43Hu0eZanew9AL2
            m9OO5QIRVPKvCJvcCzbQ5BJ9vYmKIJQZMHHFUq49VbRpwQlqwGM+O4UCQQCAlGfj
            rxwcgzZcxNpfdqar7UuGDQFHoQDSHpkFbXwuifjI4WIC0MeQYKor6JOyil/5MgOr
            eAZjoA0FZLvuRUbb
            -----END PRIVATE KEY-----
                        """;

    public static final String publicKeyContent = """
            -----BEGIN PUBLIC KEY-----
            MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/293o1t3RjppGvvQoxtvVCVPb
            pGuciZLJ9Y68m1PRfccMcFynxZMudzz/lOOExAq0RoWvypn0DbJgiou3KBRday86
            qkSXQdQsGb3Y8bHOPCKwpMbQBUGttSJkvYThpesfqtLdkNS8rg0NSxspaOVLQccq
            /IAT4Ll2cO5cdqLHEQIDAQAB
            -----END PUBLIC KEY-----
                        """;

    /**
     * In this test we use our library to sign a jwt and use auth0 lib to verify it.
     */
    @Test
    void encode() throws Exception {

        RSAPrivateKey privateKey = win.worldismine.common.jwt.JWT.getPrivateKeyFromPemFile(privateKeyContent);

        RSAPublicKey publicKey = win.worldismine.common.jwt.JWT.getPublicKeyFromPemFile(publicKeyContent);

        Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);

        win.worldismine.common.jwt.JWT tmp2 = new win.worldismine.common.jwt.JWT();
        tmp2.setJwtHeader(new JWTHeader());
        tmp2.setJwtBody(new JWTBody());
        tmp2.getJwtBody().setName("TJM");
        String res2 = tmp2.encode(privateKeyContent);
        JWTVerifier verifier = com.auth0.jwt.JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(res2);
        assertEquals("TJM", decodedJWT.getClaim("name").asString());
    }

    /**
     * In this test we use auth0 lib to sign a jwt and use our algorithm to verify.
     */
    @Test
    void decodeAndVerify() throws Exception {
        RSAPrivateKey privateKey = win.worldismine.common.jwt.JWT.getPrivateKeyFromPemFile(privateKeyContent);
        RSAPublicKey publicKey = win.worldismine.common.jwt.JWT.getPublicKeyFromPemFile(publicKeyContent);

        Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "RS256");
        String res1 = com.auth0.jwt.JWT.create().withHeader(header).withClaim("name", "TJM").sign(algorithm);

        var res2 = win.worldismine.common.jwt.JWT.decodeAndVerify(res1, publicKeyContent);
        assertEquals("TJM", res2.getJwtBody().getName());
        assertEquals("RS256", res2.getJwtHeader().getAlg());
        assertEquals("JWT", res2.getJwtHeader().getTyp());

    }
}