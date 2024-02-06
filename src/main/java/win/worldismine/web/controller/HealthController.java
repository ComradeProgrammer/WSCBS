package win.worldismine.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import win.worldismine.web.util.ResponseObject;

@RestController
public class HealthController {
    @GetMapping("/ping")
    public ResponseEntity<ResponseObject<Void>> getName() {
        ResponseObject<Void> res = new ResponseObject<Void>();
        res.setCode(200);
        res.setMessage("ok");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
