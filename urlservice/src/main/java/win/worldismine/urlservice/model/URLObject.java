package win.worldismine.urlservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class URLObject {
    private String id;
    private String url;
    private String creator;
}
