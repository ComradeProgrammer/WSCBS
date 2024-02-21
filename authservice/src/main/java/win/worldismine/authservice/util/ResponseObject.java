package win.worldismine.authservice.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)

@Data
public class ResponseObject {
    private int code;
    private String message;
    @JsonProperty("token")
    private String jwt;
    private String detail;
}