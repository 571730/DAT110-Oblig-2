package no.hvl.dat110.messages;

public class UnsubscribeMsg extends Message {
    private String unsubTo;

    public UnsubscribeMsg(String user, String unsubTo) {
        super(MessageType.UNSUBSCRIBE, user);
        this.unsubTo = unsubTo;
    }

    public String getUnsubTo() {
        return unsubTo;
    }

    public void setUnsubTo(String unsubTo) {
        this.unsubTo = unsubTo;
    }
}
