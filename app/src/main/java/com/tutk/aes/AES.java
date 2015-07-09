package com.tutk.aes;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.security.MessageDigest;


public class AES extends Activity {

    private static final String TAG = "AES";
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
            }
            break;
            case R.id.bt_file: {

            }
        }
    }


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

}
