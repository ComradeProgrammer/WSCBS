package win.worldismine.web.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import win.worldismine.web.service.URLService;
import win.worldismine.web.util.ResponseObject;

import java.net.URI;
import java.util.List;

@RestController
public class URLController {
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<String>> GetURLByID(@PathVariable("id") String id) throws Exception {
        ResponseObject<String> res = urlService.getURLByID(id);

        ResponseEntity<ResponseObject<String>> response;
        if (res.getCode() == 301) {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setLocation(new URI(res.getData()));
            response = new ResponseEntity<>(res, responseHeaders, HttpStatus.MOVED_PERMANENTLY);
        } else {
            response = new ResponseEntity<>(res, HttpStatus.resolve(res.getCode()));
        }
        return response;

    }

    @Data
    static public class PutData {
        private String url;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<Void>> PutURLByID(@PathVariable("id") String id, @RequestBody PutData obj) {
        ResponseObject<Void> res = urlService.setURLByID(id, obj.getUrl());
        return new ResponseEntity<>(res, HttpStatus.resolve(res.getCode()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Void>> DeleteURLByID(@PathVariable("id") String id) {
        ResponseObject<Void> res = urlService.deleteURLByID(id);
        return new ResponseEntity<>(res, HttpStatus.resolve(res.getCode()));
    }

    @GetMapping("/")
    public ResponseEntity<ResponseObject<List<String>>> ListURLs() {
        ResponseObject<List<String>> res = urlService.listURL();
        return new ResponseEntity<>(res, HttpStatus.resolve(res.getCode()));
    }

    @PostMapping("/")
    public ResponseEntity<ResponseObject<String>> PostURL(@RequestBody PutData obj) {
        ResponseObject<String> res = urlService.createURL(obj.getUrl());
        return new ResponseEntity<>(res, HttpStatus.resolve(res.getCode()));
    }

    @Autowired
    URLService urlService;
}
