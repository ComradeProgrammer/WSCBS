package win.worldismine.authservice.service;

import win.worldismine.authservice.util.ResponseObject;

public interface UserService {
    ResponseObject createUser(String username, String password);
    ResponseObject updateUser(String username, String oldPassword, String newPassword);

    ResponseObject login(String username, String password);

}
