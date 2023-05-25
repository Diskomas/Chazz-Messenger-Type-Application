package Requests;

import org.json.simple.JSONObject;

public class PostRequest extends Request {
    // class name to be used as tag in JSON representation
    private static final String _class =
            PostRequest.class.getSimpleName();

    private String message;
    private String channel;

    public PostRequest(String message, String channel) {
        // check for null
        if (message == null || channel == null)
            throw new NullPointerException();
        this.message = message;
        this.channel = channel;
    }

    public String getMessage() { return message; }

    public String getChannel() { return channel; }

    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("message", message);
        obj.put("channel", channel);
        return obj;
    }

    // Tries to deserialize a PostRequest instance from a JSONObject.
    public static PostRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            String message = (String)obj.get("message");
            String channel = (String)obj.get("channel");
            // construct the object to return (checking for nulls)
            return new PostRequest(message, channel);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
