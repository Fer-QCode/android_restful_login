package com.fujitsu.tmmin.central;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fujitsu.tmmin.central.api.CENTRAL010102WAPI;
import com.fujitsu.tmmin.central.id.common.domain.UserSessionInfo;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class CENTRAL010102W extends AppCompatActivity {


    //Untuk Koneksi
    EditText edtuserid, edtpass;
    Button btnLogin;
    ProgressBar prLogin;
    TextView getForgot;
    public String message = "";
    CENTRAL010102W self = null;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1==0){
                showError(message);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central010102w);
        self = this;

        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.custom_action, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(false);
        abar.setHomeButtonEnabled(true);
        //TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.toyota_red);

        //String define login
        edtuserid = (EditText) findViewById(R.id.editUser);
        edtpass = (EditText) findViewById(R.id.editPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        prLogin = (ProgressBar) findViewById(R.id.prLogin);
        prLogin.setVisibility(View.GONE);
        getForgot = (TextView) findViewById(R.id.textGetForgot);
        TextView textForgot = (TextView) findViewById(R.id.textForgot);
        textForgot.setPaintFlags(textForgot.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //getForgot.setAutoLinkMask(Linkify.WEB_URLS);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Jalankan proses login
                DoLogin doLogin = new DoLogin();
                doLogin.setParent(self);
                doLogin.execute("");
            }
        });
    }

    public void displayToast (View view) {
        //Toast.makeText(CENTRAL010102W.this, "Please contact 14045", Toast.LENGTH_LONG);
        //getForgot.setText("Please contact 14045");
        //UserSessionInfo info = CENTRAL010102WAPI.Instance().validateUser(userid,md5_pass,macAddress);
        sendMsg(CENTRAL010102WAPI.Instance().forgotPass("MCENT112224I").getContent());
    }

    public class DoLogin extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;

        String userid = edtuserid.getText().toString();
        String password = edtpass.getText().toString();
        CENTRAL010102W parent = null;

        public void setParent(CENTRAL010102W parent){
            this.parent=parent;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prLogin.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            prLogin.setVisibility(View.GONE);
            //Toast.makeText(CENTRAL010102W.this, s, Toast.LENGTH_LONG).show();

            //Jika value isSuccess = true
            if(isSuccess) {
                Intent i = new Intent(CENTRAL010102W.this, CENTRAL010101W.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                //MD5 Hashing
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(password.getBytes());

                byte byteData[] = md.digest();

                //convert the byte to hex format method 1
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < byteData.length; i++) {
                    sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                }
                String md5_pass = sb.toString();

                //Get Nilai Mac
                WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                WifiInfo wInfo = wifiManager.getConnectionInfo();
                String macAddress = wInfo.getMacAddress();


                UserSessionInfo info = CENTRAL010102WAPI.Instance().validateUser(userid,md5_pass,macAddress);
                if(info.isStatus()){
                    SharedPreferences sharedPreferences = getSharedPreferences("sesi", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(UserSessionInfo.USER_INFO, new Gson().toJson(info));
                    editor.commit();
                    isSuccess=true;
                }else {
                    //z=info.getContent();
                    //showError(info.getContent());
                    parent.sendMsg(info.getContent());
                }

            } catch (Exception ex) {
                isSuccess = false;
                z = "Exception";
            }
            return z;
        }
    }

    public void showError (String message){
        TextView partTextView = (TextView)findViewById(R.id.textGetForgot);
        partTextView.setText(message);
        //partTextView.setWidth = ;
        //partTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        //partTextView.setTextColor(Color.RED);

        LinearLayout sv = (LinearLayout)findViewById(R.id.messageError);
        try {
            sv.removeAllViews();
        } catch (Exception e){

        }
        sv.addView(partTextView);
    }

    public void sendMsg(String msg){
        Message ms = new Message();
        ms.arg1 = 0;
        message = msg;
        handler.sendMessage(ms);
    }
}
