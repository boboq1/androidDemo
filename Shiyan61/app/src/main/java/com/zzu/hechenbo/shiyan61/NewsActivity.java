package com.zzu.hechenbo.shiyan61;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class NewsActivity extends AppCompatActivity {

    private EditText searchKey;
    private Button search;
    private ListView listView;
    private String response;
    private String key = "";
    JSONArray object;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        listView = findViewById(R.id.listview);
        searchKey = findViewById(R.id.content_search);
        search = findViewById(R.id.search);

        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 0x123) {
                    class MyAdapter extends BaseAdapter {
                        LayoutInflater layoutInflater;
                        Context context;
                        String[] content;
                        String[] date;
                        String[] imags;
                        int len = 0;
                        String path = Environment.getExternalStorageDirectory()
                                + "/listviewImg/";// 文件目录
                        File fileDir;

                        public MyAdapter(Context context) {
                            this.context = context;
                            /**
                             * 文件目录如果不存在，则创建
                             */
                            fileDir = new File(path);
                            if (!fileDir.exists()) {
                                fileDir.mkdirs();
                            }
                            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            try {
                                object = new JSONArray(response);
                                len = object.length();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            content = new String[len];
                            date = new String[len];
                            imags = new String[len];
                            for (int i = 0; i < len; i++) {
                                try {
                                    JSONObject objectId = object.getJSONObject(i);
                                    content[i] = objectId.getString("title");
                                    date[i] = objectId.getString("date");
                                    imags[i] = objectId.getString("thumbnail_pic_s");
                                    File file = new File(fileDir, imags[i]);
                                    if (!file.exists()) {// 如果本地图片不存在则从服务器下载
                                        downloadPic(imags[i]);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public int getCount() {

                            return len;
                        }

                        @Override
                        public Object getItem(int position) {
                            return position;
                        }

                        @Override
                        public long getItemId(int position) {
                            return position;
                        }

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {

                            View view = layoutInflater.inflate(R.layout.info_item, null);
                            TextView title, datetime;
                            title = view.findViewById(R.id.content);
                            datetime = view.findViewById(R.id.date);
                            title.setText(content[position]);
                            datetime.setText(date[position]);
                            ImageView imageView = view.findViewById(R.id.pic);
                            File file = new File(fileDir, imags[position]);
                            Bitmap bitmap = BitmapFactory
                                    .decodeFile(file.getAbsolutePath());
                            imageView.setImageBitmap(bitmap);
                            return view;
                        }
                        private void downloadPic( final String img) {
                            new Thread(() -> {
                                FileOutputStream os = null;
                                InputStream is = null;
                                Log.w("fileDir",""+fileDir.getAbsolutePath());
                                File file = new File(path,img.substring(7));
                                Log.w("fileDir",""+file.getAbsolutePath());
                                try {
                                    URL url = new URL("http://192.168.62.1:8888/" + img);
                                    is = url.openStream();
                                    os = new FileOutputStream(file);
                                    int len = -1;
                                    byte[] b = new byte[1024];
                                    while ((len = is.read(b)) != -1) {
                                        os.write(b, 0, len);
                                    }
                                    os.flush();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (os != null) {
                                            os.close();
                                        }
                                        if (is != null){
                                            is.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                    ;
                    MyAdapter adapter = new MyAdapter(NewsActivity.this);
                    listView.setAdapter(adapter);
                }
            }
        };
        startThread(key, handler);
        search.setOnClickListener(view -> {
            key = searchKey.getText().toString();
            startThread(key, handler);
        });
    }

    private void startThread(String key, Handler handler) {
        new Thread(() -> {
            response = GetUtil.sendPost(
                    "http://192.168.62.1:8888/NewsServlet",
                    "key=" + key);
            handler.sendEmptyMessage(0x123);
        }).start();
    }
}
