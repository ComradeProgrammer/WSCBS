package win.worldismine.web.service;

import win.worldismine.web.util.ResponseObject;

import java.util.List;

public interface URLService {
    ResponseObject<String> getURLByID(String id);

    ResponseObject<Void> setURLByID(String id, String url);

    ResponseObject<String> createURL(String url);

    ResponseObject<Void> deleteURLByID(String id);

    ResponseObject<List<String>> listURL();

}
