package client;

import domain.Message;
import domain.xml.MessageParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;
import server.ChatMessengerServer;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static server.ServerThread.*;
import static server.ServerThread.METHOD_PUT_USER;

public class Utility {
    final static Logger LOGGER = LogManager.getLogger(Utility.class);
    public static<T extends Container> T findParent(Component comp, Class<T> clazz) {
        if (comp == null){
            return null;
        }
        if (clazz.isInstance(comp)){
            return (clazz.cast(comp));
        }
        else{
            return findParent(comp.getParent(), clazz);
        }
    }

    public static void putUser(ChatMessengerAppl appl){
        InetAddress addr;
        try {
            addr = InetAddress.getByName(appl.getModel().getServerIpAddress());
            try(Socket socket = new Socket(addr, ChatMessengerServer.PORT);
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ){
                out.println(METHOD_PUT_USER);
                out.println(appl.getModel().getLoggedUser());
                out.flush();
                String responseLine = in.readLine();
                if (responseLine.equals("OK")){
                    LOGGER.debug("Add user");
                }
                else {
                    LOGGER.debug("User exits");
                }

            } catch (IOException e) {
                LOGGER.debug("Socket error: " + e.getMessage());
            }
        } catch (UnknownHostException e) {
            LOGGER.debug("Unknown hist address: "+ e.getMessage());
        }
    }

    public static void messagesUpdate(ChatMessengerAppl appl) {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(appl.getModel().getServerIpAddress());
            try(Socket socket = new Socket(addr, ChatMessengerServer.PORT);
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ){
                Model model = appl.getModel();
                out.println(METHOD_GET);
                out.println(model.getLastMessageId());
                out.flush();
                String responseLine = in.readLine();
                StringBuilder mesStr = new StringBuilder();
                while (! END_LINE_MESSAGE.equals(responseLine)){
                    mesStr.append(responseLine);
                    responseLine = in.readLine();
                }
                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                SAXParser parser = parserFactory.newSAXParser();
                List<Message> messages = new ArrayList<Message>(){
                    @Override
                    public String toString() {
                        return this.stream().map(Message::toString).collect(Collectors.joining("\n"));
                    }
                };
                AtomicInteger id = new AtomicInteger(0);
                MessageParser saxp = new MessageParser(id, messages);
                parser.parse(new ByteArrayInputStream(mesStr.toString().getBytes()), saxp);
                if (messages.size() > 0){
                    model.addMessages(messages);
                    model.setLastMessageId(id.longValue());
                    LOGGER.trace("List off new messages: " + messages.toString());
                }
            } catch (IOException e) {
                LOGGER.error("Socket error: " + e.getMessage());
            } catch (SAXException | ParserConfigurationException e) {
                LOGGER.error("Parse exception: " + e.getMessage());
            }
        } catch (UnknownHostException e) {
            LOGGER.error("Unknown hist address: "+ e.getMessage());
        }
    }
    public static void usersUpdate(ChatMessengerAppl appl){
        InetAddress addr;
        try {
            addr = InetAddress.getByName(appl.getModel().getServerIpAddress());
            try(Socket socket = new Socket(addr, ChatMessengerServer.PORT);
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ){
                Model model = appl.getModel();
                out.println(METHOD_GET_USER);
                out.flush();
                String responseLine = in.readLine();
                DefaultListModel<String> splitedString = new DefaultListModel();
                for (String user: responseLine.split(";")){
                    splitedString.addElement(user);
                }
                model.setUsers(splitedString);

            } catch (IOException e) {
                LOGGER.error("Socket error: " + e.getMessage());
            }
        } catch (UnknownHostException e) {
            LOGGER.error("Unknown hist address: "+ e.getMessage());
        }
    }
}
