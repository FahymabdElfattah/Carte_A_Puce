
package com.mycompany.my_app_1_version2;

import java.util.List;
import javax.smartcardio.CardException;
import javax.smartcardio.*;

public class My_App_1_version2 {
    
    private static byte[] SUBMIT_CODE = {(byte) 0x80, (byte)0x20, (byte)0x07, (byte)0x00, (byte)0x08, 0x41, 0x43, 0x4F, 0x53, 0x54, 0x45, 0x53, 0x54};
    private static byte[] SELECT_FILE = {(byte) 0x80, (byte)0xA4, (byte)0x00, (byte)0x00, (byte)0x02,(byte)0x00, (byte)0x00};
    
    public static void main(String[] args)throws CardException,UnsupportedOperationException {
        Cmd cmd = new Cmd();
        //=====================|Etape 1 :Connexion avec le Lecteur de la carte CAD|===================
        TerminalFactory tf = TerminalFactory.getDefault();
        CardTerminals lecteurs = tf.terminals();
        CardTerminal lecteur = lecteurs.getTerminal("Gemalto Prox-DU Contact_12400713 0");
        Card card = null;
        System.out.println("Attente de la carte ...");
        
        if(lecteur.isCardPresent()){
            card = lecteur.connect("*");
            System.out.println("Le terminal est connecté ");
            
            if(card != null){
                System.out.println("ATR de la carte: " + cmd.byteArrayToHexString(card.getATR().getBytes()));
                System.out.println("Protocole de la carte: " + card.getProtocol());
                
                
                //=====================|Etape 2 :Envoi de l'IC code|==================================
                cmd.sendCommande(card, SUBMIT_CODE);
                //=====================|Etape 3 :Selection du fichier 0xFF02|==================================
                System.out.println("Opération sur le fichier FF02");
                SELECT_FILE[5] = (byte)0xFF;
                SELECT_FILE[6] =  (byte)0x02;
                cmd.sendCommande(card, SELECT_FILE);
                //=====================|Etape 4 :Personnalisation d'un fichier 0xFF02|==================================
                byte[] write_record = {(byte)0x80,(byte)0xD2,0x00,0x00,0x04,0x00,0x00,0x01,0x00};
                cmd.sendCommande(card, write_record);
                //=====================|Etape 5 :Remis a zeros de la carte RESET|=======================================
                card.disconnect(true);
                card = lecteur.connect("*");
                //=====================|Etape 6 :Envoi de l'IC code|==================================
                cmd.sendCommande(card, SUBMIT_CODE);
                //=====================|Etape 7 :Selection du fichier 0xFF04|======================================
                System.out.println("Opération sur le fichier FF04");
                SELECT_FILE[6] =  (byte)0x04;
                cmd.sendCommande(card, SELECT_FILE);
                //=====================|Etape 8 :Association d'un ID a le fichier crée|===========================
                write_record = new byte[11];
                write_record[0] = (byte)0x80;
                write_record[1] = (byte)0xD2;
                write_record[2] =       0x00;
                write_record[3] =       0x00;
                write_record[4] =       0x06;
                write_record[5] =       0x14;
                write_record[6] =       0x04;
                write_record[7] =       0x00;
                write_record[8] =       0x00;
                write_record[9] = (byte)0xAA; //ID du fichier 0xAA10
                write_record[10]= (byte)0x10;
                cmd.sendCommande(card, write_record);
                //=====================|Etape 9 :Selection du fichier d'ID 0xAA10|===========================
                System.out.println("Opération sur le fichier AA10");
                SELECT_FILE[5] = (byte)0xAA;
                SELECT_FILE[6] =  (byte)0x10;
                cmd.sendCommande(card, SELECT_FILE);
                //=====================|Etape 10 :Personnalisation du fichier d'ID 0xAA10|===========================
                String[] file_AA10 = new String[4];
                file_AA10[0] = "Mr";
                file_AA10[1] = "FAHYM Abd Elfattah";
                file_AA10[2] = "0000111122223333";
                file_AA10[3] = "24012023";
                int j;
                for (j = 0; j <4; j++) {
                    byte[] nom_b = file_AA10[j].getBytes();
                    write_record = new byte[25];
                    write_record[0] = (byte)0x80;
                    write_record[1] = (byte)0xD2;
                    write_record[2] = (byte)j;
                    write_record[3] = 0x00;
                    write_record[4] = 0x14;
                    for(int i = 0; i < nom_b.length; i++) {
                        write_record[5+i]=nom_b[i];
                    }
                    cmd.sendCommande(card, write_record);
                //=====================|Etape 11 :Test de lecture des données inserée dans le fichier AA10|===========================
                for ( j = 0; j < 10; j++) {
                    byte[] read_record = {(byte)0x80,(byte)0xB2,(byte)j,0x00,0x14};
                    cmd.sendCommande(card, read_record);
                    CardChannel ch = card.getBasicChannel();
                    ResponseAPDU rp = ch.transmit(new CommandAPDU(read_record));
                    if (rp.getSW() == 0x9000) {
                        System.out.print("OK LECTURE DE AA10,Enreg"+j+" : ");
                        System.out.println(new  String(rp.getData()));
                    }
                }
                card.disconnect(true);
                }
            }
        }
        
    }
}
