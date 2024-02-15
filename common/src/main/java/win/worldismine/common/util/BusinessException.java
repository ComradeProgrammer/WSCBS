package win.worldismine.common.util;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException {
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String INVALID_JWT = "INVALID_JWT";
    public static final String TOKEN_ERROR = "TOKEN_ERROR";

    public BusinessException(String msg) {
        super();
        message = msg;
    }

    private String message;
}
