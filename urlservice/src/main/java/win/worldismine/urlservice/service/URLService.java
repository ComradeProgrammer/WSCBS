package win.worldismine.urlservice.service;

import win.worldismine.urlservice.util.ResponseObject;

public interface URLService {
    ResponseObject getURLByID(String id, String auth);

    ResponseObject setURLByID(String id, String url, String auth);

    ResponseObject createURL(String url, String auth);

    ResponseObject deleteURLByID(String id, String auth);

    ResponseObject deleteURLs( String auth);

    ResponseObject listURL( String auth);

}
