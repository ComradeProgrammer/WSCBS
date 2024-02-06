package win.worldismine.web.dao.Impl;

import org.springframework.stereotype.Component;
import win.worldismine.web.dao.URLDao;
import win.worldismine.web.model.BusinessException;
import win.worldismine.web.model.URLObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class URLDaoMemoryImpl implements URLDao {

    private Map<String, URLObject> idToURLObject = new HashMap<>();

    @Override
    synchronized public URLObject getURLByID(String id) {
        if (!idToURLObject.containsKey(id)) {
            throw new BusinessException(BusinessException.NOT_FOUND);
        }
        return idToURLObject.get(id);
    }

    @Override
    synchronized public void updateURLByID(URLObject urlObj) {
        String id = urlObj.getId();
        if (!idToURLObject.containsKey(id)) {
            throw new BusinessException(BusinessException.NOT_FOUND);
        }
        idToURLObject.put(id, urlObj);
    }

    @Override
    synchronized public void deleteURLByID(String id) {
        if (!idToURLObject.containsKey(id)) {
            throw new BusinessException(BusinessException.NOT_FOUND);
        }
        idToURLObject.remove(id);
    }

    @Override
    synchronized public void createURLByID(URLObject urlObj) {
        String id = urlObj.getId();

        idToURLObject.put(id, urlObj);
    }

    @Override
    synchronized public List<URLObject> listURL() {
        ArrayList<URLObject> res = new ArrayList<>();
        for (Map.Entry<String, URLObject> entry : idToURLObject.entrySet()) {
            res.add(entry.getValue());
        }
        return res;
    }
}
