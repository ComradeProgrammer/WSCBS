package win.worldismine.web.controller;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import win.worldismine.web.service.URLService;
import win.worldismine.web.util.ResponseObject;

import java.io.InputStream;
import java.net.URI;
@Slf4j
@RestController
public class URLController {
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> GetURLByID(@PathVariable("id") String id) throws Exception {
        ResponseObject res = urlService.getURLByID(id);

        ResponseEntity<ResponseObject> response;
        if (res.getCode() == 301) {
            HttpHeaders responseHeaders = new HttpHeaders();
            //responseHeaders.setLocation(new URI(res.getValue()));
            response = new ResponseEntity<>(res, responseHeaders, HttpStatus.resolve(res.getCode()));
        } else {
            response = new ResponseEntity<>(res, HttpStatus.resolve(res.getCode()));
        }
        return response;

    }


    @Data
    static public class PutData {
        private String url;
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ResponseObject> PutURLByID(@PathVariable("id") String id, HttpServletRequest request) throws Exception {
        PutData obj;
        InputStream stream = request.getInputStream();
        try {
            ObjectMapper mapper = new ObjectMapper();
            obj = mapper.readValue(stream, PutData.class);
        } catch (JsonGenerationException e) {
            ResponseObject res = new ResponseObject();
            res.setCode(400);
            res.setMessage("invalid request body");
            return new ResponseEntity<>(res, HttpStatus.resolve(res.getCode()));
        }
        ResponseObject res = urlService.setURLByID(id, obj.getUrl());
        return new ResponseEntity<>(res, HttpStatus.resolve(res.getCode()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> DeleteURLByID(@PathVariable("id") String id) {
        ResponseObject res = urlService.deleteURLByID(id);
        return new ResponseEntity<>(res, HttpStatus.resolve(res.getCode()));
    }

    @GetMapping("/")
    public ResponseEntity<ResponseObject> ListURLs() {
        ResponseObject res = urlService.listURL();
        return new ResponseEntity<>(res, HttpStatus.resolve(res.getCode()));
    }

    @Data
    static public class PostData {
        private String value;
    }

    @PostMapping(value = "/")
    public ResponseEntity<ResponseObject> PostURL(@RequestBody PostData obj) {
        ResponseObject res = urlService.createURL(obj.getValue());
        return new ResponseEntity<>(res, HttpStatus.resolve(res.getCode()));
    }

    @DeleteMapping("/")
    public ResponseEntity<ResponseObject> DeleteURL() {
        ResponseObject res = urlService.deleteURLs();
        return new ResponseEntity<>(res, HttpStatus.resolve(res.getCode()));
    }

    @Autowired
    URLService urlService;
}
