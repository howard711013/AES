package com.tutk.aes;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class AES extends Activity {

    private static final String TAG = "AES";
    private static final String DECODE_FILE = "/sdcard/aes_decode";
    private static final String ENCODE_FILE = "/sdcard/aes_encode";
    private EditText mProjectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aes);
        mProjectText = (EditText)findViewById(R.id.et_project);
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.bt_decode: {

            }
            break;
            case R.id.bt_encode: {

                String project_name = mProjectText.getText().toString();
                Log.d(TAG,"Project name : " +project_name );
                String md5 = getMD5(mProjectText.getText().toString());
                Log.d(TAG, "MD5 = " + md5 + "(" + md5.length() + ")");
                ArrayList<HashMap<String,String>> decode_values = getDecodeInFile();
                Log.d(TAG,"SIZE = " + decode_values.size());
                for(HashMap<String,String> value : decode_values)
                {
                   String aesValue = AesEncode(md5 , value.get("mac_addr")+ ";" + value.get("uid"));
                   Log.d(TAG,"UID = " + value.get("uid"));
                   Log.d(TAG,"AES = " + aesValue);
                }
            }
            break;
        }
    }

    private ArrayList<HashMap<String,String>> getDecodeInFile()
    {
        ArrayList<HashMap<String,String>> values = new ArrayList<HashMap<String,String>>();
        try {
            FileInputStream fos = new FileInputStream(DECODE_FILE);
            InputStreamReader isr = new InputStreamReader(fos);
            BufferedReader br = new BufferedReader(isr);
            String buf = new String();
            while((buf = br.readLine())!=null)
            {
                if(buf.startsWith("#")==false)
                {
                    String[] split = buf.split(";");
                    HashMap<String,String> value = new HashMap();
                    value.put("mac_addr",split[0]);
                    value.put("uid",split[1]);
                    values.add(value);
                }
            }

            br.close();
            isr.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return values;
    }

    //md5
    private String getMD5(String str)
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

    // aec decode / encode
    private String AesEncode(String key, String value) {
        SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "AES");
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


    private String AesDecode(String key, String value) {
        SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "AES");
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
