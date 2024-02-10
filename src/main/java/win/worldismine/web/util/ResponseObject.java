package win.worldismine.web.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)

@Data
public class ResponseObject {
    private int code;
    private String message;

    private String id;
    private String value;
    private List<String> values; // no longer used due to specification change
}
