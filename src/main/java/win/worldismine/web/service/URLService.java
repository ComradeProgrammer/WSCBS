package win.worldismine.web.service;

import win.worldismine.web.util.ResponseObject;

import java.util.List;

public interface URLService {
    ResponseObject getURLByID(String id);

    ResponseObject setURLByID(String id, String url);

    ResponseObject createURL(String url);

    ResponseObject deleteURLByID(String id);

    ResponseObject deleteURLs();

    ResponseObject listURL();

}
