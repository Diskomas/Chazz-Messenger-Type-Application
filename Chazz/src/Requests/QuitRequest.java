package Requests;

import org.json.simple.JSONObject;

public class QuitRequest extends Request {
    // class name to be used as tag in JSON representation
    private static final String _class =
            QuitRequest.class.getSimpleName();

    public QuitRequest() {}

    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        return obj;
    }

    // Tries to deserialize a QuitRequest instance from a JSONObject.
    public static QuitRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // construct the new object to return
            return new QuitRequest();
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
