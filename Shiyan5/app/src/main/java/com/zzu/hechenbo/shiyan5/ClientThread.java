package com.zzu.hechenbo.shiyan5;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientThread implements Runnable {
    private OutputStream out;
    private Handler handler;
    public  Handler reshandler;
    BufferedReader br;

    ClientThread(Handler handler) {
        this.handler = handler;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void run() {
        try {

            Socket socket = new Socket("192.168.62.1", 5252);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = socket.getOutputStream();
            new Thread(() -> {
                String content;
                try {
                    while ((content = br.readLine()) != null) {
                        Message message = new Message();
                        message.what = 0x123;
                        message.obj = content;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            Looper.prepare();
            reshandler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if (msg.what == 0x145) {
                        try {
                            Log.w("msg"+Math.random(),msg.obj.toString());
                            out.write((msg.obj.toString()+"\n").getBytes("utf-8"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Looper.loop();
        } catch (SocketTimeoutException e) {
            System.out.println("网络连接超时");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
