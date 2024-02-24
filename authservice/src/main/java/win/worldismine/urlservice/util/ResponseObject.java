package win.worldismine.urlservice.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)

@Data
public class ResponseObject {
    private int code;
    private String message;
    @JsonProperty("token")
    private String jwt;
    private String detail;
}