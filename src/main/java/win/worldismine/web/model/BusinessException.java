package win.worldismine.web.model;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException {
    public static final String NOT_FOUND = "NOT_FOUND";

    public BusinessException(String msg) {
        super();
        message = msg;
    }

    private String message;
}
