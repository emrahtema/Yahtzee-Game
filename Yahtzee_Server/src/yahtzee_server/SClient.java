package yahtzee_server;

import game.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static game.Message.Message_Type.Play;
import static java.lang.Thread.sleep;

public class SClient {

    int id;
    Socket soket;
    ObjectOutputStream sOutput;
    ObjectInputStream sInput;
    //clientten gelenleri dinleme threadi
    Listen listenThread;
    //cilent eşleştirme thredi
    PairingThread pairThread;
    //rakip client
    SClient rival;
    //eşleşme durumu
    public boolean paired = false;

    public SClient(Socket gelenSoket, int id) {
        this.soket = gelenSoket;
        this.id = id;
        try {
            this.sOutput = new ObjectOutputStream(this.soket.getOutputStream());
            this.sInput = new ObjectInputStream(this.soket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        //thread nesneleri
        this.listenThread = new Listen(this);
        this.pairThread = new PairingThread(this);

    }

    //client mesaj gönderme
    public void Send(Message message) {
        try {
            this.sOutput.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //client dinleme threadi
    //her clientin ayrı bir dinleme thredi var
    class Listen extends Thread {

        SClient TheClient;
        //thread nesne alması için yapıcı metod
        Listen(SClient TheClient) {
            this.TheClient = TheClient;
        }

        public void run() {
            //client bağlı olduğu sürece dönsün
            while (TheClient.soket.isConnected()) {
                try {
                    //mesajı bekleyen kod satırı
                    Message received = (Message) (TheClient.sInput.readObject());
                    //mesaj gelirse bu satıra geçer
                    //mesaj tipine göre işlemlere ayır                 
                    switch (received.type) {
                        case Start:
                            //eşleştirme işlemine başla
                            TheClient.pairThread.start();
                            break;
                        case Disconnect:
                            break;
                        case Play:
                            //gelen seçim yapıldı mesajını rakibe gönder
                            Server.Send(TheClient.rival, received);
                            break;
                        case Bitis:
                            break;
                    }

                } catch (Exception e) {
                    //client bağlantısı koparsa listeden sil
                    Server.Clients.remove(TheClient);
                    Message msg = new Message(Message.Message_Type.Play);
                    msg.content = "Start|-1~";//rakip oyundan çıktı mesajı
                    Server.Send(TheClient.rival, msg);
                    Server.Clients.remove(TheClient.rival);
                    TheClient.rival.pairThread.stop();
                    TheClient.rival.listenThread.stop();
                    break;
                }
            }
        }
    }

    //eşleştirme threadi
    //her clientin ayrı bir eşleştirme thredi var
    class PairingThread extends Thread {

        SClient TheClient;
        PairingThread(SClient TheClient) {
            this.TheClient = TheClient;
        }

        public void run() {
            //client bağlı ve eşleşmemiş olduğu durumda dön
            while (TheClient.soket.isConnected() && !TheClient.paired) {
                try {
                    //lock mekanizması
                    //sadece bir client içeri grebilir
                    //diğerleri release olana kadar bekler
                    Server.pairTwo.acquire(1);
                    
                    //client eğer eşleşmemişse gir
                    if (!TheClient.paired) {
                        SClient crival = null;
                        //eşleşme sağlanana kadar dön
                        while (crival == null && TheClient.soket.isConnected()) {
                            //liste içerisinde eş arıyor
                            for (SClient clnt : Server.Clients) {
                                if (TheClient != clnt && clnt.rival == null) {
                                    //eşleşme sağlandı ve gerekli işaretlemeler yapıldı
                                    crival = clnt;
                                    crival.paired = true;
                                    crival.rival = TheClient;
                                    TheClient.rival = crival;
                                    TheClient.paired = true;
                                    break;
                                }
                            }
                            //sürekli dönmesin 1 saniyede bir dönsün
                            //thredi uyutuyoruz
                            sleep(1000);
                        }
                        //eşleşme oldu
                        //her iki tarafada eşleşme mesajı gönder 
                        //oyunu başlat
                        Message msg1 = new Message(Message.Message_Type.RivalConnected);
                        msg1.content = "Durum: Oyun Bulundu...";
                        Server.Send(TheClient.rival, msg1);

                        Message msg2 = new Message(Message.Message_Type.RivalConnected);
                        msg2.content = "Durum: Oyun Bulundu...";
                        Server.Send(TheClient, msg2);
                        
                        //ilk kim başlıyor, tabiki ilk bağlanan o kadar beklemiş
                        msg1 = new Message(Message.Message_Type.Play);
                        msg1.content = "Start|0~";
                        Server.Send(TheClient.rival, msg1);

                        msg2 = new Message(Message.Message_Type.Play);
                        msg2.content = "Start|1~";
                        Server.Send(TheClient, msg2);
                    }
                    //lock mekanizmasını servest bırak
                    //bırakılmazsa deadlock olur.
                    Server.pairTwo.release(1);
                } catch (InterruptedException ex) {
                    //do nothing
                }
            }
        }
    }
}
