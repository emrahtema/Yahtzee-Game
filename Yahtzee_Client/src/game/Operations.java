package game;

import java.util.Random;

public class Operations {
    
    //bu method elimizdeki zarların puanlarını hesaplayıp bir dizi olarak döndürüyor.
    //dizideki her bir eleman, oyun kısmında her bir puan türüyle sıralı olarak
    //eşleştiriliyor.
    public int[] puanlariGetir(int[] zarlar){
        int[] puanlar = new int[13];
        int count1=0,count2=0,count3=0,count4=0,count5=0,count6=0;
        for(int i:zarlar){
            if(i == 1)
                count1++;
            if(i == 2)
                count2++;
            if(i == 3)
                count3++;
            if(i == 4)
                count4++;
            if(i == 5)
                count5++;
            if(i == 6)
                count6++;
        }
        
        //birler
        puanlar[0] = count1;
        //ikiler
        puanlar[1] = count2*2;
        //üçler
        puanlar[2] = count3*3;
        //dörtler
        puanlar[3] = count4*4;
        //beşler
        puanlar[4] = count5*5;
        //altılar
        puanlar[5] = count6*6;
        
        //3 of a kind
        if(count1>=3 || count2>=3 || count3>=3 || count4>=3 || count5>=3 || count6>=3)
            puanlar[6] = count1 + count2*2 + count3*3 + count4*4 + count5*5 + count6*6;
        else
            puanlar[6] = 0;
        
        //4 of a kind
        if(count1>=4 || count2>=4 || count3>=4 || count4>=4 || count5>=4 || count6>=4)
            puanlar[7] = count1 + count2*2 + count3*3 + count4*4 + count5*5 + count6*6;
        else
            puanlar[7] = 0;
        
        //küçük seri 4lü
        if(count1>=1 && count2>=1 && count3>=1 && count4>=1)
            puanlar[8] = 30;
        else if(count2>=1 && count3>=1 && count4>=1 && count5>=1)
            puanlar[8] = 30;
        else if(count3>=1 && count4>=1 && count5>=1 && count6>=1)
            puanlar[8] = 30;
        else
            puanlar[8] = 0;
        
        //büyük seri 5li
        if(count1>=1 && count2>=1 && count3>=1 && count4>=1 && count5>=1)
            puanlar[9] = 40;
        else if(count2>=1 && count3>=1 && count4>=1 && count5>=1 && count6>=1)
            puanlar[9] = 40;
        else
            puanlar[9] = 0;
        
        //full house
        boolean ucler = false, ikiler = false;
        if(count1==3 || count2==3 || count3==3 || count4==3 || count5==3 ||count6==3)
            if(count1==2 || count2==2 || count3==2 || count4==2 || count5==2 ||count6==2)
                puanlar[10] = 25;
            else
                puanlar[10] = 0;
        else
            puanlar[10] = 0;
        
        //şans
        puanlar[11] = count1 + count2*2 + count3*3 + count4*4 + count5*5 + count6*6;
        
        //Yahtzee
        if(count1==5 || count2==5 || count3==5 || count4==5 || count5==5 ||count6==5)
            puanlar[12] = 50;
        else
            puanlar[12] = 0;
        
        return puanlar;
    }
    
    
    //masaya zar attığımızda masadaki zarların hep aynı konumda olmaması için
    //her atışta onlara random konum bilgisi göndereceğiz.
    public int addToX(){
        Random rnd = new Random();
        return rnd.nextInt(15) + 10;
    }
    public int addToY(){
        Random rnd = new Random();
        return rnd.nextInt(80) + 10;
    }
    
    //random metodu, rasgele zar sayısı üretecek.
    public int randomZarGetir(){
        Random rnd = new Random();
        return rnd.nextInt(6) + 1;
    }
    
}
