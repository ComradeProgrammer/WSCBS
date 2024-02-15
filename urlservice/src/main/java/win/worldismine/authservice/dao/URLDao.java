package win.worldismine.authservice.dao;

import win.worldismine.authservice.model.URLObject;

import java.util.List;


public interface URLDao {
    URLObject getURLByID(String id);

    void updateURLByID(URLObject urlObj);

    void deleteURLByID(String id);

    void createURLByID(URLObject urlObject);

    void deleteAllURL();

    List<URLObject> listURL();

}
