package Requests;

import org.json.simple.JSONObject;

public class LoginRequest extends Request {
    // class name to be used as tag in JSON representation
    private static final String _class =
            LoginRequest.class.getSimpleName();

    private String name;
    private String password;

    // validation
    public LoginRequest(String name, String password) {
        // check for null
        if (name == null || password == null)
            throw new NullPointerException();
        this.name = name;
        this.password = password;
    }

    public String getName() { return name; }
    public String getPassword() { return password; }

    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("name", name);
        obj.put("password", password);
        return obj;
    }

    // Tries to deserialize a LoginRequest instance from a JSONObject.
    public static LoginRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize login name
            String name = (String)obj.get("name");
            // deserialize login password
            String password = (String)obj.get("password");
            // construct the object to return any nulls
            return new LoginRequest(name, password);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
