package com.zzu.hechenbo.shiyan61;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class RegisterActivity extends AppCompatActivity {

    private EditText nick, name, pass;
    private String nickname, username, password;
    private Button register;
    private String response = "";
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                Log.e("response",response);
                if (Boolean.parseBoolean(response) || response.equalsIgnoreCase("true")) {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    prompt();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nick = findViewById(R.id.nickname);
        name = findViewById(R.id.account);
        pass = findViewById(R.id.pass);
        register = findViewById(R.id.register);
        register.setOnClickListener(view -> {
            new Thread(() -> {
                nickname = nick.getText().toString();
                username = name.getText().toString();
                password = pass.getText().toString();
                response = GetUtil.sendGet(
                        "http://172.20.10.3:8888/UserServlet",
                        "action=Register" + "&nickname=" + nickname + "&username=" + username + "&password=" + password);
                response = response.substring(4,8);
                handler.sendEmptyMessage(0x123);
            }).start();

        });
    }

    private void prompt() {
        Toast.makeText(this, "注册失败！", Toast.LENGTH_LONG).show();
    }
}
