package Chazz;

import org.json.simple.JSONObject;

public class Message {
    // class name to be used as tag in JSON representation
    private static final String _class =
            Message.class.getSimpleName();

    private final String body;
    private final String author;
    private final String   timestamp;

    // Constructor; throws NullPointerException if arguments are null
    public Message(String body, String author, String timestamp) {
        if (body == null || author == null)
            throw new NullPointerException();
        this.body      = body;
        this.author    = author;
        this.timestamp = timestamp;
    }

    public String getBody()      { return body; }
    public String getAuthor()    { return author; }
    public String getTimestamp() { return timestamp; }

    public String toString() {
        return author + ": " + body + " (" + timestamp + ")";
    }


    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class",    _class);
        obj.put("body",      body);
        obj.put("author",    author);
        obj.put("timestamp", timestamp);
        return obj;
    }

    // Deserialize a Message instance from a JSONObject
    public static Message fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize message fields
            String body      = (String)obj.get("body");
            String author    = (String)obj.get("author");
            String timestamp = (String)obj.get("timestamp");
            // construct the object to look for missing data
            return new Message(body, author, timestamp);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
