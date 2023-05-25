package Requests;

import org.json.simple.JSONObject;

public class Subscribe extends Request {

    private static final String _class =
            Subscribe.class.getSimpleName();

    private String channel;
    private String unsubscribe; // Yes for unsubscribe

    public Subscribe(String channel, String unsubscribe) {
        // check for null
        if (channel == null)
            throw new NullPointerException();
        this.channel = channel;
        this.unsubscribe = unsubscribe;
    }

    public String getChannel() { return channel; }
    public String getUnsubStatus() { return unsubscribe; }

    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("channel", channel);
        obj.put("unsubscribe", unsubscribe);
        return obj;
    }

    public static Subscribe fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize posted message
            String channel = (String)obj.get("channel");
            String unsubscribe = (String)obj.get("unsubscribe");
            // construct the object to return (checking for nulls)
            return new Subscribe(channel, unsubscribe);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }

}
