package win.worldismine.common.jwt;

import lombok.Data;

@Data
public class JWTHeader {
    private String alg = "RS256";
    private String typ = "JWT";
}
