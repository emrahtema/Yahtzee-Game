package game;

public class Message implements java.io.Serializable {

    public static enum Message_Type {
        None, Startt, Disconnect, RivalConnected, Text, Selected, Bitis, Start,
    }
    public Message_Type type;
    public Object content;

    public Message(Message_Type t) {
        this.type = t;
    }
}
