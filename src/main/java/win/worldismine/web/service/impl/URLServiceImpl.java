package win.worldismine.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import win.worldismine.web.dao.URLDao;
import win.worldismine.web.model.BusinessException;
import win.worldismine.web.model.URLObject;
import win.worldismine.web.service.URLService;
import win.worldismine.web.util.ResponseObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class URLServiceImpl implements URLService {

    @Override
    public ResponseObject<String> getURLByID(String id) {
        ResponseObject<String> res = new ResponseObject<>();
        try {
            res.setData(urlDao.getURLByID(id).getUrl());
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
    public ResponseObject<Void> setURLByID(String id, String url) {
        ResponseObject<Void> res = new ResponseObject<>();
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
    public ResponseObject<String> createURL(String url) {
        ResponseObject<String> res = new ResponseObject<>();
        URLObject obj = new URLObject();
        String id = generateIDForString();
        obj.setUrl(url);
        obj.setId(id);
        urlDao.createURLByID(obj);

        res.setCode(201);
        res.setMessage("ok");
        res.setMessage(id);
        return res;
    }

    @Override
    public ResponseObject<Void> deleteURLByID(String id) {
        ResponseObject<Void> res = new ResponseObject<>();
        try {
            urlDao.deleteURLByID(id);
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
    public ResponseObject<List<String>> listURL() {
        ResponseObject<List<String>>res=new ResponseObject<>();
        var urlObjects=urlDao.listURL();

        ArrayList<String> urls=new ArrayList<>();
        for (URLObject urlObject : urlObjects) {
            urls.add(urlObject.getId());
        }
        res.setCode(201);
        res.setData(urls);
        res.setMessage("ok");
        return res;
    }

    private String generateIDForString() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    @Autowired
    private URLDao urlDao;
}
