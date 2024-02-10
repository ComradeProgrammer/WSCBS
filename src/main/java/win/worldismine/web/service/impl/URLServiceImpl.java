package win.worldismine.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import win.worldismine.web.dao.URLDao;
import win.worldismine.web.model.BusinessException;
import win.worldismine.web.model.URLObject;
import win.worldismine.web.service.URLService;
import win.worldismine.web.util.ResponseObject;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class URLServiceImpl implements URLService {

    @Override
    public ResponseObject getURLByID(String id) {
        ResponseObject res = new ResponseObject();
        try {
            res.setValue(urlDao.getURLByID(id).getUrl());
            res.setCode(301);
            res.setMessage("ok");
        } catch (BusinessException e) {
            if (e.getMessage().equals(BusinessException.NOT_FOUND)) {
                res.setCode(404);
                res.setMessage(e.getMessage());
            }
        }
        return res;
    }

    @Override
    public ResponseObject setURLByID(String id, String url) {
        ResponseObject res = new ResponseObject();
        // for passing the test script
        try {
            urlDao.getURLByID(id);
        } catch (BusinessException e) {
            if (e.getMessage().equals(BusinessException.NOT_FOUND)) {
                res.setCode(404);
                res.setMessage(e.getMessage());
                return res;
            }
        }

        // check url validity
        if (url == null || !checkURLValidity(url)) {
            res.setCode(400);
            res.setMessage("Invalid url");
            return res;
        }

        URLObject obj = new URLObject();
        obj.setUrl(url);
        obj.setId(id);
        try {
            urlDao.updateURLByID(obj);
            res.setCode(200);
            res.setMessage("ok");

        } catch (BusinessException e) {
            if (e.getMessage().equals(BusinessException.NOT_FOUND)) {
                res.setCode(404);
                res.setMessage(e.getMessage());
            }
        }
        return res;
    }

    @Override
    public ResponseObject createURL(String url) {
        ResponseObject res = new ResponseObject();
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
        urlDao.createURLByID(obj);

        res.setCode(201);
        res.setMessage("ok");
        res.setId(id);
        return res;
    }

    @Override
    public ResponseObject deleteURLByID(String id) {
        ResponseObject res = new ResponseObject();
        try {
            urlDao.deleteURLByID(id);
            res.setCode(204);
            res.setMessage("ok");

        } catch (BusinessException e) {
            if (e.getMessage().equals(BusinessException.NOT_FOUND)) {
                res.setCode(404);
                res.setMessage(e.getMessage());
            }
        }
        return res;
    }

    @Override
    public ResponseObject listURL() {
        ResponseObject res = new ResponseObject();
        var urlObjects = urlDao.listURL();

        ArrayList<String> urls = new ArrayList<>();
        for (URLObject urlObject : urlObjects) {
            urls.add(urlObject.getId());
        }
        res.setCode(200);
        res.setValues(urls);
        res.setMessage("ok");
        return res;
    }

    // java's uuid consists 32 bytes, and the collision possibility is 1/(2.7e18)
    // so here we take first 16 bytes, and the collision possibility is 1/(1.6e9)
    // that is acceptable in here, I think
    private String generateIDForString() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[8]);
        bb.putLong(uuid.getMostSignificantBits());
        //bb.putLong(uuid.getLeastSignificantBits());
        return Base64.getUrlEncoder().encodeToString(bb.array());
    }

    public ResponseObject deleteURLs() {
        urlDao.deleteAllURL();
        ResponseObject res = new ResponseObject();
        res.setCode(404);
        res.setMessage("ok");
        return res;
    }

    private static final String regex = "^(https?)://[-a-zA-Z0-9;/?:@&=+$,_.!~*'()%]{1,255}[-a-zA-Z0-9;/?:@&=+$,_.!~*'()]";

    private boolean checkURLValidity(String s) {
        // url allows a-zA-Z0-9;/?:@&=+-$,_.!~*'()
        Matcher matcher = Pattern.compile(regex).matcher(s);
        return matcher.matches();
    }


    @Autowired
    private URLDao urlDao;
}
