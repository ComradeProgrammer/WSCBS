package win.worldismine.authservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import win.worldismine.authservice.model.URLObject;
import win.worldismine.authservice.util.ResponseObject;
import win.worldismine.common.jwt.JWT;
import win.worldismine.common.util.BusinessException;
import win.worldismine.authservice.service.URLService;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class URLServiceImpl implements URLService {

    @Override
    synchronized public ResponseObject getURLByID(String id, String auth) {
        ResponseObject res = new ResponseObject();
        if (!idToURLObject.containsKey(id)) {
            res.setCode(404);
            res.setMessage("Not Found");
            return res;
        }

        res.setValue(idToURLObject.get(id).getUrl());
        res.setCode(301);
        res.setMessage("ok");

        return res;
    }

    @Override
    synchronized public ResponseObject setURLByID(String id, String url, String auth) {

        ResponseObject res = new ResponseObject();
        String username = getUserNameFromAuthHeader(auth);
        if (username == null) {
            res.setCode(403);
            res.setMessage("Forbidden");
            return res;
        }
        if (!idToURLObject.containsKey(id)) {
            res.setCode(404);
            res.setMessage("Not Found");
            return res;
        }
        // check authentication
        URLObject obj = idToURLObject.get(id);
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
        res.setCode(200);
        res.setMessage("ok");

        return res;
    }

    @Override
    synchronized public ResponseObject createURL(String url, String auth) {
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
        idToURLObject.put(id, obj);
        res.setCode(201);
        res.setMessage("ok");
        res.setId(id);
        return res;
    }

    @Override
    synchronized public ResponseObject deleteURLByID(String id, String auth) {
        ResponseObject res = new ResponseObject();
        String username = getUserNameFromAuthHeader(auth);
        if (username == null) {
            res.setCode(403);
            res.setMessage("Forbidden");
            return res;
        }
        if (!idToURLObject.containsKey(id)) {
            res.setCode(404);
            res.setMessage("Not Found");
            return res;
        }
        // check authentication
        URLObject obj = idToURLObject.get(id);
        if (!obj.getCreator().equals(username)) {
            res.setCode(403);
            res.setMessage("Forbidden");
            return res;
        }

        idToURLObject.remove(id);

        res.setCode(204);
        res.setMessage("ok");
        return res;
    }

    @Override
    synchronized public ResponseObject listURL(String auth) {
        ResponseObject res = new ResponseObject();
        String username = getUserNameFromAuthHeader(auth);
        if (username == null) {
            res.setCode(403);
            res.setMessage("Forbidden");
            return res;
        }
        ArrayList<String> urls = new ArrayList<>();
        for (URLObject urlObject : idToURLObject.values()) {
            if (urlObject.getCreator().equals(username)) {
                urls.add(urlObject.getId());
            }
        }
        res.setCode(200);
        res.setValues(urls);
        res.setMessage("ok");
        return res;
    }

    synchronized public ResponseObject deleteURLs(String auth) {
        ResponseObject res = new ResponseObject();
        String username = getUserNameFromAuthHeader(auth);
        if (username == null) {
            res.setCode(403);
            res.setMessage("Forbidden");
            return res;
        }

        ArrayList<String> ids = new ArrayList<>();
        for (String i : idToURLObject.keySet()) {
            if (idToURLObject.get(i).getCreator().equals(username)) {
                ids.add(i);
            }
        }

        for (String i : ids) {
            idToURLObject.remove(i);
        }

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
        if (segments.length != 2 || !segments[0].equals("Bearer")) {
            log.warn("invalid auth header {}", auth);
            return null;
        }
        JWT jwt;
        try {
            jwt = JWT.decodeAndVerify(segments[1], publicKeyContent);
        } catch (Exception e) {
            log.warn("invalid jwt: {}", e.getMessage());
            return null;
        }
        return jwt.getJwtBody().getName();

    }


    @Value("${myjwt.publicKey}")
    private String publicKeyContent;

    private Map<String, URLObject> idToURLObject = new HashMap<>();
}
