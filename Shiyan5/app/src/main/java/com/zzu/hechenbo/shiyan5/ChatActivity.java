package com.zzu.hechenbo.shiyan5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class ChatActivity extends AppCompatActivity {
    private EditText chatName = null;//聊天对象
    private EditText input;//输入聊天内容
    private Button send;//发送信息
    private TextView show;//显示聊天内容
    private String name;//登录用户名
    private JSONObject nameJSON = null;
    private TextView nickname;

    public ChatActivity() {
    }

    static class MyHandler extends Handler {
        private String showName = null;
        private String showMsg = null;
        private WeakReference<ChatActivity> chatActivity;

        MyHandler(WeakReference<ChatActivity> chatActivity) {
            this.chatActivity = chatActivity;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 0x123) {
                try {
                    JSONObject msgJSON = new JSONObject((String) msg.obj);//接受返回的消息
                    showName = msgJSON.getString("name");
                    showMsg = msgJSON.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                chatActivity.get().show.append("\n" + showName + ":" + showMsg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        nickname = findViewById(R.id.nickname);
        chatName = findViewById(R.id.chatname);
        input = findViewById(R.id.sendmsg);
        send = findViewById(R.id.send);
        show = findViewById(R.id.showmsg);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        name = bundle.getString("name");

        MyHandler handler = new MyHandler(new WeakReference<>(this));
        ClientThread thread = new ClientThread(handler);
        new Thread(thread).start();
        /*登录初始化操作*/
        try {
            nameJSON = new JSONObject();
            String chatMsg = chatName.getText().toString();
            nameJSON.put("name", name);
            nameJSON.put("isLogin", false);
            if (!isEmpty(chatMsg)) {
                nameJSON.put("chatName", chatMsg);
            }
            Message msg = new Message();
            msg.what = 0x145;
            msg.obj = nameJSON.toString();
            Thread.sleep(100);
            thread.reshandler.sendMessage(msg);
            nickname.setText("昵称：" + name);
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
        /*发送信息操作*/
        send.setOnClickListener(view -> {
            String chatMsg = chatName.getText().toString();
            try {
                nameJSON = new JSONObject();//将用户名和消息放入
                nameJSON.put("name", name);
                nameJSON.put("isLogin", false);
                if (!isEmpty(chatMsg)) {
                    nameJSON.put("chatName", chatMsg);
                }
                nameJSON.put("message", input.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Message msg = new Message();
            msg.what = 0x145;
            if (isEmpty(input.getText().toString())) {
                prompt();
            } else {
                msg.obj = nameJSON.toString();//将存有用户名和消息的JSONObject对象放入msg.obj
                Log.e("nameJSON", nameJSON.toString());
                thread.reshandler.sendMessage(msg);//发送信息
                input.setText("");
            }
        });
    }

    private void prompt() {
        Toast.makeText(this, "信息不能为空", Toast.LENGTH_LONG).show();
    }

    private boolean isEmpty(String s) {
        if (s == null || s.equals("")) {
            return true;
        } else if (s.split(" ").length == 0) {
            return true;
        }
        return false;
    }
}
