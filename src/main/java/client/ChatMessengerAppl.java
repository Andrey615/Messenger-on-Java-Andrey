package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;

public class ChatMessengerAppl extends JFrame {
    public static final short DELAY = 100;
    public static final short PERIOD = 1000;
    final static Logger LOGGER = LogManager.getLogger(ChatMessengerAppl.class);

    private static final Model MODEL;
    private static final Controller CONTROLLER;
    private static final ViewFactory VIEWS;
    public static final int FRAME_WIDTH = 600;
    public static final int FRAME_HEIGHT = 600;

    static {
        MODEL = Model.getInstance();
        CONTROLLER = Controller.getInstance();
        VIEWS = ViewFactory.getInstance();
        LOGGER.trace("MVC instantiated" + MODEL + ";" + CONTROLLER + ";" + VIEWS);
    }

    private Timer timer;

    public ChatMessengerAppl(){
        super();
        initialize();
    }

    public static void main(String[] args) {
        JFrame frame = new ChatMessengerAppl();
        frame.setVisible(true);
        frame.repaint();

    }

    private void initialize() {
        AbstractView.setParent(this);
        MODEL.setParent(this);
        MODEL.initialize();
        CONTROLLER.setParent(this);
        VIEWS.viewRegister("login", LoginPanelView.getInstance());
        VIEWS.viewRegister("chat", ChatPanelView.getInstance());
        timer = new Timer("Server request for update messages");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(FRAME_WIDTH / 2, FRAME_HEIGHT / 4);
        this.setLocationRelativeTo(null);
        this.setTitle("Chat Messenger");
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(getLoginPanelView(), BorderLayout.CENTER);
        this.setContentPane(contentPanel);
    }

    public LoginPanelView getLoginPanelView() {
        LoginPanelView loginPanelView = VIEWS.getView("login");
        loginPanelView.initModel();
        return  loginPanelView;
    }

    public ChatPanelView getChatPanelView(boolean doGetMessages) {
        ChatPanelView chatPanelView = VIEWS.getView("chat");
        chatPanelView.initModel(doGetMessages);
        return chatPanelView;
    }

    public static Model getModel() {
        return MODEL;
    }

    public static Controller getController() {
        return CONTROLLER;
    }

    public static ViewFactory getViews() {
        return VIEWS;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    private void showPanel(JPanel panel){
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setVisible(true);
        panel.repaint();

    }

    public void showChatPanelView() {
        getChatPanelView(false).showMessages();
        showPanel(getChatPanelView(true));
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setLocationRelativeTo(null);
    }

    public void showLoginPanelView() {
        showPanel(getLoginPanelView());
        InputMap im = getLoginPanelView().getLoginButton().getInputMap();
        im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        im.put(KeyStroke.getKeyStroke("released ENTER"), "released");
        this.setSize(FRAME_WIDTH / 2, FRAME_HEIGHT/ 4);
        this.setLocationRelativeTo(null);
    }
}
