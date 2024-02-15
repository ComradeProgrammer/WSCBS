package win.worldismine.authservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import win.worldismine.authservice.util.ResponseObject;

@RestController
public class HealthController {
    @GetMapping("/ping")
    public ResponseEntity<ResponseObject> getName() {
        ResponseObject res = new ResponseObject();
        res.setCode(200);
        res.setMessage("ok");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
