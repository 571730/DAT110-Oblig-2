package no.hvl.dat110.messages;

public class DeleteTopicMsg extends Message {
	private String topicToBeDeleted;

    public DeleteTopicMsg(String user, String topicToBeDeleted) {
        super(MessageType.DELETETOPIC, user);
        this.topicToBeDeleted = topicToBeDeleted;
    }

    public String getTopicToBeDeleted() {
        return topicToBeDeleted;
    }

    public void setTopicToBeDeleted(String topicToBeDeleted) {
        this.topicToBeDeleted = topicToBeDeleted;
    }

    @Override
    public String toString() {
        return super.toString() + " topic to delete: " + this.topicToBeDeleted;
    }
}
