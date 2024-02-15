package win.worldismine.authservice.service;

import win.worldismine.authservice.util.ResponseObject;

public interface URLService {
    ResponseObject getURLByID(String id);

    ResponseObject setURLByID(String id, String url);

    ResponseObject createURL(String url);

    ResponseObject deleteURLByID(String id);

    ResponseObject deleteURLs();

    ResponseObject listURL();

}
