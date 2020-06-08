package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChatPanelView extends AbstractView {
    final static Logger LOGGER = LogManager.getLogger(ChatPanelView.class);
    public static final String SEND_ACTION_COMMAND = "send";
    public static final String LOGOUT_ACTION_COMMAND = "logout";
    private JScrollPane messagesListPanel;
    private JTextPane messagesTextPane;
    private JPanel textMessagePanel;
    private JButton sendMessageButton;
    private JTextField textMessageField;
    private JButton logoutButton;
    private JLabel promptLabel;
    // added by me
    private JScrollPane usersListPanel;
    private JList usersList;
    public static String messagesTextPaneColor = "#FFD386";
    public static String usersListPaneColor = "#A80000";
    private static String headerColor = "#69F3FF";

    private ChatPanelView(){
        super();
        initialize();
    }

    public static ChatPanelView getInstance() {
        return ChatPanelViewHolder.INSTANCE;
    }


    private static class ChatPanelViewHolder {
        private static final ChatPanelView INSTANCE = new ChatPanelView();
    }

    @Override
    public void initialize() {
        this.setName("chatPanelView");
        this.setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        header.add(getPromptLabel(), BorderLayout.WEST);
        header.add(getLogoutButton(), BorderLayout.EAST);
        header.setBackground(Color.decode(headerColor));
        // added by me
        JPanel center = new JPanel(new BorderLayout());
        center.add(getMessagesListPanel(), BorderLayout.CENTER);
        center.add(getUsersListPanel(), BorderLayout.EAST);
        //
        this.add(header, BorderLayout.NORTH);
        this.add(center, BorderLayout.CENTER);
        this.add(getTextMessagePanel(), BorderLayout.SOUTH);
        InputMap im = getSendMessageButton().getInputMap();
        im.put(KeyStroke.getKeyStroke("ENTER"), "pressed");
        im.put(KeyStroke.getKeyStroke("released ENTER"), "released");
    }

    @Override
    public void clearFields() {
        getMessagesTextPane().setText("");
        getTextMessageField().setText("");
    }

    public void initModel(boolean getMessages){
        parent.getModel().setLastMessageText("");
        getPromptLabel().setText("Hello, " + parent.getModel().getLoggedUser() + "!");
        getTextMessageField().requestFocusInWindow();
        parent.getRootPane().setDefaultButton(getSendMessageButton());
    }

    public void updateUsers(){
        getUsersList().setModel(parent.getModel().getUsers());
        getUsersList().setSelectedIndex(parent.getModel().getUsers().indexOf(parent.getModel().getCurrentUSer()));
    }


    public JScrollPane getMessagesListPanel() {
        if(messagesListPanel == null){
             messagesListPanel = new JScrollPane(getMessagesTextPane());
             messagesListPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        return messagesListPanel;
    }

    // added by me
    public JScrollPane getUsersListPanel(){
        if (usersListPanel == null){
            usersListPanel = new JScrollPane(getUsersList());
            usersListPanel.setSize(getMaximumSize());
            usersListPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        return usersListPanel;
    }

    public JList getUsersList() {
        if (usersList == null){
            usersList = new JList(new DefaultListModel());
            usersList.setFont(new Font("Times New Roman", Font.BOLD, 16));
            usersList.setBackground(Color.decode(usersListPaneColor));
            usersList.setForeground(Color.decode("#FFFFFF"));
            usersList.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    if (evt.getClickCount() == 1) {
                        parent.getModel().setCurrentUSer(usersList.getSelectedValue().toString());
                        showMessages();
                    }
                }
            });
            usersList.setFixedCellHeight(30);
        }
        return usersList;
    }

    public void showMessages(){
        getMessagesTextPane().setText(parent.getModel().getFilteredStringOfMessages(parent.getModel().getMessages()));
    }
    //

    private JLabel getPromptLabel(){
        if (promptLabel == null){
            promptLabel = new JLabel("Hello," + parent.getModel().getLoggedUser() + "!");
        }
        return promptLabel;
    }

    public JTextPane getMessagesTextPane() {
        if (messagesTextPane == null){
            messagesTextPane = new JTextPane();
            messagesTextPane.setContentType("text/html");
            messagesTextPane.setEditable(false);
            messagesTextPane.setName("messagesTextArea");
            messagesTextPane.setBackground(Color.decode(messagesTextPaneColor));
            ((DefaultCaret)messagesTextPane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        }
        return messagesTextPane;
    }

    public JPanel getTextMessagePanel() {
        if (textMessagePanel == null){
            textMessagePanel = new JPanel();
            textMessagePanel.setLayout(new BoxLayout(textMessagePanel, BoxLayout.X_AXIS));
            textMessagePanel.setBackground(Color.decode(headerColor));
            addLabeledField(textMessagePanel, "Enter message", getTextMessageField());
            textMessagePanel.add(getSendMessageButton());
        }
        return textMessagePanel;
    }

    public JButton getSendMessageButton() {
        if (sendMessageButton == null){
            sendMessageButton = new JButton();
            sendMessageButton.setText("Send");
            sendMessageButton.setName("sendMessageButton");
            sendMessageButton.setActionCommand(SEND_ACTION_COMMAND);
            sendMessageButton.addActionListener(parent.getController());
        }
        return sendMessageButton;
    }

    public JTextField getTextMessageField() {
        if (textMessageField == null){
            textMessageField = new JTextField(12);
            textMessageField.setName("textMessageField");
        }
        return textMessageField;
    }

    public JButton getLogoutButton() {
        if (logoutButton == null){
            logoutButton = new JButton();
            logoutButton.setText("Logout");
            logoutButton.setName("logoutButton");
            logoutButton.setActionCommand(LOGOUT_ACTION_COMMAND);
            logoutButton.addActionListener(parent.getController());
        }
        return logoutButton;
    }

}
