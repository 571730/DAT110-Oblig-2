package no.hvl.dat110.broker;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import no.hvl.dat110.common.Logger;
import no.hvl.dat110.common.Stopable;
import no.hvl.dat110.messages.*;
import no.hvl.dat110.messagetransport.Connection;

public class Dispatcher extends Stopable {

	private Storage storage;

	public Dispatcher(Storage storage) {
		super("Dispatcher");
		this.storage = storage;

	}

	@Override
	public void doProcess() {

		Collection<ClientSession> clients = storage.getSessions();

		Logger.lg(".");
		for (ClientSession client : clients) {

			Message msg = null;

			if (client.hasData()) {
				msg = client.receive();
			}

			if (msg != null) {
				dispatch(client, msg);
			}
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void dispatch(ClientSession client, Message msg) {

		MessageType type = msg.getType();

		switch (type) {

		case DISCONNECT:
			onDisconnect((DisconnectMsg) msg);
			break;

		case CREATETOPIC:
			onCreateTopic((CreateTopicMsg) msg);
			break;

		case DELETETOPIC:
			onDeleteTopic((DeleteTopicMsg) msg);
			break;

		case SUBSCRIBE:
			onSubscribe((SubscribeMsg) msg);
			break;

		case UNSUBSCRIBE:
			onUnsubscribe((UnsubscribeMsg) msg);
			break;

		case PUBLISH:
			onPublish((PublishMsg) msg);
			break;

		default:
			Logger.log("broker dispatch - unhandled message type");
			break;

		}
	}

	// called from Broker after having established the underlying connection
	public void onConnect(ConnectMsg msg, Connection connection) {

		String user = msg.getUser();

		Logger.log("onConnect:" + msg.toString());

		storage.addClientSession(user, connection);
		// Getting potential unread messages if user is on disconnected list
        // Then deleting the message from buffer
        if (storage.getDisconnectedClients().containsKey(user)){
            for (String id : storage.getDisconnectedClients().get(user)){
                MessageUtils.send(connection, storage.bufferedMessages.get(id));
                Logger.log("sending unread message to " + user);
                storage.bufferedMessages.remove(id);
            }
            // Taking user off the disconnected list
            Logger.log("removing " + user + " from the disconnected list");
            storage.disconnectedClients.remove(user);
        }
	}

	// called by dispatch upon receiving a disconnect message 
	public void onDisconnect(DisconnectMsg msg) {

		String user = msg.getUser();

		Logger.log("onDisconnect:" + msg.toString());

		storage.removeClientSession(user);
		storage.addToDisconnected(user);
	}

	public void onCreateTopic(CreateTopicMsg msg) {

		Logger.log("onCreateTopic:" + msg.toString());

		storage.createTopic(msg.getTopic());

	}

	public void onDeleteTopic(DeleteTopicMsg msg) {

		Logger.log("onDeleteTopic:" + msg.toString());

		storage.deleteTopic(msg.getTopicToBeDeleted());
	}

	public void onSubscribe(SubscribeMsg msg) {

		Logger.log("onSubscribe:" + msg.toString());

		storage.addSubscriber(msg.getUser(), msg.getSubscribeTo());
		
	}

	public void onUnsubscribe(UnsubscribeMsg msg) {

		Logger.log("onUnsubscribe:" + msg.toString());
		
		storage.removeSubscriber(msg.getUser(), msg.getUnsubTo());

	}

	public void onPublish(PublishMsg msg) {

		Logger.log("onPublish:" + msg.toString());

		Collection<ClientSession> clients = storage.getSessions();

		for (ClientSession client : clients){
			if (storage.subscriptions.get(msg.getTopic()).contains(client.getUser())){
				MessageUtils.send(client.getConnection(), msg);
			}
		}
        // Stores the message if subscribed user is offline
		for (String subbedUser : storage.getSubscribers(msg.getTopic())){
		    if (storage.disconnectedClients.containsKey(subbedUser)){
		        storage.addToBufferAndToUnread(msg.getTopic(), msg, subbedUser);
            }
        }
		// Stores the message if any offline clients subscribe to the topic
//		for (String user : storage.getDisconnectedClients()){
//            if (storage.subscriptions.get(msg.getTopic()).contains(user)){
//                storage.addToBufferAndToUnread(msg.getTopic(), msg, user);
//            }
//        }
	}

//	public boolean isUserSubscribed(String user, String topic){
//        for (String u : storage.ge)
//    }
}
