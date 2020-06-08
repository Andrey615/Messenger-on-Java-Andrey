package client;

import domain.Message;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class Model {
    private ChatMessengerAppl parent;
    private String currentUSer;
    private String loggedUser;
    private String lastMessageText;
    private Set<Message> messages;
    private DefaultListModel<String> users;

    public DefaultListModel<String> getUsers() {
        if(users.size() == 0){
            users = new DefaultListModel<String>();
        }
        return users;
    }

    public void setUsers(DefaultListModel<String> users) {
        this.users = users;
        parent.getChatPanelView(false).updateUsers();
    }

    private Long lastMessageId;
    private String serverIpAddress = "127.0.0.1";

    private Model(){}

    public static Model getInstance() {
        return ModelHolder.INSTANCE;
    }

    public String messagesToString() {
        return messages.toString();
    }

    //added by me
    public String getFilteredStringOfMessages(Set<Message> messages){
        return buildStringOfFilteredMessages(messages.stream().filter(p -> p.getUserNameTo().equals(getLoggedUser())
                && p.getUserNameFrom().equals(getCurrentUSer())
                || p.getUserNameTo().equals(getCurrentUSer())
                && p.getUserNameFrom().equals(getLoggedUser()))
                .collect(Collectors.toSet()));
    }

    public String buildStringOfFilteredMessages(Set<Message> messages){
        List<Message> messagesSorted = messages.stream().collect(Collectors.toList());
        Collections.sort(messagesSorted, (x, y) -> x.getMoment().compareTo(y.getMoment()));
        StringBuilder result = new StringBuilder("<html><body id = 'body'>");
        for(Message message: messagesSorted){
            result.append(message.toString()).append("\n");
        }
        return result.append("</body></html>").toString();
    }
    //

    public void addMessages(List<Message> messages) {
        this.getMessages().addAll(messages);
        parent.getChatPanelView(false).getMessagesTextPane().setText(getFilteredStringOfMessages(getMessages()));
    }

    private static class  ModelHolder {
        private static final Model INSTANCE = new Model();
    }

    public void initialize(){
        setMessages(new TreeSet<Message>(){
            @Override
            public String toString() {
                StringBuilder result = new StringBuilder("<html><body id = 'body'>");
                Iterator<Message> i = iterator();
                while (i.hasNext()){
                    result.append(i.next().toString()).append("\n");
                }
                return result.append("</body></html>").toString();
            }
        });
        lastMessageId = 0L;
        currentUSer = "";
        loggedUser = "";
        lastMessageText = "";
    }

    // getters and setters
    public ChatMessengerAppl getParent() {
        return parent;
    }

    public void setParent(ChatMessengerAppl parent) {
        this.parent = parent;
    }

    public String getCurrentUSer() {
        return currentUSer;
    }

    public void setCurrentUSer(String currentUSer) {
        this.currentUSer = currentUSer;
    }

    public String getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(String loggedUser) {
        this.loggedUser = loggedUser;
    }

    public String getLastMessageText() {
        return lastMessageText;
    }

    public void setLastMessageText(String lastMessageText) {
        this.lastMessageText = lastMessageText;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public Long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public String getServerIpAddress() {
        return serverIpAddress;
    }

    public void setServerIpAddress(String serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }
}
