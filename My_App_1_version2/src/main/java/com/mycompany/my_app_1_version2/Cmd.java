
package com.mycompany.my_app_1_version2;

import java.util.List;
import javax.smartcardio.CardException;
import javax.smartcardio.*;

public class Cmd {
    
    public Cmd(){
        
    }
    
    public static void sendCommande(Card card,byte[] cmdCode)throws CardException,UnsupportedOperationException{
        CardChannel ch = card.getBasicChannel();
        CommandAPDU codCmd = new CommandAPDU(cmdCode);
        ResponseAPDU rp = ch.transmit(codCmd);
        if(cmdCode[1]== 0x20){
            if(rp.getSW1()== 0x69 && rp.getSW2() == 0x83){
                System.out.println("Le code est bloqué");
            }else if(rp.getSW() == 0x9000){
                System.out.println("L'identification IC est reussite! ");

            }else{
                System.out.println("Erreur d'indetification IC " + byteArrayToHexString(rp.getBytes()));
                int nbretent = rp.getSW() & 0x000F;
                System.out.println("Nombre de tentatives restantes = " + nbretent);
            }
            
        }else if(cmdCode[1]== 0x30){
            if(rp.getSW1()== 0x69 && rp.getSW2() == 0x82){
                System.out.println("Le IC n'a pas été vérifié ou la carte est dans l'étape utilisateur");
            }else if(rp.getSW() == 0x9000){
                System.out.println("La suppression est reussite! ");

            }else{
                System.out.println("Erreur inconnu(CLEAR CARD) ");
            }
        }else if(cmdCode[1] == (byte)0xA4){
            if(rp.getSW() == 0x9000 || rp.getSW() == 0x9100){
                System.out.println("La selection du fichier est reussite!");

            }else{
                System.out.println("Erreur de la selection du fichier");
            }
        }else if(cmdCode[1] == (byte)0xD2){
            if (rp.getSW() != 0x9000) {
                System.out.println("Erreur d'écriture dans le fichier " + byteArrayToHexString(rp.getBytes()));
            } else {
                System.out.println("L'écriture dans le fichier réussite ");
            }
        }
       
    }
    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }
}
