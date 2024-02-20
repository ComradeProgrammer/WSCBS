package win.worldismine.authservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import win.worldismine.common.util.BusinessException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String username;
    private String password;

    public void setPasswordFromRawText(String raw) {
        password = hashPassword(raw);
    }

    public boolean verifyPassword(String raw) {
        return password.equals(hashPassword(raw));
    }

    private static final String SALT = "sAlt";

    private static String hashPassword(String rawPassword) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");

        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException(e.getMessage());
        }
        md.update(SALT.getBytes());
        md.update(rawPassword.getBytes());
        md.update(SALT.getBytes());
        return Hex.encodeHexString(md.digest());
    }
}
