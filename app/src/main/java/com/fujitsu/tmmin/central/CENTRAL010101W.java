package com.fujitsu.tmmin.central;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fujitsu.tmmin.central.api.CENTRAL010102WAPI;
import com.fujitsu.tmmin.central.api.CENTRAL010101WAPI;
import com.fujitsu.tmmin.central.domain.ConfirmedResultMap;
import com.fujitsu.tmmin.central.domain.SparePart;
import com.fujitsu.tmmin.central.id.common.domain.UserSessionInfo;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CENTRAL010101W extends AppCompatActivity {

    public static final String DEFAULT = "N/A";
    CENTRAL010102WAPI koneksiClass;

    private List<String> parts = null;
    private String bodyId = null;
    private int amount = 0;
    private int index = 0;
    private TextListener worker = null;
    private String termCd = "CCC";
    private String roleID = "0";
    private String userName = "YUSUF";

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1==0){
                com.fujitsu.tmmin.central.id.common.domain.Message res = CENTRAL010101WAPI.Instance().isValidBody(getBodyNo().getText().toString(), termCd, roleID);
                if(res.isStatus()){
                    setAmount(res.getAmountPart());
                    if(res.getContent_cd().equals("I")){
                        getBodyNo().setEnabled(false);
                        getBodyNo().setBackgroundColor(Color.GRAY);
                        setAmountPart();
                        setPartFocus();
                        bodyId = getBodyNo().getText().toString();
                        showMessage(res.getContent());
                        cleanMessage();
                    }else if(res.getContent_cd().equals("W")){
                        worker.enableWork = false;
                        showMessage(res.getContent());
                        getConfirmButton().setVisibility(View.GONE);
                        getYesOroButtonArea().setVisibility(View.VISIBLE);
                    }
                }else {
                    getBodyNo().setText("");
                    cleanMessage();
                    showMessage(res.getContent());
                }
            }else if(msg.arg1==1){
                String partID = getPartID().getText().toString();
                if(partID.length()>0){
                    boolean insertAble = true;
                    for(String existingPartID : parts){
                        if(partID.equals(existingPartID)){
                            insertAble = false;
                            break;
                        }
                    }
                    if(parts.size()==0 || insertAble){
                        index = index + 1;
                        getIndex().setText(new Integer(index).toString());
                        addPart(partID);
                        if(index==getAmount()){
                            getPartID().setEnabled(false);
                            getPartID().setBackgroundColor(Color.GRAY);
                            getConfirmButton().setEnabled(true);
                            getConfirmButton().setTextColor(Color.BLACK);
                            worker.enableWork = false;
                        }
                    }else {
                        getPartID().setText("");
                        showMessage("Same Part ID scan more than one values");
                    }
                }
            }else if(msg.arg1==2) {
                getPartID().setEnabled(false);
                getPartID().setBackgroundColor(Color.GRAY);
                getPartID().setText("");
                getConfirmButton().setEnabled(true);
            }
        }
    };

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_central010101w, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem actionViewItem = menu.findItem(R.id.miActionButton);
        // Retrieve the action-view from menu
        View v = MenuItemCompat.getActionView(actionViewItem);
        // Find the button within action-view
        //Button b = (Button) v.findViewById(R.id.btnCustomAction);
        TextView lblAction = (TextView) v.findViewById(R.id.lblAction);
        lblAction.setText("'" + userName + "'(Shift)");

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent i = new Intent(CENTRAL010101W.this, CENTRAL010102W.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        } else {
            this.reset();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central010101w);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //bt.setOnClickListener(CENTRAL010101W);
        koneksiClass = new CENTRAL010102WAPI();
        //btnLogout = (Button) findViewById(R.id.btnLogout);

        SharedPreferences sharedPreferences = getSharedPreferences("sesi", Context.MODE_PRIVATE);
        UserSessionInfo info = new Gson().fromJson(sharedPreferences.getString(UserSessionInfo.USER_INFO, DEFAULT), UserSessionInfo.class);
        userName = info.getUserName();
        roleID = info.getRoleID();
        termCd = info.getTerminalCode();
        parts = new ArrayList<String>();

        getBodyNo().setBackgroundColor(Color.WHITE);
        getPartID().setBackgroundColor(Color.WHITE);
        getConfirmButton().setEnabled(false);
        getConfirmButton().setTextColor(Color.WHITE);

        worker = new TextListener(this);
        worker.start();

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void addPart(String partID){
        try {
            this.parts.add(partID);
            getPartID().setText("");

            TextView partTextView = new TextView(this);
            partTextView.setText(partID);

            LinearLayout group = new LinearLayout(this);
            group.setOrientation(LinearLayout.HORIZONTAL);
            group.addView(partTextView);

            LinearLayout sv = (LinearLayout)findViewById(R.id.partListContent);
            sv.addView(group);
        }catch (Exception e){
            System.out.println("Error : "+e.getMessage());
        }
    }

    public void showMessage(String message){
        try {
            TextView partTextView = new TextView(this);
            partTextView.setText(message);

            LinearLayout group = new LinearLayout(this);
            group.setOrientation(LinearLayout.HORIZONTAL);
            group.addView(partTextView);

            LinearLayout sv = (LinearLayout)findViewById(R.id.messageContent);
            sv.removeAllViews();
            sv.addView(group);
        }catch (Exception e){
            System.out.println("Error : "+e.getMessage());
        }
    }

    public void cleanMessage(){
        try {
            getMessageContent().removeAllViews();
        }catch (Exception x){}
    }

    public LinearLayout getMessageContent(){
        return (LinearLayout)findViewById(R.id.messageContent);
    }

    public void cleanPart(){
        try {
            getPart().removeAllViews();
        }catch (Exception x){}
    }

    public LinearLayout getPart(){
        return (LinearLayout)findViewById(R.id.partListContent);
    }

    public LinearLayout getYesOroButtonArea(){
        return (LinearLayout)findViewById(R.id.grpBtnYesOrNo);
    }

    public void confirm(View view){
        try {
            LinearLayout sv = (LinearLayout)findViewById(R.id.partListContent);
            sv.removeAllViews();

            ConfirmedResultMap confirmedResultMap = CENTRAL010101WAPI.Instance().confimProcess(getBodyNo().getText().toString(),termCd,getAmount(),roleID,userName,parts);
            for(SparePart body : confirmedResultMap.getResult()){
                TextView partTextView = new TextView(this);
                TextView messageTextView = new TextView(this);

                partTextView.setText(body.getPartID());
                partTextView.setPadding(0,0,10,10);
                messageTextView.setText(body.getStatusName());

                if(body.getStatusCD().equals("O")){
                    partTextView.setBackgroundColor(Color.GREEN);
                    messageTextView.setBackgroundColor(Color.GREEN);
                }else {
                    partTextView.setBackgroundColor(Color.RED);
                    messageTextView.setBackgroundColor(Color.RED);
                }

                TextView spaceTextView = new TextView(this);

                LinearLayout group = new LinearLayout(this);
                group.setOrientation(LinearLayout.HORIZONTAL);
                group.addView(partTextView, 0);
                group.addView(spaceTextView, 1);
                group.addView(messageTextView, 2);
                sv.addView(group);
            }
            showMessage(confirmedResultMap.getContent());
            getConfirmButton().setEnabled(false);
            getConfirmButton().setTextColor(Color.WHITE);
        }catch (Exception ex){
            System.out.println("Error : "+ex.getMessage());
        }
    }

    public void doYes(View view){
        getBodyNo().setEnabled(false);
        getBodyNo().setBackgroundColor(Color.GRAY);
        setAmountPart();
        setPartFocus();
        bodyId = getBodyNo().getText().toString();
        showMessage("");
        cleanMessage();
        getConfirmButton().setVisibility(View.VISIBLE);
        getYesOroButtonArea().setVisibility(View.GONE);
        worker.enableWork = true;
    }

    public void doNo(View view){
        setAmount(0);
        getBodyNo().setText("");
        setBodyFocus();
        showMessage("");
        getConfirmButton().setVisibility(View.VISIBLE);
        getYesOroButtonArea().setVisibility(View.GONE);
        worker.enableWork = true;
    }

    public Button getConfirmButton(){
        return (Button) findViewById(R.id.btnConfirm);
    }

    public void disableButton(){
        getConfirmButton().setEnabled(false);
        getConfirmButton().setTextColor(Color.WHITE);
    }

    public EditText getBodyNo(){
        return (EditText) findViewById(R.id.txtBodyNo);
    }

    public EditText getPartID(){
        return (EditText) findViewById(R.id.txtPartID);
    }

    public TextView getIndex(){
        return (TextView) findViewById(R.id.txtIndex);
    }

    public TextView getAmountPart(){
        return (TextView) findViewById(R.id.txtAmountPart);
    }

    public void setPartFocus(){
        this.getPartID().requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(this.getPartID(), InputMethodManager.SHOW_IMPLICIT);
    }

    public void setBodyFocus(){
        this.getBodyNo().requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(this.getBodyNo(), InputMethodManager.SHOW_IMPLICIT);
    }

    public void setBodyColor(int color){
        getBodyNo().setBackgroundColor(color);
    }

    public void setAmountPart(){
        getAmountPart().setText(new Integer(amount).toString());
    }

    public class TextListener extends Thread {
        public boolean enableWork = true;
        public boolean done = false;
        private CENTRAL010101W main;

        public TextListener(CENTRAL010101W main){
            this.main = main;
        }

        public void run() {
            while (done==false) {
                try {
                    if(enableWork){
                        if(main.bodyId == null){
                            String bodyNo = main.getBodyNo().getText().toString();
                            System.out.println("isi body = "+new Boolean(bodyNo.length()>0).toString());
                            if(bodyNo!=null && bodyNo.length()>0){
                                enableWork = false;
                                Message msg = new Message();
                                msg.arg1 = 0;
                                handler.sendMessage(msg);
                                enableWork = true;
                            }
                        }else{
                            String partID = main.getPartID().getText().toString();
                            if(partID!=null && partID.length()>0){
                                enableWork = false;
                                if(main.index!=this.main.amount){
                                    Message msg = new Message();
                                    msg.arg1 = 1;
                                    handler.sendMessage(msg);
                                }else{
                                    Message msg = new Message();
                                    msg.arg1 = 2;
                                    handler.sendMessage(msg);
                                    done=true;
                                }
                                enableWork = true;
                            }
                        }
                    }
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void reset(){
        //clear and focusing body
        getBodyNo().setText("");
        getBodyNo().setEnabled(true);
        getBodyNo().setBackgroundColor(Color.WHITE);
        setBodyFocus();

        //clear part id
        getPartID().setText("");
        getPartID().setEnabled(true);
        getPartID().setBackgroundColor(Color.WHITE);

        //clean part and message
        cleanMessage();
        cleanPart();

        //clear amount of part id
        setAmount(0);
        setAmountPart();
        index = 0;
        getIndex().setText(new Integer(index).toString());
        parts.clear();

        bodyId = null;

        //recreate text listener
        worker.enableWork = true;

        disableButton();
    }
}
