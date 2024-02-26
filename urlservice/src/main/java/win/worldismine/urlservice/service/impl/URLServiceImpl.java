package win.worldismine.urlservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import win.worldismine.urlservice.dao.UrlDao;
import win.worldismine.urlservice.model.URLObject;
import win.worldismine.urlservice.util.ResponseObject;
import win.worldismine.common.jwt.JWT;
import win.worldismine.urlservice.service.URLService;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class URLServiceImpl implements URLService {

    @Override
    @Transactional
    public ResponseObject getURLByID(String id, String auth) {
        ResponseObject res = new ResponseObject();
        URLObject obj = urlDao.getUrlObjById("id");
        if (obj == null) {
            res.setCode(404);
            res.setMessage("Not Found");
            return res;
        }

        res.setValue(obj.getUrl());
        res.setCode(301);
        res.setMessage("ok");

        return res;
    }

    @Override
    @Transactional
    public ResponseObject setURLByID(String id, String url, String auth) {

        ResponseObject res = new ResponseObject();
        String username = getUserNameFromAuthHeader(auth);
        if (username == null) {
            res.setCode(403);
            res.setMessage("Forbidden");
            return res;
        }

        URLObject obj = urlDao.getUrlObjById("id");
        if (obj == null) {
            res.setCode(404);
            res.setMessage("Not Found");
            return res;
        }
        // check authentication
        if (!obj.getCreator().equals(username)) {
            res.setCode(403);
            res.setMessage("Forbidden");
            return res;
        }

        // check url validity
        if (url == null || !checkURLValidity(url)) {
            res.setCode(400);
            res.setMessage("Invalid url");
            return res;
        }

        // change url
        obj.setUrl(url);
        obj.setId(id);
        urlDao.updateUrlObjById(obj);

        res.setCode(200);
        res.setMessage("ok");

        return res;
    }

    @Override
    @Transactional
    public ResponseObject createURL(String url, String auth) {
        ResponseObject res = new ResponseObject();
        String username = getUserNameFromAuthHeader(auth);
        if (username == null) {
            res.setCode(403);
            res.setMessage("Forbidden");
            return res;
        }
        // check url validity
        if (url == null || !checkURLValidity(url)) {
            res.setCode(400);
            res.setMessage("Invalid url");
            return res;
        }

        URLObject obj = new URLObject();
        String id = generateIDForString();
        obj.setUrl(url);
        obj.setId(id);
        obj.setCreator(username);
        urlDao.createUrlObj(obj);
        res.setCode(201);
        res.setMessage("ok");
        res.setId(id);
        return res;
    }

    @Override
    @Transactional
    public ResponseObject deleteURLByID(String id, String auth) {
        ResponseObject res = new ResponseObject();
        String username = getUserNameFromAuthHeader(auth);
        if (username == null) {
            res.setCode(403);
            res.setMessage("Forbidden");
            return res;
        }
        URLObject obj = urlDao.getUrlObjById("id");
        if (obj == null) {
            res.setCode(404);
            res.setMessage("Not Found");
            return res;
        }

        // check authentication
        if (!obj.getCreator().equals(username)) {
            res.setCode(403);
            res.setMessage("Forbidden");
            return res;
        }

        urlDao.deleteUrlById(id);

        res.setCode(204);
        res.setMessage("ok");
        return res;
    }

    @Override
    @Transactional

    public ResponseObject listURL(String auth) {
        ResponseObject res = new ResponseObject();
        String username = getUserNameFromAuthHeader(auth);
        if (username == null) {
            res.setCode(403);
            res.setMessage("Forbidden");
            return res;
        }
        ArrayList<String> urls = new ArrayList<>();
        List<URLObject> urlObjs = urlDao.listUrlObjsByUser(username);
        for (URLObject urlObject : urlObjs) {
            urls.add(urlObject.getId());
        }
        res.setCode(200);
        res.setValues(urls);
        res.setMessage("ok");
        return res;
    }

    @Transactional
    public ResponseObject deleteURLs(String auth) {
        ResponseObject res = new ResponseObject();
        String username = getUserNameFromAuthHeader(auth);
        if (username == null) {
            res.setCode(403);
            res.setMessage("Forbidden");
            return res;
        }

        urlDao.deleteUrlByUser(username);

        res.setCode(404);
        res.setMessage("ok");
        return res;
    }

    // java's uuid consists 32 bytes, and the collision possibility is 1/(2.7e18)
    // so here we take first 16 bytes, and the collision possibility is 1/(1.6e9)
    // that is acceptable in here, I think
    private String generateIDForString() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[8]);
        bb.putLong(uuid.getLeastSignificantBits());
        //bb.putLong(uuid.getMostSignificantBits());
        return Base64.getUrlEncoder().encodeToString(Arrays.copyOf(bb.array(), 4));
    }


    private boolean checkURLValidity(String s) {
        Matcher matcher = Pattern.compile("^(https?)://[-a-zA-Z0-9;/?:@&=+$,_.!~*'()%#]{1,255}[-a-zA-Z0-9;/?:@&=+$,_.!~*'()#]").matcher(s);
        return matcher.matches();
    }

    private String getUserNameFromAuthHeader(String auth) {
        if (auth == null) {
            return null;
        }
        String[] segments = auth.split(" ");
        String token = null;
        if (segments.length == 2 && segments[0].equals("Bearer")) {
            token = segments[1];
        } else if (segments.length == 1) {
            token = segments[0];
        } else {
            log.warn("invalid auth header {}", auth);
            return null;
        }
        JWT jwt;
        try {
            jwt = JWT.decodeAndVerify(token, publicKeyContent);
        } catch (Exception e) {
            log.warn("invalid jwt: {}", e.getMessage());
            return null;
        }
        return jwt.getJwtBody().getName();

    }


    @Value("${myjwt.publicKey}")
    private String publicKeyContent;

    @Autowired
    private UrlDao urlDao;
}
