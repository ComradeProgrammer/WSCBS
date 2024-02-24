package win.worldismine.urlservice.service;

import win.worldismine.urlservice.util.ResponseObject;

public interface UserService {
    ResponseObject createUser(String username, String password);
    ResponseObject updateUser(String username, String oldPassword, String newPassword);

    ResponseObject login(String username, String password);

}
