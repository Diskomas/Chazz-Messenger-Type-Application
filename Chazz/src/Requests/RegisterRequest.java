package Requests;

import org.json.simple.JSONObject;

public class RegisterRequest extends Request{

    // class name to be used as tag in JSON representation
    private static final String _class =
            RegisterRequest.class.getSimpleName();

    private String name;
    private String password;

    public RegisterRequest(String name, String password) {
        // check for null
        if (name == null)
            throw new NullPointerException();
        this.name = name;
        this.password = password;
    }

    public String getName() { return name; }
    public String getPassword() { return password; }

    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("name", name);
        obj.put("password", password);
        return obj;
    }

    public static RegisterRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize login name
            String name = (String)obj.get("name");
            String password = (String)obj.get("password");
            // construct the object to return (checking for nulls)
            return new RegisterRequest(name, password);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
