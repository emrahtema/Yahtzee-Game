package yahtzee_server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

// dinleme threadi
class ServerThread extends Thread {

    public void run() {

        while (true) {
            try {
                Server.Display("Client Bekleniyor...");
                Socket clientSocket = Server.serverSocket.accept();
                Server.Display("Client Geldi...");
                Client nclient = new Client(clientSocket, Server.IdClient);
                Server.IdClient++;
                Server.Clients.add(nclient);

            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

public class Server {

    //server soketi eklemeliyiz
    public static ServerSocket serverSocket;
    public static int IdClient=0;
    // Serverın dileyeceği port
    public static int port = 0;
    //Serverı sürekli dinlemede tutacak thread nesnesi
    public static ServerThread runThread;
    public static ArrayList<Client> Clients = new ArrayList<>();
    public static void Start(int openport) {
        try {
            Server.port = openport;
            // serversoket nesnesi
            Server.serverSocket = new ServerSocket(Server.port);

            Server.runThread = new ServerThread();
            Server.runThread.start();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void Display(String msg) {
        System.out.println(msg);
    }
}