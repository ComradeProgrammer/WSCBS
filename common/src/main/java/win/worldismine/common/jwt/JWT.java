package win.worldismine.common.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import win.worldismine.common.util.BusinessException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;


public class JWT {
    @Getter
    @Setter
    private JWTHeader jwtHeader;
    @Getter
    @Setter
    private JWTBody jwtBody;
    @Getter
    @Setter
    private String signature;

    protected static Logger log = LogManager.getLogger(JWT.class);

    public String encode(String privateKeyContent) {
        String[] segments = new String[3];
        // serialization of header and body
        try {
            ObjectMapper mapper = new ObjectMapper();
            segments[0] = Base64.getUrlEncoder().encodeToString(mapper.writeValueAsString(jwtHeader).getBytes());
            segments[1] = Base64.getUrlEncoder().encodeToString(mapper.writeValueAsString(jwtBody).getBytes());
        } catch (Exception e) {
            log.warn("failed to marshal token");
            throw new BusinessException(BusinessException.TOKEN_ERROR);
        }
        // signing the signature
        try {
            // load private key
            RSAPrivateKey rsaPrivateKey = getPrivateKeyFromPemFile(privateKeyContent);
            // generate binary signature
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(rsaPrivateKey);
            signature.update((segments[0] + "." + segments[1]).getBytes());
            // url-base64-encoding it
            segments[2] = Base64.getUrlEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            log.warn("signature generation failed, {}", e.getMessage());
            throw new BusinessException(BusinessException.TOKEN_ERROR);
        }
        return String.join(".", segments);
    }

    public static JWT decodeAndVerify(String token, String publicKeyContent) {
        String[] segments = token.split("\\.");
        if (segments.length != 3) {
            log.warn("token {} does not contains 3 segments", token);
            throw new BusinessException(BusinessException.INVALID_JWT);
        }
        JWTHeader header;
        JWTBody body;
        // deserialization of header and body
        try {
            ObjectMapper mapper = new ObjectMapper();
            header = mapper.readValue(Base64.getUrlDecoder().decode(segments[0]), JWTHeader.class);
            body = mapper.readValue(Base64.getUrlDecoder().decode(segments[1]), JWTBody.class);
        } catch (Exception e) {
            log.warn("failed to unmarshal {}", token);
            throw new BusinessException(BusinessException.INVALID_JWT);
        }

        // calculate the signature and use rsa256 verify it
        try {
            //load public key
            RSAPublicKey rsaPublicKey = getPublicKeyFromPemFile(publicKeyContent);
            // verify the signature
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(rsaPublicKey);
            signature.update((segments[0] + "." + segments[1]).getBytes());
            boolean result = signature.verify(Base64.getUrlDecoder().decode(segments[2]));
            if (!result) {
                log.warn("incorrect signature {}", token);
                throw new BusinessException(BusinessException.INVALID_JWT);
            }
        } catch (Exception e) {
            log.warn("signature verification failed, {}", e.getMessage());
            throw new BusinessException(BusinessException.INVALID_JWT);
        }
        // return the jwt
        JWT res = new JWT();
        res.setJwtBody(body);
        res.setJwtHeader(header);
        res.setSignature(segments[2]);

        return res;
    }

    // getPublicKeyFromPemFile parse a content of a pem public key file
    public static RSAPublicKey getPublicKeyFromPemFile(String publicKeyContent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyPEM = publicKeyContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyPEM));
        RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
        return publicKey;
    }
    // getPrivateKeyFromPemFile parse a content of a pem private key file
    public static RSAPrivateKey getPrivateKeyFromPemFile(String privateKeyContent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyPEM = privateKeyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyPEM.getBytes()));
        RSAPrivateKey privateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        return privateKey;
    }


}
