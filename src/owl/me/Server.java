package owl.me;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Server {
    private static final int PORT = 6666;
    private List<Connection> connections;
    private ServerSocket server;

    private Logger log = Logger.getLogger(Server.class.getName());

    public Server() {
        try {
            server = new ServerSocket(PORT);
            connections = new ArrayList<Connection>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void work() {
        while(true) {
            try {
                Socket socket = server.accept();
                Connection connection = new Connection(this, socket);
                connection.start();
                informEveryone("User " + connection.getUsername() + " joined our chat.");
                connections.add(connection);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void informEveryone(String message) {
        for(Connection connection : connections)
            connection.sendMessage(message);
    }

    public void sendMessage(Connection author, String msg) {
        for(Connection connection : connections) {
            if(author != connection)
                connection.sendMessage(author.getUsername() + ": " + msg);
        }
    }

    public void removeConnection(Connection connection) {
        connections.remove(connection);
        try {
            log.info(connection.getUsername() + " has left.");
            informEveryone("User " + connection.getUsername() + " has left our chat.");
            connection.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
