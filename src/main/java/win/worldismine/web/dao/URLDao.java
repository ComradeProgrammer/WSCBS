package win.worldismine.web.dao;

import win.worldismine.web.model.URLObject;

import java.util.List;


public interface URLDao {
    URLObject getURLByID(String id);
    void updateURLByID(URLObject urlObj);
    void deleteURLByID(String id);
    void createURLByID(URLObject urlObject);
    List<URLObject> listURL();

}
