package game;

import java.util.Random;
import javax.swing.ImageIcon;

public class Operations {
    
    
//    public int[][] getCordinatesForDicesInTable(){
//        int[][] dices = new int[5][2];
//        Random rnd = new Random();
//        
//        //dice 1
//        dices[0][0] = rnd.nextInt(10) + 10;//x = 10-20
//        dices[0][1] = rnd.nextInt(90) + 10;//y = 10-100
//        
//        //dice 2
//        dices[1][0] = rnd.nextInt(10) + 100;//x = 100-110
//        dices[1][1] = rnd.nextInt(90) + 10;//y = 10-100
//        
//        //dice 3
//        dices[2][0] = rnd.nextInt(10) + 190;//x = 190-200
//        dices[2][1] = rnd.nextInt(90) + 10;//y = 10-100
//        
//        //dice 4
//        dices[3][0] = rnd.nextInt(10) + 280;//x = 280-290
//        dices[3][1] = rnd.nextInt(90) + 10;//y = 10-100
//        
//        //dice 5
//        dices[4][0] = rnd.nextInt(10) + 360;//x = 360-370
//        dices[4][1] = rnd.nextInt(90) + 10;//y = 10-100
//        
//        return dices;
//    }
    
    public int randomZarGetir(){
        Random rnd = new Random();
        return rnd.nextInt(6) + 1;
    }
    
}
