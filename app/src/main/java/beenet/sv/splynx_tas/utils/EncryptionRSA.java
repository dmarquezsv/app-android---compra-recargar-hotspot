package beenet.sv.splynx_tas.utils;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class EncryptionRSA {


    /**
     * Variables para utilizar en la incriptacion
     * */
    private byte[] encrytedByte;
    private Cipher cipher; //Repesenta un algoritmo de cifrado
    private final static String OPCION_RSA= "RSA/ECB/OAEPWithSHA1AndMGF1Padding";//Tipo de Padding y el metodo a utilizar RSA

    //LLAVE PUBLICA DEL BANCO
    public static String PUBLIC_KEY =
            "1sIIBIjANBgkqsiG9w0BAQEFAAOCAQ8sdaAMIIBCgKsdsadasdsadsadsaCAQsdadaq232ewaoR"+
                    "SzFpRAVRMS/GL/Yr8dsaLjLjBznj1AOXcSJwnUAFj7nINP4CjgMsqwRknyuPObNcM"+
                    "DFeV52LccqriwXwvAPtxF22asdSTvoJVrhkfu8WVsdTfI6ndq9NumG07vu/Lr9Kg3"+
                    "tg6Ki+eZkm0j7THXi4LClxKadsadasddsds3hnz81ma7TEH/8LYahBbrCxQkZFkU"+
                    "FCp75IzQp6tdbFcolN+kXzzrHPPX84U7Ia6j5SOX+qcPMD7SRD5Sd7WUZr/vjyI8"+
                    "o1diptDvJgaKS0ynVmvWhgH3dCasdasdw123dasdsadcxxas0JqcQKR045zSmSEHK3"+
                    "mwIDAQsBscsasdsaq22212s";

    /**
     * Funcion para incriptar json de la informacion de la tarjeta
     * */

    public String encrypt(String mensajeAEncriptar) throws Exception{

        byte[] publicBytes = Base64.decode(PUBLIC_KEY, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        cipher = Cipher.getInstance(OPCION_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);

        encrytedByte = cipher.doFinal(mensajeAEncriptar.getBytes());
        return Base64.encodeToString(encrytedByte, Base64.DEFAULT);
    }

}
