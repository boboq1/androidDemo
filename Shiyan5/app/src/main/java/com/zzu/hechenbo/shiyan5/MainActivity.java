package com.zzu.hechenbo.shiyan5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText username, password;
    private ImageButton submit;
    private JSONObject msgJSON;
    private OutputStream out;
    private BufferedReader br;
    private String loginName;
    private String msg;
    public Handler handler;
    boolean hasLogin = false;//判断是否登录过
    public MainActivity() {

    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 0x123) {
                    try {
                        JSONObject loginJSON = new JSONObject((String) msg.obj);
                        hasLogin = loginJSON.getBoolean("hasLogin");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (hasLogin) {
                        prompt(hasLogin);
                        hasLogin = false;
                    } else {
                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", String.valueOf(username.getText()));
                        bundle.putString("password", String.valueOf(password.getText()));
                        intent.putExtra("data", bundle);
                        startActivity(intent);
                    }
                }
            }
        };

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientThread thread = new ClientThread(handler);
                new Thread(thread).start();

                String loginName = username.getText().toString();
                if (isEmpty(loginName)){
                    prompt(hasLogin);
                }else {
                    try {
                        JSONObject loginJSON = new JSONObject();
                        loginJSON.put("isLogin",true);
                        loginJSON.put("name",loginName);
                        Message loginMsg = new Message();
                        loginMsg.what = 0x145;
                        loginMsg.obj = loginJSON.toString()+"\n";
                        Thread.sleep(100);
                        thread.reshandler.sendMessage(loginMsg);
                    } catch (JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void prompt(boolean hasLogin) {
        if (hasLogin) {
            Toast.makeText(this, "您已经登录过了！不能重复登录", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "信息不能为空", Toast.LENGTH_LONG).show();
        }
    }
    private boolean isEmpty(String s) {
        if (s == null || s.equals("")) {
            return true;
        } else if (s.split(" ").length == 0) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
