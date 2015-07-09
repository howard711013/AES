package com.tutk.aes;

import android.util.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by howard_chu on 2015/7/9.
 */
public class AES {

    public static String getMD5(String str)
    {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());


            byte[] b_md = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < b_md.length; i++)
            {
                String hex = Integer.toHexString(0xFF & b_md[i]);
                if (hex.length() == 1)
                {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            result = hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String Encode(String key, String value) {
        SecretKeySpec spec = new SecretKeySpec(getMD5(key).getBytes(), "AES");
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, spec);
            return Base64.encodeToString(cipher.doFinal(value.getBytes()), android.util.Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String Decode(String key, String value) {
        SecretKeySpec spec = new SecretKeySpec(getMD5(key).getBytes(), "AES");
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, spec);
            return new String( cipher.doFinal(Base64.decode(value, android.util.Base64.NO_WRAP)) );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
