package client;

import java.util.TimerTask;

public class UpdateMessageTask extends TimerTask {
    ChatMessengerAppl appl;
    public UpdateMessageTask(ChatMessengerAppl appl) {
        this.appl = appl;
    }

    @Override
    public void run() {
        Utility.messagesUpdate(appl);
        Utility.usersUpdate(appl);
    }
}
