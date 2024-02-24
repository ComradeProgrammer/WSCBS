package win.worldismine.urlservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import win.worldismine.urlservice.dao.UserDao;
import win.worldismine.urlservice.entity.User;
import win.worldismine.urlservice.service.UserService;
import win.worldismine.urlservice.util.ResponseObject;
import win.worldismine.common.jwt.JWT;
import win.worldismine.common.jwt.JWTBody;
import win.worldismine.common.jwt.JWTHeader;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Value("${myjwt.privateKey}")
    private String privateKeyContent;

    @Override
    @Transactional
    public ResponseObject createUser(String username, String password) {
        // check whether this user exists
        User user = userDao.findUserByUserName(username);
        ResponseObject res = new ResponseObject();
        if (user != null) {
            res.setCode(409);
            res.setMessage("Duplicated");
            res.setDetail("duplicate");
            return res;
        }

        // create the user
        user = new User();
        user.setUsername(username);
        user.setPasswordFromRawText(password);
        userDao.createUser(user);
        // return the response
        res.setMessage("Ok");
        res.setCode(201);
        return res;
    }

    @Override
    @Transactional
    public ResponseObject updateUser(String username, String oldPassword, String newPassword) {
        // check whether this user exists
        User user = userDao.findUserByUserName(username);
        ResponseObject res = new ResponseObject();
        if (user == null || !user.verifyPassword(oldPassword)) {
            res.setCode(403);
            res.setMessage("Forbidden");
            res.setDetail("forbidden");
            return res;
        }

        user.setPasswordFromRawText(newPassword);
        userDao.updateUserByUserName(user);

        res.setCode(200);
        res.setMessage("Ok");
        return res;
    }


    @Override
    public ResponseObject login(String username, String password) {
        // check whether this user exists and whether this password is correct
        User user = userDao.findUserByUserName(username);
        ResponseObject res = new ResponseObject();
        if (user == null || !user.verifyPassword(password)) {
            res.setCode(403);
            res.setMessage("Forbidden");
            res.setDetail("forbidden");
            return res;
        }
        // now we are going to issue a JWT token
        JWT jwt = new JWT();
        jwt.setJwtHeader(new JWTHeader());
        JWTBody jwtBody = new JWTBody();
        jwtBody.setName(username);
        jwt.setJwtBody(jwtBody);
        // sign it
        String jwtString = jwt.encode(privateKeyContent);
        res.setJwt(jwtString);
        res.setMessage("Ok");
        res.setCode(200);
        return res;

    }
}
