package Requests;

import org.json.simple.JSONObject;

public class MembershipRequest extends Request{

    // class name to be used as tag in JSON representation
    private static final String _class =
            MembershipRequest.class.getSimpleName();

    private String Channel;

    public MembershipRequest(String Channel) {
        // check for null
        if (Channel == null)
            throw new NullPointerException();
        this.Channel = Channel;
    }

    public String getChannel() { return Channel; }

    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("Channel", Channel);
        return obj;
    }

    public static MembershipRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize login name
            String Channel = (String)obj.get("Channel");
            return new MembershipRequest(Channel);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
