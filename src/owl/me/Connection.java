package owl.me;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;

public class Connection extends Thread{
    private String username;
    private Server server;
    final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private static Logger log  = Logger.getLogger(Connection.class.getName());

    public Connection(Server server, Socket socket) {
        this.socket = socket;
        this.server = server;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            while((username = in.readLine()) == null) {}
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Connected " + username);
    }

    @Override
    public void run() {
        String message;
        while(true) {
            try {
                while ((message = in.readLine()) != null) {
                    if (!message.equals("exit")) {
                        System.out.println(message);
                        server.sendMessage(this, message);
                    } else
                        server.removeConnection(this);
                }
            } catch(SocketException e) {
                server.removeConnection(this);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String msg) {
        out.println(msg);
        out.flush();
    }

}
