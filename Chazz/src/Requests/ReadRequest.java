package Requests;

import org.json.simple.JSONObject;

public class ReadRequest extends Request {
    // class name to be used as tag in JSON representation
    private static final String _class =
            ReadRequest.class.getSimpleName();

    private String channel;
    private String Recent;
    // Constructor
    public ReadRequest(String channel, String Recent) {
        if(channel == null)
            throw new NullPointerException();
        this.channel = channel;
        this.Recent = Recent;
    }

    public String getChannel() { return channel;}

    public String getRecent() { return Recent;}

    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("channel", channel);
        obj.put("Recent", Recent);
        return obj;
    }

    // Tries to deserialize a ReadRequest instance from a JSONObject.
    public static ReadRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;

            String channel = (String)obj.get("channel");
            String Recent = (String)obj.get("Recent");
            return new ReadRequest(channel, Recent);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
