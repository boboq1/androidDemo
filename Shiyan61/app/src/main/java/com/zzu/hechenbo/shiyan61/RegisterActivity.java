package com.zzu.hechenbo.shiyan61;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class RegisterActivity extends AppCompatActivity {

    private EditText nick, name, pass; //昵称，用户名，密码
    private String username,password;//用户名和密码
    private Button register;//注册
    private String response = "";//注册信息
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                if (Boolean.parseBoolean(response) || response.equalsIgnoreCase("true")) {
                    /*注册成功*/
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    //将用户名传递到登录页面
                    intent.putExtra("username",name.getText().toString());
                    startActivity(intent);
                } else {
                    //注册失败
                    prompt();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        /*初始化视图组件*/
        nick = findViewById(R.id.nickname);
        name = findViewById(R.id.account);
        pass = findViewById(R.id.pass);
        register = findViewById(R.id.register);
        //注册事件
        register.setOnClickListener(view -> {
            username = name.getText().toString();
            password = pass.getText().toString();
            if (username.contains(" ") || password.contains(" ")){
                prompt();
            }else {
                new Thread(() -> {
                    //请求服务器并返回注册信息
                    response = GetUtil.sendGet(
                            "http://192.168.62.1:8888/UserServlet",
                            "action=Register" + "&username=" + username + "&password=" + password);
                    response = response.substring(4,8);
                    handler.sendEmptyMessage(0x123);
                }).start();
            }
        });
    }
//注册失败消息提示
    private void prompt() {
        Toast.makeText(this, "注册失败!", Toast.LENGTH_LONG).show();
    }
}
