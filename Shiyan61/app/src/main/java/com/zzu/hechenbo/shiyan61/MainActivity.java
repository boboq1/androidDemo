package com.zzu.hechenbo.shiyan61;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.security.spec.MGF1ParameterSpec.SHA256;

public class MainActivity extends AppCompatActivity {
    private EditText username, password;
    private String user, pass;
    private Button register,login;
    private String response;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 0x123){
                Log.e("response3",response+"");
                if (Boolean.parseBoolean(response)){
                    Intent intent = new Intent(MainActivity.this,NewsActivity.class);
                    startActivity(intent);
                }else {
                    prompt();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        register = findViewById(R.id.regist);
        login = findViewById(R.id.login);
        Intent newIntent = getIntent();
        String  registName =  newIntent.getStringExtra("username");
        if (registName != null || registName == ""){
            username.setText(registName);
        }
        register.setOnClickListener(view->{
            Intent intent = new Intent(this,RegisterActivity.class);
            startActivity(intent);
        });
        login.setOnClickListener(view -> {
            user = username.getText().toString();
            pass = com.zzu.hechenbo.shiyan61.SHA256.Encrypt(password.getText().toString(),"");
            new Thread(() -> {
                response = GetUtil.sendGet(
                        "http://172.20.10.3:8888/UserServlet",
                        "action=Login"+"&username=" + user + "&password=" + pass);
                response = response.substring(4,8);
                Log.e("response",response+"");
                handler.sendEmptyMessage(0x123);
            }).start();
        });
    }
    private void prompt(){
        Toast.makeText(this,"你输入的用户名或密码不正确,请重新输入或注册",Toast.LENGTH_LONG).show();
    }
}
