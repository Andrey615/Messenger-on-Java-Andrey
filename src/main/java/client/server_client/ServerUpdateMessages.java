package client.server_client;


import java.util.TimerTask;

public class ServerUpdateMessages extends TimerTask {
    ChatMessengerServerApplication appl;
    public ServerUpdateMessages(ChatMessengerServerApplication appl) {
        this.appl = appl;
    }

    @Override
    public void run() {
        ServerUtility.messagesUpdate(appl);
        appl.getMessagesPane().setText(appl.messagesToStringAndFilter(appl.getSelectedUsersSpeech()));
    }
}
