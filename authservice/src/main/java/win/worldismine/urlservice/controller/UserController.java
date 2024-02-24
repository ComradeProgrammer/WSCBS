package win.worldismine.urlservice.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import win.worldismine.urlservice.service.UserService;
import win.worldismine.urlservice.util.ResponseObject;

@RestController
public class UserController {

    @Data
    public static class CreateUserBody {
        public String username;
        public String password;
    }

    @PostMapping("/users")
    ResponseEntity<ResponseObject> CreateUser(@RequestBody CreateUserBody body) {
        ResponseObject res = userService.createUser(body.username, body.password);
        return new ResponseEntity<>(res, HttpStatus.resolve(res.getCode()));
    }


    public static class UpdatePasswordBody {
        @JsonProperty("username")
        public String username;

        @JsonProperty("password")
        public String oldpassword;

        @JsonProperty("new_password")
        public String newpassword;
    }

    @PutMapping("/users")
    ResponseEntity<ResponseObject> UpdatePassword(@RequestBody UpdatePasswordBody body) {
        ResponseObject res = userService.updateUser(body.username, body.oldpassword, body.newpassword);
        return new ResponseEntity<>(res, HttpStatus.resolve(res.getCode()));
    }

    @PostMapping("/users/login")
    ResponseEntity<ResponseObject> Login(@RequestBody CreateUserBody body) {
        ResponseObject res = userService.login(body.username, body.password);
        return new ResponseEntity<>(res, HttpStatus.resolve(res.getCode()));
    }


    @Autowired
    UserService userService;
}
