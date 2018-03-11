package yahtzee_server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client {
    int id;
    Socket soket;
    ObjectOutputStream sOutput;
    ObjectInputStream sInput;
    Listen listenMe;

    public Client(Socket gelenSoket, int id) {
        this.soket = gelenSoket;
        this.id= id;
        try {
            this.sOutput = new ObjectOutputStream(this.soket.getOutputStream());
            this.sInput = new ObjectInputStream(this.soket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        listenMe = new Listen();
        listenMe.start();

    }
    public void Send(Object message)
    {
        try {
            this.sOutput.writeObject(message.toString());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    class Listen extends Thread {

        public void run() {

            while (true) {
                try {
                    
                    Send("Naber?");
                    Server.Display(id+" -> "+sInput.readObject().toString());
                    
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

}