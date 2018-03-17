package game;

import java.awt.Image;
import java.awt.Point;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
    ImageIcon zarlar_ben[];
    ImageIcon zarlar_rakip[];
    ImageIcon zarlar_orta[];
    ImageIcon zar_resimleri[];
    ImageIcon table;
    Random rand;
    private int[] skor = new int[19];
    private int[] rakip = new int[19];
    private int[] zarlar = new int[5];
    public String islem="";
    public int islemSirasi=1;
    private boolean sira=false;
    Operations op;
    
    /**
     * Creates new form Yahtzee
     */
    public Yahtzee() {
        initComponents();
        op = new Operations();
        ThisGame = this;
        try {
            zarlar_ben = new ImageIcon[5];//bizim zar resimlerimiz için
            zarlar_rakip = new ImageIcon[5];//rakibin zar resimleri için
            zarlar_orta = new ImageIcon[5];//rakibin zar resimleri için
            zar_resimleri = new ImageIcon[7];//rakibin zar resimleri için
            zar_resimleri[0] = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/none.png"))).getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT));
            zar_resimleri[1] = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/1.png"))).getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT));
            zar_resimleri[2] = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/2.png"))).getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT));
            zar_resimleri[3] = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/3.png"))).getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT));
            zar_resimleri[4] = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/4.png"))).getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT));
            zar_resimleri[5] = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/5.png"))).getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT));
            zar_resimleri[6] = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/6.png"))).getImage().getScaledInstance(75, 75, Image.SCALE_DEFAULT));
            
            zarlar_orta[0] = zar_resimleri[op.randomZarGetir()];
            zarlar_orta[1] = zar_resimleri[op.randomZarGetir()];
            zarlar_orta[2] = zar_resimleri[op.randomZarGetir()];
            zarlar_orta[3] = zar_resimleri[op.randomZarGetir()];
            zarlar_orta[4] = zar_resimleri[op.randomZarGetir()];
            masazar1.setIcon(zarlar_orta[0]);
            masazar2.setIcon(zarlar_orta[1]);
            masazar3.setIcon(zarlar_orta[2]);
            masazar4.setIcon(zarlar_orta[3]);
            masazar5.setIcon(zarlar_orta[4]);

            table = new ImageIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/images/table.png"))).getImage().getScaledInstance(380, 640, Image.SCALE_DEFAULT));
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
                            for(int i=0;i<5;i++){
                                zarlar_ben[i] = zarlar_orta[i];
                                zarlar_orta[i] = null;
                            }
                            bizim_zarlari_yerlestir();
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

    public void bizim_zarlari_yerlestir(){
        bizim_zar_resimlerini_sifirla();
        if(zarlar_ben[0]!=null)
            benzar1.setIcon(zarlar_ben[0]);
        if(zarlar_ben[1]!=null)
            benzar2.setIcon(zarlar_ben[1]);
        if(zarlar_ben[2]!=null)
            benzar3.setIcon(zarlar_ben[2]);
        if(zarlar_ben[3]!=null)
            benzar4.setIcon(zarlar_ben[3]);
        if(zarlar_ben[4]!=null)
            benzar5.setIcon(zarlar_ben[4]);
    }
    
    public void bizim_zar_resimlerini_sifirla(){
        benzar1.setIcon(zar_resimleri[0]);
        benzar2.setIcon(zar_resimleri[0]);
        benzar3.setIcon(zar_resimleri[0]);
        benzar4.setIcon(zar_resimleri[0]);
        benzar5.setIcon(zar_resimleri[0]);
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(920, 680));
        setMinimumSize(new java.awt.Dimension(920, 680));
        setPreferredSize(new java.awt.Dimension(920, 680));
        setSize(new java.awt.Dimension(920, 680));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setMaximumSize(new java.awt.Dimension(380, 640));
        jPanel1.setMinimumSize(new java.awt.Dimension(380, 640));
        jPanel1.setPreferredSize(new java.awt.Dimension(380, 640));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        masaPanel.setBackground(new java.awt.Color(0, 204, 153));
        masaPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        masaPanel.setMaximumSize(new java.awt.Dimension(500, 200));
        masaPanel.setMinimumSize(new java.awt.Dimension(500, 200));
        masaPanel.setPreferredSize(new java.awt.Dimension(500, 200));
        masaPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        masazar1.setText("masazar1");
        masazar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                masazar1MouseClicked(evt);
            }
        });
        masaPanel.add(masazar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        masazar2.setText("masazar2");
        masaPanel.add(masazar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, -1, -1));

        masazar3.setText("masazar3");
        masaPanel.add(masazar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, -1, -1));

        masazar4.setText("masazar4");
        masaPanel.add(masazar4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 50, -1, -1));

        masazar5.setText("masazar5");
        masaPanel.add(masazar5, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 50, -1, -1));

        getContentPane().add(masaPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 190, 500, 200));

        bizimPanel.setBackground(new java.awt.Color(255, 255, 255));
        bizimPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        bizimPanel.setEnabled(false);
        bizimPanel.setMaximumSize(new java.awt.Dimension(500, 125));
        bizimPanel.setMinimumSize(new java.awt.Dimension(500, 125));
        bizimPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        benzar2.setText("benzar2");
        bizimPanel.add(benzar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, -1, -1));
        benzar2.getAccessibleContext().setAccessibleName("benzar2");

        benzar1.setText("benzar1");
        benzar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                benzar1MouseClicked(evt);
            }
        });
        bizimPanel.add(benzar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));
        benzar1.getAccessibleContext().setAccessibleName("benzar1");

        benzar3.setText("benzar3");
        bizimPanel.add(benzar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, -1, -1));

        benzar4.setText("benzar4");
        bizimPanel.add(benzar4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, -1, -1));

        benzar5.setText("benzar5");
        bizimPanel.add(benzar5, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 30, -1, -1));

        getContentPane().add(bizimPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 500, 500, 125));

        rakipPanel.setBackground(new java.awt.Color(255, 255, 255));
        rakipPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rakipPanel.setEnabled(false);
        rakipPanel.setMaximumSize(new java.awt.Dimension(500, 125));
        rakipPanel.setMinimumSize(new java.awt.Dimension(500, 125));
        rakipPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        rakipzar1.setText("rakipzar1");
        rakipzar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rakipzar1MouseClicked(evt);
            }
        });
        rakipPanel.add(rakipzar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        rakipzar2.setText("rakipzar2");
        rakipPanel.add(rakipzar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, -1, -1));

        rakipzar3.setText("rakipzar3");
        rakipPanel.add(rakipzar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, -1, -1));

        rakipzar4.setText("rakipzar4");
        rakipPanel.add(rakipzar4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, -1, -1));

        rakipzar5.setText("rakipzar5");
        rakipPanel.add(rakipzar5, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 30, -1, -1));

        getContentPane().add(rakipPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 50, 500, 125));

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

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void benzar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_benzar1MouseClicked
        benzar1.setIcon(zar_resimleri[0]);
    }//GEN-LAST:event_benzar1MouseClicked

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

    private void rakipzar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rakipzar1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_rakipzar1MouseClicked

    private void masazar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_masazar1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_masazar1MouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //form kapanırken clienti durdur
        Client.Stop();
    }//GEN-LAST:event_formWindowClosing

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
    private javax.swing.JLabel benzar1;
    private javax.swing.JLabel benzar2;
    private javax.swing.JLabel benzar3;
    private javax.swing.JLabel benzar4;
    private javax.swing.JLabel benzar5;
    private javax.swing.JPanel bizimPanel;
    public javax.swing.JLabel durum;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel masaPanel;
    private javax.swing.JLabel masazar1;
    private javax.swing.JLabel masazar2;
    private javax.swing.JLabel masazar3;
    private javax.swing.JLabel masazar4;
    private javax.swing.JLabel masazar5;
    private javax.swing.JPanel rakipPanel;
    private javax.swing.JLabel rakipzar1;
    private javax.swing.JLabel rakipzar2;
    private javax.swing.JLabel rakipzar3;
    private javax.swing.JLabel rakipzar4;
    private javax.swing.JLabel rakipzar5;
    // End of variables declaration//GEN-END:variables
}
