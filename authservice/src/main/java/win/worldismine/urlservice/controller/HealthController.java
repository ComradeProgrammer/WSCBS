package win.worldismine.urlservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/ping")
    public ResponseEntity<String> getName() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
