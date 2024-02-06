package win.worldismine.web.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_NULL)

@Data
public class ResponseObject <T>{
    private int code;
    private String message;
    private T data;
}
