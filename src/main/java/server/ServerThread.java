package server;

import domain.Message;
import domain.xml.MessageBuilder;
import domain.xml.MessageParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.Map.Entry;

public class ServerThread extends Thread {
    final static Logger LOGGER = LogManager.getLogger(ServerThread.class);
    private final AtomicInteger messageid;
    private final Map<Long, Message> messageList;
    private final List<String> usersList;
    public static final String METHOD_GET = "GET";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_PUT_USER = "PUT_USER";
    public static final String METHOD_GET_USER = "GET_USER";
    public static final String END_LINE_MESSAGE = "END";
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;


    public AtomicInteger getMessageid() {
        return messageid;
    }


    public Map<Long, Message> getMessageList() {
        return messageList;
    }
    public List<String> getUsersList(){return usersList;}


    public ServerThread(Socket socket, AtomicInteger messageid, Map<Long, Message> messagesList, List<String> usersList) throws IOException {
        this.socket = socket;
        this.messageid = messageid;
        this.messageList = messagesList;
        this.usersList = usersList;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        start();
    }

    @Override
    public void run() {
        try {
            LOGGER.debug("New server thread starting");
            String requestLine = in.readLine();
            LOGGER.debug("request" + requestLine);
            switch (requestLine){
                case METHOD_GET:
                    LOGGER.debug("get: ");
                    final Long lastId = Long.valueOf(in.readLine());
                    LOGGER.debug("last id" + lastId);
                    List<Message> lastNotSeenMessages =
                            messageList.entrySet().stream().filter(message -> message.getKey().compareTo(lastId) > 0).map(Entry::getValue).collect(Collectors.toList());
                    LOGGER.debug("messages:" + lastNotSeenMessages);

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.newDocument();


                    String xmlContent = MessageBuilder.buildDocument(document, lastNotSeenMessages);
                    LOGGER.trace("Echoing:" + xmlContent);
                    out.println(xmlContent);
                    out.println(END_LINE_MESSAGE);
                    out.flush();
                    break;
                case METHOD_PUT:
                    LOGGER.debug("put:" );
                    requestLine = in.readLine();
                    StringBuilder mesStr = new StringBuilder();
                    while (! END_LINE_MESSAGE.equals(requestLine)){
                        mesStr.append(requestLine);
                        requestLine = in.readLine();
                    }
                    LOGGER.debug(mesStr);
                    SAXParserFactory parsefactory = SAXParserFactory.newInstance();
                    SAXParser parser = parsefactory.newSAXParser();
                    List<Message> messages = new ArrayList<>();
                    MessageParser saxp = new MessageParser(messageid, messages);
                    InputStream is = new ByteArrayInputStream(mesStr.toString().getBytes());
                    parser.parse(is, saxp);
                    for (Message message: messages){
                        messageList.put(message.getId(), message);
                    }
                    LOGGER.trace("Echoing: " + messages);
                    out.println("OK");
                    out.flush();
                    out.close();
                    break;
                case METHOD_GET_USER:
                    String users = "";
                    for (String user: usersList){
                        users += user + ";";
                    }
                    LOGGER.trace("Echoing:" + users);
                    out.println(users);
                    out.flush();
                    break;

                case METHOD_PUT_USER:
                    LOGGER.debug("put user");
                    requestLine = in.readLine();
                    if(!(usersList.contains(requestLine))){
                        usersList.add(requestLine);
                        out.println("OK");
                    }
                    else {
                        out.println("Not OK");
                    }
                    out.flush();
                    out.close();
                    break;
                default:
                    LOGGER.info("Unknown request" + requestLine);
                    out.println("BAD REQUEST");
                    out.flush();
                    break;

            }

        }
        catch (Exception e){
            LOGGER.error(e.getMessage());
            out.println("Error");
            out.flush();
        } finally {
            try {
                LOGGER.debug("SOCKET is closed");
                LOGGER.debug("Close object stream");
                in.close();
                out.close();
                socket.close();
            } catch (IOException e){
                LOGGER.error("Socket not close");
            }
        }
    }
}
