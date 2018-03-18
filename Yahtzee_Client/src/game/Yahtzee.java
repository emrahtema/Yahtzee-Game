package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import yahtzee_client.Client;

//oyunla ilgili durum mesajı gönderimi sonra eklencek.
//Message msg = new Message(Message.Message_Type.Selected);
//msg.content = myselection;
//Client.Send(msg);

public class Yahtzee extends javax.swing.JFrame {
    //framedeki komponentlere erişim için satatik oyun değişkeni
    public static Yahtzee ThisGame;
    //ekrandaki resim değişimi için timer yerine thread
    public Thread tmr_slider;
    ImageIcon zar_resimleri[];
    ImageIcon table;
    Random rand;
    private BufferedImage tablo;
    private int[] skor = new int[16];
    private int[] rakipSkor = new int[16];
    private int[] zarlarinDegerleri = new int[5];
    private int[] ortadakiZarlarinDegerleri = new int[5];
    public String islem="";
    public int islemSirasi=1;
    private boolean sira=false;
    Operations op;
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(tablo, 0, 0, null);
    }
    /**
     * Creates new form Yahtzee
     */
    public Yahtzee() {
        initComponents();
        op = new Operations();
        ThisGame = this;
        try {
            zar_resimleri = new ImageIcon[7];//rakibin zar resimleri için
            zar_resimleri[0] = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/none.png"))).getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT));
            zar_resimleri[1] = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/1.png"))).getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT));
            zar_resimleri[2] = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/2.png"))).getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT));
            zar_resimleri[3] = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/3.png"))).getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT));
            zar_resimleri[4] = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/4.png"))).getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT));
            zar_resimleri[5] = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/5.png"))).getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT));
            zar_resimleri[6] = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/6.png"))).getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT));
            zarla.setIcon(new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/dice.png"))).getImage().getScaledInstance(150, 50, Image.SCALE_DEFAULT)));
            
            table = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/table.png"))).getImage().getScaledInstance(302, 573, Image.SCALE_DEFAULT));
            jLabel1.setIcon(table);
        } catch (Exception e) {
            System.out.println(e);
        }

        // resimleri döndürmek için tread aynı zamanda oyun bitiminide takip ediyor
        tmr_slider = new Thread(() -> {
            //soket bağlıysa dönsün
            while (Client.socket.isConnected()) {
                try {
                    Thread.sleep(100);
                    //eğer islem stringine bir mesaj gelmişse yani boş değilse
                    //aşağıdaki işlemleri yapsın.
                    if(!islem.equals("")){
                        //birden çok mesaj varsa bölelim.
                        String[] msg = islem.split("~");
                        islem="";
                        for(String str : msg){
                            //her mesajın içeriğini bölelim.
                            //mesaj Start ile başlıyorsa oyuna kim başlayacak onu söyler.
                            if(str.equals("Start|1")){
                                //biz başlıyoruz.
                                sira = true;
                            }else{
                                //rakip başlıyor.
                                sira = false;
                            }
                        }
                    }
                    if(sira){
                        durum.setText("Durum: Oynama Sırası Sende.");
                        //sira trueyse sıra bizdedir.ona göre işlemler yap
                        bizimPanel.setEnabled(true);
                        rakipPanel.setEnabled(false);
                        if(islemSirasi==1){//ilk islem masadaki zarları kendimize almak.
                            ilk_sira_zarlari_at();
                            zarla.setEnabled(true);
                            islemSirasi++;
                        }
                    }else{
                        durum.setText("Durum: Oynama Sırası Rakipte.");
                        //sira rakiptedir ona göre işlemler yap.
                        rakipPanel.setEnabled(true);
                        bizimPanel.setEnabled(false);
                    }
                    //oyun bitimi takibi buradan yapılabilir.
                    //tmr_slider.stop();
                    //7 saniye sonra oyun bitsin tekrar bağlansın
                    //Thread.sleep(7000);
                    //Reset();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
    }

    //elimizdeki zarlara göre puanları hesaplayıp getiren metod.
    public void zarlara_gore_puanlari_getir(){
        op = new Operations();
        int[] puanlar = op.puanlariGetir(zarlarinDegerleri);
        System.out.println("okey geldi puanlar");
        if(skor[0] == -1){
            benpuan1.setText(String.valueOf(puanlar[0]));
            benpuan1.setBackground(Color.yellow);
        }
        if(skor[1] == -1){
            benpuan2.setText(String.valueOf(puanlar[1]));
            benpuan2.setBackground(Color.yellow);
        }
        if(skor[2] == -1){
            benpuan3.setText(String.valueOf(puanlar[2]));
            benpuan3.setBackground(Color.yellow);
        }
        if(skor[3] == -1){
            benpuan4.setText(String.valueOf(puanlar[3]));
            benpuan4.setBackground(Color.yellow);
        }
        if(skor[4] == -1){
            benpuan5.setText(String.valueOf(puanlar[4]));
            benpuan5.setBackground(Color.yellow);
        }
        if(skor[5] == -1){
            benpuan6.setText(String.valueOf(puanlar[5]));
            benpuan6.setBackground(Color.yellow);
        }
        
        benpuan7.setText(String.valueOf(skor[6]));
        
        if(skor[7] == -1){
            benpuan8.setText(String.valueOf(puanlar[6]));
            benpuan8.setBackground(Color.yellow);
        }
        if(skor[8] == -1){
            benpuan9.setText(String.valueOf(puanlar[7]));
            benpuan9.setBackground(Color.yellow);
        }
        if(skor[9] == -1){
            benpuan10.setText(String.valueOf(puanlar[8]));
            benpuan10.setBackground(Color.yellow);
        }
        if(skor[10] == -1){
            benpuan11.setText(String.valueOf(puanlar[9]));
            benpuan11.setBackground(Color.yellow);
        }
        if(skor[11] == -1){
            benpuan12.setText(String.valueOf(puanlar[10]));
            benpuan12.setBackground(Color.yellow);
        }
        if(skor[12] == -1){
            benpuan13.setText(String.valueOf(puanlar[11]));
            benpuan13.setBackground(Color.yellow);
        }
        if(skor[13] == -1){
            benpuan14.setText(String.valueOf(puanlar[12]));
            benpuan14.setBackground(Color.yellow);
        }
        
        benpuan15.setText(String.valueOf(skor[14]));
        benpuan16.setText(String.valueOf(skor[15]));
        
    }
    
    //eğer masada hiç zar kalmamışsa, iyi kötü hepsini elimize almışsak
    //bunu kontrol edip tespit ediyoruz.
    //eğer tespit edilirse, elimizdeki zarlara göre seçebileceğimiz
    //puanları getirecek metodu çağırıyoruz.
    public void kontrol_et(){
        int sayac=0;
        for(int i:ortadakiZarlarinDegerleri)
            sayac+=i;
        if(sayac==0 && islemSirasi==5){
            zarlara_gore_puanlari_getir();
        }
    }
    
    public void zarlari_at(){
        //önce masadaki zarlara rasgele değerler alalım.
        //sonra bu değerlere ait olan zar resimlerini alalım.
        //masadaki zar null ise kenara ayırmış olabiliriz.
        if(ortadakiZarlarinDegerleri[0]!=0){
            ortadakiZarlarinDegerleri[0]=op.randomZarGetir();
            masazar1.setIcon(zar_resimleri[ortadakiZarlarinDegerleri[0]]);
        }
        if(ortadakiZarlarinDegerleri[1]!=0){
            ortadakiZarlarinDegerleri[1]=op.randomZarGetir();
            masazar2.setIcon(zar_resimleri[ortadakiZarlarinDegerleri[1]]);
        }
        if(ortadakiZarlarinDegerleri[2]!=0){
            ortadakiZarlarinDegerleri[2]=op.randomZarGetir();
            masazar3.setIcon(zar_resimleri[ortadakiZarlarinDegerleri[2]]);
        }
        if(ortadakiZarlarinDegerleri[3]!=0){
            ortadakiZarlarinDegerleri[3]=op.randomZarGetir();
            masazar4.setIcon(zar_resimleri[ortadakiZarlarinDegerleri[3]]);
        }
        if(ortadakiZarlarinDegerleri[4]!=0){
            ortadakiZarlarinDegerleri[4]=op.randomZarGetir();
            masazar5.setIcon(zar_resimleri[ortadakiZarlarinDegerleri[4]]);
        }
    }
    
    public void elindeki_zarlari_at(){
        ortadakiZarlarinDegerleri[0]=op.randomZarGetir();
        ortadakiZarlarinDegerleri[1]=op.randomZarGetir();
        ortadakiZarlarinDegerleri[2]=op.randomZarGetir();
        ortadakiZarlarinDegerleri[3]=op.randomZarGetir();
        ortadakiZarlarinDegerleri[4]=op.randomZarGetir();
        masazar1.setIcon(zar_resimleri[ortadakiZarlarinDegerleri[0]]);
        masazar2.setIcon(zar_resimleri[ortadakiZarlarinDegerleri[1]]);
        masazar3.setIcon(zar_resimleri[ortadakiZarlarinDegerleri[2]]);
        masazar4.setIcon(zar_resimleri[ortadakiZarlarinDegerleri[3]]);
        masazar5.setIcon(zar_resimleri[ortadakiZarlarinDegerleri[4]]);
        
        zarlarinDegerleri[0]=0;
        zarlarinDegerleri[1]=0;
        zarlarinDegerleri[2]=0;
        zarlarinDegerleri[3]=0;
        zarlarinDegerleri[4]=0;
        
        benzar1.setIcon(zar_resimleri[0]);
        benzar2.setIcon(zar_resimleri[0]);
        benzar3.setIcon(zar_resimleri[0]);
        benzar4.setIcon(zar_resimleri[0]);
        benzar5.setIcon(zar_resimleri[0]);
    }

    public void ilk_sira_zarlari_at(){
        //önce masadaki zarlara rasgele değerler alalım.
        //sonra bu değerlere ait olan zar resimlerini alalım.
        zarlarinDegerleri[0]=op.randomZarGetir();
        zarlarinDegerleri[1]=op.randomZarGetir();
        zarlarinDegerleri[2]=op.randomZarGetir();
        zarlarinDegerleri[3]=op.randomZarGetir();
        zarlarinDegerleri[4]=op.randomZarGetir();
        
        benzar1.setIcon(zar_resimleri[zarlarinDegerleri[0]]);
        benzar2.setIcon(zar_resimleri[zarlarinDegerleri[1]]);
        benzar3.setIcon(zar_resimleri[zarlarinDegerleri[2]]);
        benzar4.setIcon(zar_resimleri[zarlarinDegerleri[3]]);
        benzar5.setIcon(zar_resimleri[zarlarinDegerleri[4]]);
        
        for(int i=0;i<5;i++)
            ortadakiZarlarinDegerleri[i]=0;
        masazar1.setIcon(zar_resimleri[0]);
        masazar1.setIcon(zar_resimleri[0]);
        masazar1.setIcon(zar_resimleri[0]);
        masazar1.setIcon(zar_resimleri[0]);
        masazar1.setIcon(zar_resimleri[0]);
    }
    
    public void rakip_zar_resimlerini_sifirla(){
        rakipzar1.setIcon(zar_resimleri[0]);
        rakipzar2.setIcon(zar_resimleri[0]);
        rakipzar3.setIcon(zar_resimleri[0]);
        rakipzar4.setIcon(zar_resimleri[0]);
        rakipzar5.setIcon(zar_resimleri[0]);
    }
    
    public void Reset(){
        if (Client.socket!=null) {
            if (Client.socket.isConnected())
            {
                Client.Stop();
            }
        }
    //oyun bitiminde hangi şeyler enable false olacaksa onlar oluyor.
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        rakippuan2 = new javax.swing.JLabel();
        rakippuan1 = new javax.swing.JLabel();
        rakippuan16 = new javax.swing.JLabel();
        rakippuan15 = new javax.swing.JLabel();
        rakippuan14 = new javax.swing.JLabel();
        rakippuan13 = new javax.swing.JLabel();
        rakippuan12 = new javax.swing.JLabel();
        rakippuan11 = new javax.swing.JLabel();
        rakippuan10 = new javax.swing.JLabel();
        rakippuan9 = new javax.swing.JLabel();
        rakippuan8 = new javax.swing.JLabel();
        rakippuan7 = new javax.swing.JLabel();
        rakippuan6 = new javax.swing.JLabel();
        rakippuan5 = new javax.swing.JLabel();
        rakippuan4 = new javax.swing.JLabel();
        rakippuan3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        benpuan1 = new javax.swing.JLabel();
        benpuan2 = new javax.swing.JLabel();
        benpuan3 = new javax.swing.JLabel();
        benpuan4 = new javax.swing.JLabel();
        benpuan5 = new javax.swing.JLabel();
        benpuan6 = new javax.swing.JLabel();
        benpuan7 = new javax.swing.JLabel();
        benpuan8 = new javax.swing.JLabel();
        benpuan9 = new javax.swing.JLabel();
        benpuan10 = new javax.swing.JLabel();
        benpuan11 = new javax.swing.JLabel();
        benpuan12 = new javax.swing.JLabel();
        benpuan13 = new javax.swing.JLabel();
        benpuan14 = new javax.swing.JLabel();
        benpuan15 = new javax.swing.JLabel();
        benpuan16 = new javax.swing.JLabel();
        masaPanel = new javax.swing.JPanel();
        masazar1 = new javax.swing.JLabel();
        masazar2 = new javax.swing.JLabel();
        masazar3 = new javax.swing.JLabel();
        masazar4 = new javax.swing.JLabel();
        masazar5 = new javax.swing.JLabel();
        bizimPanel = new javax.swing.JPanel();
        benzar2 = new javax.swing.JLabel();
        benzar1 = new javax.swing.JLabel();
        benzar3 = new javax.swing.JLabel();
        benzar4 = new javax.swing.JLabel();
        benzar5 = new javax.swing.JLabel();
        rakipPanel = new javax.swing.JPanel();
        rakipzar1 = new javax.swing.JLabel();
        rakipzar2 = new javax.swing.JLabel();
        rakipzar3 = new javax.swing.JLabel();
        rakipzar4 = new javax.swing.JLabel();
        rakipzar5 = new javax.swing.JLabel();
        baglanbutton = new javax.swing.JButton();
        durum = new javax.swing.JLabel();
        zarla = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(920, 650));
        setMinimumSize(new java.awt.Dimension(920, 650));
        setPreferredSize(new java.awt.Dimension(920, 650));
        setSize(new java.awt.Dimension(920, 650));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setMaximumSize(new java.awt.Dimension(380, 573));
        jPanel1.setMinimumSize(new java.awt.Dimension(380, 573));
        jPanel1.setPreferredSize(new java.awt.Dimension(380, 575));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, -1, -1));

        rakippuan2.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan2.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan2.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan2.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan2, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 71, -1, -1));

        rakippuan1.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan1.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan1.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan1.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan1, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 40, -1, -1));

        rakippuan16.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan16.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan16.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan16.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan16.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan16, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 542, -1, -1));

        rakippuan15.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan15.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan15.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan15.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan15.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan15, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 510, -1, -1));

        rakippuan14.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan14.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan14.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan14.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan14.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan14, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 468, -1, -1));

        rakippuan13.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan13.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan13.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan13.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan13.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan13, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 436, -1, -1));

        rakippuan12.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan12.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan12.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan12.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan12.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan12, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 405, -1, -1));

        rakippuan11.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan11.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan11.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan11.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan11.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan11, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 372, -1, -1));

        rakippuan10.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan10.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan10.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan10.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan10.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan10, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 340, -1, -1));

        rakippuan9.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan9.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan9.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan9.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan9.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan9, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 308, -1, -1));

        rakippuan8.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan8.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan8.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan8.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan8, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 276, -1, -1));

        rakippuan7.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan7.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan7.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan7.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan7, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 242, -1, -1));

        rakippuan6.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan6.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan6.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan6.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan6, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 198, -1, -1));

        rakippuan5.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan5.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan5.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan5.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan5, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 166, -1, -1));

        rakippuan4.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan4.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan4.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan4.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan4, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 133, -1, -1));

        rakippuan3.setBackground(new java.awt.Color(153, 153, 153));
        rakippuan3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rakippuan3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakippuan3.setMaximumSize(new java.awt.Dimension(30, 31));
        rakippuan3.setMinimumSize(new java.awt.Dimension(30, 31));
        rakippuan3.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(rakippuan3, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 102, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Ben  Rakip");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, -1, 30));

        benpuan1.setBackground(new java.awt.Color(255, 255, 255));
        benpuan1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan1.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan1.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan1.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 40, -1, -1));

        benpuan2.setBackground(new java.awt.Color(255, 255, 255));
        benpuan2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan2.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan2.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan2.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan2, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 71, -1, -1));

        benpuan3.setBackground(new java.awt.Color(255, 255, 255));
        benpuan3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan3.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan3.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan3.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan3, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 102, -1, -1));

        benpuan4.setBackground(new java.awt.Color(255, 255, 255));
        benpuan4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan4.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan4.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan4.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 133, -1, -1));

        benpuan5.setBackground(new java.awt.Color(255, 255, 255));
        benpuan5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan5.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan5.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan5.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan5, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 166, -1, -1));

        benpuan6.setBackground(new java.awt.Color(255, 255, 255));
        benpuan6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan6.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan6.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan6.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan6, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 198, -1, -1));

        benpuan7.setBackground(new java.awt.Color(255, 255, 255));
        benpuan7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan7.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan7.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan7.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan7, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 242, -1, -1));

        benpuan8.setBackground(new java.awt.Color(255, 255, 255));
        benpuan8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan8.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan8.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan8.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan8, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 276, -1, -1));

        benpuan9.setBackground(new java.awt.Color(255, 255, 255));
        benpuan9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan9.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan9.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan9.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan9.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan9, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 308, -1, -1));

        benpuan10.setBackground(new java.awt.Color(255, 255, 255));
        benpuan10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan10.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan10.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan10.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan10.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan10, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 340, -1, -1));

        benpuan11.setBackground(new java.awt.Color(255, 255, 255));
        benpuan11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan11.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan11.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan11.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan11.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan11, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 372, -1, -1));

        benpuan12.setBackground(new java.awt.Color(255, 255, 255));
        benpuan12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan12.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan12.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan12.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan12.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan12, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 405, -1, -1));

        benpuan13.setBackground(new java.awt.Color(255, 255, 255));
        benpuan13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan13.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan13.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan13.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan13.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan13, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 436, -1, -1));

        benpuan14.setBackground(new java.awt.Color(255, 255, 255));
        benpuan14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan14.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan14.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan14.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan14.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan14, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 468, -1, -1));

        benpuan15.setBackground(new java.awt.Color(255, 255, 255));
        benpuan15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan15.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan15.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan15.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan15.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan15, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 510, -1, -1));

        benpuan16.setBackground(new java.awt.Color(255, 255, 255));
        benpuan16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        benpuan16.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        benpuan16.setMaximumSize(new java.awt.Dimension(30, 31));
        benpuan16.setMinimumSize(new java.awt.Dimension(30, 31));
        benpuan16.setPreferredSize(new java.awt.Dimension(30, 31));
        jPanel1.add(benpuan16, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 542, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        masaPanel.setBackground(new java.awt.Color(0, 204, 153));
        masaPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        masaPanel.setMaximumSize(new java.awt.Dimension(500, 200));
        masaPanel.setMinimumSize(new java.awt.Dimension(500, 200));
        masaPanel.setPreferredSize(new java.awt.Dimension(500, 200));
        masaPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        masazar1.setText(".");
        masazar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                masazar1MouseClicked(evt);
            }
        });
        masaPanel.add(masazar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        masazar2.setText(".");
        masazar2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                masazar2MouseClicked(evt);
            }
        });
        masaPanel.add(masazar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, -1, -1));

        masazar3.setText(".");
        masazar3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                masazar3MouseClicked(evt);
            }
        });
        masaPanel.add(masazar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, -1, -1));

        masazar4.setText(".");
        masazar4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                masazar4MouseClicked(evt);
            }
        });
        masaPanel.add(masazar4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 50, -1, -1));

        masazar5.setText(".");
        masazar5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                masazar5MouseClicked(evt);
            }
        });
        masaPanel.add(masazar5, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 50, -1, -1));

        getContentPane().add(masaPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 170, 500, 200));

        bizimPanel.setBackground(new java.awt.Color(255, 255, 255));
        bizimPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        bizimPanel.setEnabled(false);
        bizimPanel.setMaximumSize(new java.awt.Dimension(500, 125));
        bizimPanel.setMinimumSize(new java.awt.Dimension(500, 125));
        bizimPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        benzar2.setText(".");
        benzar2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                benzar2MouseClicked(evt);
            }
        });
        bizimPanel.add(benzar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, -1, -1));

        benzar1.setText(".");
        benzar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                benzar1MouseClicked(evt);
            }
        });
        bizimPanel.add(benzar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        benzar3.setText(".");
        benzar3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                benzar3MouseClicked(evt);
            }
        });
        bizimPanel.add(benzar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, -1, -1));

        benzar4.setText(".");
        benzar4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                benzar4MouseClicked(evt);
            }
        });
        bizimPanel.add(benzar4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, -1, -1));

        benzar5.setText(".");
        benzar5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                benzar5MouseClicked(evt);
            }
        });
        bizimPanel.add(benzar5, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 30, -1, -1));

        getContentPane().add(bizimPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 445, 500, 125));

        rakipPanel.setBackground(new java.awt.Color(255, 255, 255));
        rakipPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakipPanel.setEnabled(false);
        rakipPanel.setMaximumSize(new java.awt.Dimension(500, 115));
        rakipPanel.setMinimumSize(new java.awt.Dimension(500, 115));
        rakipPanel.setPreferredSize(new java.awt.Dimension(500, 115));
        rakipPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        rakipzar1.setText(".");
        rakipPanel.add(rakipzar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        rakipzar2.setText(".");
        rakipPanel.add(rakipzar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, -1, -1));

        rakipzar3.setText(".");
        rakipPanel.add(rakipzar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, -1, -1));

        rakipzar4.setText(".");
        rakipPanel.add(rakipzar4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, -1, -1));

        rakipzar5.setText(".");
        rakipPanel.add(rakipzar5, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 30, -1, -1));

        getContentPane().add(rakipPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 50, 500, 115));

        baglanbutton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        baglanbutton.setText("Oyun Ara");
        baglanbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baglanbuttonActionPerformed(evt);
            }
        });
        getContentPane().add(baglanbutton, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 10, 120, 30));

        durum.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        durum.setText("Durum :");
        getContentPane().add(durum, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, -1, -1));

        zarla.setText("jButton1");
        zarla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zarlaActionPerformed(evt);
            }
        });
        getContentPane().add(zarla, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 383, 150, 50));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void baglanbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baglanbuttonActionPerformed
        //bağlanılacak server ve portu veriyoruz
        Client.Start("127.0.0.1", 2000);
        //başlangıç durumları
        durum.setText("Durum: Rakip Bekleniyor...");
        baglanbutton.setEnabled(false);
        Message msg = new Message(Message.Message_Type.Startt);
        msg.content = "";
        Client.Send(msg);
    }//GEN-LAST:event_baglanbuttonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //form kapanırken clienti durdur
        Client.Stop();
    }//GEN-LAST:event_formWindowClosing

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        zarla.setEnabled(false);
        //masadaki zarların değerlerini atıyoruz.
        for(int i=0;i<5;i++){
            zarlarinDegerleri[i] = 0;
            ortadakiZarlarinDegerleri[i] = 0;
        }
        for(int i=0;i<16;i++){
            skor[i] = -1;
            rakipSkor[i] = -1;
        }
        skor[6]=0;
        skor[14]=0;
        skor[15]=0;
        rakipSkor[6]=0;
        rakipSkor[14]=0;
        rakipSkor[15]=0;
    }//GEN-LAST:event_formWindowOpened

    private void zarlaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zarlaActionPerformed
        if(islemSirasi==2){
            islemSirasi++;
            elindeki_zarlari_at();
        }else if(islemSirasi==3){
            islemSirasi++;
            zarlari_at();
        }else if(islemSirasi==4){
            islemSirasi++;
            zarlari_at();
            zarla.setEnabled(false);
        }
        //sırada puan seçimi var.
    }//GEN-LAST:event_zarlaActionPerformed

    private void benzar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_benzar1MouseClicked
        if(ortadakiZarlarinDegerleri[0]==0 && islemSirasi!=1){
            masazar1.setIcon(zar_resimleri[zarlarinDegerleri[0]]);
            ortadakiZarlarinDegerleri[0]=zarlarinDegerleri[0];
            zarlarinDegerleri[0]=0;
            benzar1.setIcon(zar_resimleri[0]);
        }
    }//GEN-LAST:event_benzar1MouseClicked

    private void benzar2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_benzar2MouseClicked
        if(ortadakiZarlarinDegerleri[1]==0 && islemSirasi!=1){
            masazar2.setIcon(zar_resimleri[zarlarinDegerleri[1]]);
            ortadakiZarlarinDegerleri[1]=zarlarinDegerleri[1];
            zarlarinDegerleri[1]=0;
            benzar2.setIcon(zar_resimleri[0]);
        }
    }//GEN-LAST:event_benzar2MouseClicked

    private void benzar3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_benzar3MouseClicked
        if(ortadakiZarlarinDegerleri[2]==0 && islemSirasi!=1){
            masazar3.setIcon(zar_resimleri[zarlarinDegerleri[2]]);
            ortadakiZarlarinDegerleri[2]=zarlarinDegerleri[2];
            zarlarinDegerleri[2]=0;
            benzar3.setIcon(zar_resimleri[0]);
        }
    }//GEN-LAST:event_benzar3MouseClicked

    private void benzar4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_benzar4MouseClicked
        if(ortadakiZarlarinDegerleri[3]==0 && islemSirasi!=1){
            masazar4.setIcon(zar_resimleri[zarlarinDegerleri[3]]);
            ortadakiZarlarinDegerleri[3]=zarlarinDegerleri[3];
            zarlarinDegerleri[3]=0;
            benzar4.setIcon(zar_resimleri[0]);
        }
    }//GEN-LAST:event_benzar4MouseClicked

    private void benzar5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_benzar5MouseClicked
        if(ortadakiZarlarinDegerleri[4]==0 && islemSirasi!=1){
            masazar5.setIcon(zar_resimleri[zarlarinDegerleri[4]]);
            ortadakiZarlarinDegerleri[4]=zarlarinDegerleri[4];
            zarlarinDegerleri[4]=0;
            benzar5.setIcon(zar_resimleri[0]);
        }
    }//GEN-LAST:event_benzar5MouseClicked

    private void masazar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_masazar1MouseClicked
        if(zarlarinDegerleri[0]==0 && islemSirasi!=1){
            benzar1.setIcon(zar_resimleri[ortadakiZarlarinDegerleri[0]]);
            zarlarinDegerleri[0]=ortadakiZarlarinDegerleri[0];
            ortadakiZarlarinDegerleri[0]=0;
            masazar1.setIcon(zar_resimleri[0]);
            kontrol_et();
        }
    }//GEN-LAST:event_masazar1MouseClicked

    private void masazar2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_masazar2MouseClicked
        if(zarlarinDegerleri[1]==0 && islemSirasi!=1){
            benzar2.setIcon(zar_resimleri[ortadakiZarlarinDegerleri[1]]);
            zarlarinDegerleri[1]=ortadakiZarlarinDegerleri[1];
            ortadakiZarlarinDegerleri[1]=0;
            masazar2.setIcon(zar_resimleri[0]);
            kontrol_et();
        }
    }//GEN-LAST:event_masazar2MouseClicked

    private void masazar3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_masazar3MouseClicked
        if(zarlarinDegerleri[2]==0 && islemSirasi!=1){
            benzar3.setIcon(zar_resimleri[ortadakiZarlarinDegerleri[2]]);
            zarlarinDegerleri[2]=ortadakiZarlarinDegerleri[2];
            ortadakiZarlarinDegerleri[2]=0;
            masazar3.setIcon(zar_resimleri[0]);
            kontrol_et();
        }
    }//GEN-LAST:event_masazar3MouseClicked

    private void masazar4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_masazar4MouseClicked
        if(zarlarinDegerleri[3]==0 && islemSirasi!=1){
            benzar4.setIcon(zar_resimleri[ortadakiZarlarinDegerleri[3]]);
            zarlarinDegerleri[3]=ortadakiZarlarinDegerleri[3];
            ortadakiZarlarinDegerleri[3]=0;
            masazar4.setIcon(zar_resimleri[0]);
            kontrol_et();
        }
    }//GEN-LAST:event_masazar4MouseClicked

    private void masazar5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_masazar5MouseClicked
        if(zarlarinDegerleri[4]==0 && islemSirasi!=1){
            benzar5.setIcon(zar_resimleri[ortadakiZarlarinDegerleri[4]]);
            zarlarinDegerleri[4]=ortadakiZarlarinDegerleri[4];
            ortadakiZarlarinDegerleri[4]=0;
            masazar5.setIcon(zar_resimleri[0]);
            kontrol_et();
        }
    }//GEN-LAST:event_masazar5MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Yahtzee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Yahtzee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Yahtzee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Yahtzee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Yahtzee().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton baglanbutton;
    private javax.swing.JLabel benpuan1;
    private javax.swing.JLabel benpuan10;
    private javax.swing.JLabel benpuan11;
    private javax.swing.JLabel benpuan12;
    private javax.swing.JLabel benpuan13;
    private javax.swing.JLabel benpuan14;
    private javax.swing.JLabel benpuan15;
    private javax.swing.JLabel benpuan16;
    private javax.swing.JLabel benpuan2;
    private javax.swing.JLabel benpuan3;
    private javax.swing.JLabel benpuan4;
    private javax.swing.JLabel benpuan5;
    private javax.swing.JLabel benpuan6;
    private javax.swing.JLabel benpuan7;
    private javax.swing.JLabel benpuan8;
    private javax.swing.JLabel benpuan9;
    private javax.swing.JLabel benzar1;
    private javax.swing.JLabel benzar2;
    private javax.swing.JLabel benzar3;
    private javax.swing.JLabel benzar4;
    private javax.swing.JLabel benzar5;
    private javax.swing.JPanel bizimPanel;
    public javax.swing.JLabel durum;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel masaPanel;
    private javax.swing.JLabel masazar1;
    private javax.swing.JLabel masazar2;
    private javax.swing.JLabel masazar3;
    private javax.swing.JLabel masazar4;
    private javax.swing.JLabel masazar5;
    private javax.swing.JPanel rakipPanel;
    private javax.swing.JLabel rakippuan1;
    private javax.swing.JLabel rakippuan10;
    private javax.swing.JLabel rakippuan11;
    private javax.swing.JLabel rakippuan12;
    private javax.swing.JLabel rakippuan13;
    private javax.swing.JLabel rakippuan14;
    private javax.swing.JLabel rakippuan15;
    private javax.swing.JLabel rakippuan16;
    private javax.swing.JLabel rakippuan2;
    private javax.swing.JLabel rakippuan3;
    private javax.swing.JLabel rakippuan4;
    private javax.swing.JLabel rakippuan5;
    private javax.swing.JLabel rakippuan6;
    private javax.swing.JLabel rakippuan7;
    private javax.swing.JLabel rakippuan8;
    private javax.swing.JLabel rakippuan9;
    private javax.swing.JLabel rakipzar1;
    private javax.swing.JLabel rakipzar2;
    private javax.swing.JLabel rakipzar3;
    private javax.swing.JLabel rakipzar4;
    private javax.swing.JLabel rakipzar5;
    private javax.swing.JButton zarla;
    // End of variables declaration//GEN-END:variables
}
