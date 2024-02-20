package win.worldismine.authservice.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)

@Data
public class ResponseObject {
    private int code;
    private String message;

    private String jwt;
    private String detail;
}