package Response;

import Chazz.Message;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageListResponse extends Response {
    // class name to be used as tag in JSON representation
    private static final String _class =
            MessageListResponse.class.getSimpleName();

    private List<Message> messages;

    public MessageListResponse(List<Message> messages) {
        // check for nulls
        if (messages == null || messages.contains(null))
            throw new NullPointerException();
        this.messages = messages;
    }

    public List<Message> getMessages() { return messages; }

    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        // serialize messages into a JSONArray
        JSONArray arr = new JSONArray();
        for (Message msg : messages)
            arr.add(msg.toJSON());
        // serialize this as a JSONObject
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("messages", arr);
        return obj;
    }

    // Tries to deserialize a MessageListResponse instance from a JSONObject.
    public static MessageListResponse fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize messages from JSONArray
            JSONArray arr = (JSONArray)obj.get("messages");
            List<Message> messages = new ArrayList<>();
            for (Object msg_obj : arr)
                messages.add(Message.fromJSON(msg_obj));
            // construct the object to return (checking for nulls)
            return new MessageListResponse(messages);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
