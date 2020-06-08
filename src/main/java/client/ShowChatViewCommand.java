package client;

import server.ChatMessengerServer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;


public class ShowChatViewCommand implements Command {
    private ChatMessengerAppl appl;
    private LoginPanelView view;
    public ShowChatViewCommand(ChatMessengerAppl parent, LoginPanelView view) {
        appl = parent;
        this.view = view;
    }



    @Override
    public void execute() {
        Utility.messagesUpdate(appl);
        appl.getModel().setLoggedUser(view.getUserNameField().getText());
        Utility.putUser(appl);
        Utility.usersUpdate(appl);
        view.clearFields();
        view.setVisible(false);
        appl.setTimer(new Timer());
        appl.getTimer().scheduleAtFixedRate(new UpdateMessageTask(appl), ChatMessengerAppl.DELAY, ChatMessengerAppl.PERIOD);
        appl.showChatPanelView();
    }
}
