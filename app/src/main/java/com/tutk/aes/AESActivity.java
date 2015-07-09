package com.tutk.aes;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
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


public class AESActivity extends Activity {

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
                String project_name = mProjectText.getText().toString();
                ArrayList<HashMap<String,String>> encode_infos = getFromeEncodeFile();
                for(HashMap<String,String> info : encode_infos)
                {
                    String aes = info.get("aes");
                    String decode = AES.Decode(project_name , aes);
                    if(decode!=null) {
                        String[] split = decode.split(";");
                        info.put("mac_addr", split[0]);
                        info.put("uid", split[1]);
                     }
                }

                setToDecodeFile(encode_infos);
                Toast.makeText(view.getContext(),"DONE",Toast.LENGTH_LONG).show();
            }
            break;
            case R.id.bt_encode: {

                String project_name = mProjectText.getText().toString();
                ArrayList<HashMap<String,String>> decode_infos = getFromeDecodeFile();
                Log.d(TAG,"SIZE = " + decode_infos.size());
                for(HashMap<String,String> info : decode_infos)
                {
                    String value = info.get("mac_addr")+ ";" + info.get("uid");
                    String encode = AES.Encode(project_name , value);
                    info.put("aes",encode);
                }
                setToEncodeFile(decode_infos);
                Toast.makeText(view.getContext(),"DONE",Toast.LENGTH_LONG).show();
            }
            break;
        }
    }

    private void setToDecodeFile(ArrayList<HashMap<String,String>> infos)
    {
        try{
            FileOutputStream fos = new FileOutputStream(DECODE_FILE);
            for(HashMap<String,String> info : infos)
            {
                String uid = info.get("uid");
                String mac = info.get("mac_addr");
                String aes = info.get("aes");

                fos.write(("#"+aes+"\n").getBytes());
                fos.write((mac + ";" + uid + "\n\n").getBytes());
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<HashMap<String,String>> getFromeEncodeFile()
    {
        ArrayList<HashMap<String,String>> infos = new ArrayList<HashMap<String,String>>();
        try {
            FileInputStream fos = new FileInputStream(ENCODE_FILE);
            InputStreamReader isr = new InputStreamReader(fos);
            BufferedReader br = new BufferedReader(isr);
            String buf = new String();
            while((buf = br.readLine())!=null)
            {
                if(buf.startsWith("#")==false && buf.startsWith("key:")==true)
                {
                    HashMap<String,String> info = new HashMap<String,String>();
                    info.put("aes",buf.substring(4));
                    infos.add(info);
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

        return infos;
    }
    private void setToEncodeFile(ArrayList<HashMap<String,String>> infos)
    {
        try{
            FileOutputStream fos = new FileOutputStream(ENCODE_FILE);
            for(HashMap<String,String> info : infos)
            {
                String uid = info.get("uid");
                String mac = info.get("mac_addr");
                String aes = info.get("aes");

                fos.write(("#"+uid+"\n").getBytes());
                fos.write(("#" + mac + "\n").getBytes());
                fos.write(("key:" + aes + "\n\n").getBytes());
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<HashMap<String,String>> getFromeDecodeFile()
    {
        ArrayList<HashMap<String,String>> infos = new ArrayList<HashMap<String,String>>();
        try {
            FileInputStream fos = new FileInputStream(DECODE_FILE);
            InputStreamReader isr = new InputStreamReader(fos);
            BufferedReader br = new BufferedReader(isr);
            String buf = new String();
            while((buf = br.readLine())!=null)
            {
                if(buf.startsWith("#")==false && buf.length()>0)
                {
                    String[] split = buf.split(";");
                    HashMap<String,String> info = new HashMap();
                    info.put("mac_addr",split[0].toUpperCase());
                    info.put("uid",split[1].toUpperCase());
                    infos.add(info);
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

        return infos;
    }
}
