package no.hvl.dat110.broker;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import no.hvl.dat110.messages.Message;
import no.hvl.dat110.messagetransport.Connection;

public class Storage {

    protected ConcurrentHashMap<String, Set<String>> subscriptions;
    protected ConcurrentHashMap<String, ClientSession> clients;
    protected ConcurrentHashMap<String, Set<String>> disconnectedClients;
    protected ConcurrentHashMap<String, Message> bufferedMessages;

    public Storage() {
        subscriptions = new ConcurrentHashMap<String, Set<String>>();
        clients = new ConcurrentHashMap<String, ClientSession>();
        //disconnectedClients = new ArrayList<>();
        disconnectedClients = new ConcurrentHashMap<>();
        bufferedMessages = new ConcurrentHashMap<>();
    }

    public Collection<ClientSession> getSessions() {
        return clients.values();
    }

    public Set<String> getTopics() {

        return subscriptions.keySet();

    }

    public ConcurrentHashMap<String, Set<String>> getDisconnectedClients() {
        return disconnectedClients;
    }


    public ClientSession getSession(String user) {

        ClientSession session = clients.get(user);

        return session;
    }

    public Set<String> getSubscribers(String topic) {

        return (subscriptions.get(topic));

    }

    public void addClientSession(String user, Connection connection) {
        clients.put(user, new ClientSession(user, connection));
    }

    public void addToDisconnected(String user) {
        disconnectedClients.put(user, new HashSet<>());
    }


    public void addToBufferAndToUnread(String topic, Message msg, String user) {
        String uniqueID = UUID.randomUUID().toString();
        disconnectedClients.get(user).add(uniqueID);
        bufferedMessages.put(uniqueID, msg);
    }

    public void removeClientSession(String user) {
        clients.remove(user);
    }

    public void createTopic(String topic) {
        subscriptions.put(topic, new HashSet<>());
    }

    public void deleteTopic(String topic) {
        subscriptions.remove(topic);
    }

    public void addSubscriber(String user, String topic) {
        subscriptions.get(topic).add(user);
    }

    public void removeSubscriber(String user, String topic) {
        subscriptions.get(topic).remove(user);
    }
}
