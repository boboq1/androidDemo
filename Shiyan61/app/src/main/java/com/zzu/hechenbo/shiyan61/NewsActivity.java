package com.zzu.hechenbo.shiyan61;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class NewsActivity extends AppCompatActivity {

    private EditText searchKey; //搜索关键字编辑视图
    private Button search;//定义搜索按钮
    private ListView listView;//显示新闻的列表视图
    private String response;//接受服务器传回的信息
    private String key = "";//搜索关键字文本
    JSONArray object;//接受并转化response为相应格式的数组
    private String[] urlStrs;//存放由服务器返回的url
    private WebView webView;//显示网页
    private boolean isVisible;//webView是否可见

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        listView = findViewById(R.id.listview);
        searchKey = findViewById(R.id.title_search);
        search = findViewById(R.id.search);
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        /*显示并处理请求服务器得到的信息*/
        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 0x123) {
                    //自定义适配器类
                    class MyAdapter extends BaseAdapter {
                        Context context;
                        LayoutInflater layoutInflater;//加载info_item.xml的布局加载器
                        String[] title;//新闻标题数组
                        String[] date;//日期数组
                        String[] imags;//图片部分路径及名称
                        int len = 0;//JSON数组的长度
                        String path = Environment.getExternalStorageDirectory()
                                + "/listviewImg/";// 存放图片文件目录
                        File fileDir;//文件目录

                        public MyAdapter(Context context) {
                            this.context = context;
                            fileDir = new File(path);
                            if (!fileDir.exists()) {
                                fileDir.mkdirs();
                            }
                            //初始化加载布局视图
                            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            try {
                                //将rsponse转化为JSON数组
                                object = new JSONArray(response);
                                len = object.length();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            /*设置存放新闻标题、日期、图片的数组的大小，
                             * 并将object数组中的信息取出放入各自对应的数组*/
                            title = new String[len];
                            date = new String[len];
                            imags = new String[len];
                            urlStrs = new String[len];
                            for (int i = 0; i < len; i++) {
                                try {
                                    JSONObject objectId = object.getJSONObject(i);
                                    title[i] = objectId.getString("title");
                                    date[i] = objectId.getString("date");
                                    imags[i] = objectId.getString("thumbnail_pic_s");
                                    urlStrs[i] = objectId.getString("url");
                                    File file = new File(fileDir, imags[i]);
                                    if (!file.exists()) {// 如果本地图片不存在则从服务器下载
                                        GetUtil.downloadPic(imags[i], path, fileDir);
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
                            //获取info_item.xml视图
                            View view = layoutInflater.inflate(R.layout.info_item, null);
                            TextView showTitle, datetime;//显示新闻标题和日期的视图
                            ImageView imageView;//显示图片
                            File file = new File(fileDir, imags[position]);//存放图片的本地文件地址
                            Bitmap bitmap = BitmapFactory//将图片解码取出
                                    .decodeFile(file.getAbsolutePath());
                            /*初始化视图组件，将相应的信息放入其中*/
                            showTitle = view.findViewById(R.id.title);
                            datetime = view.findViewById(R.id.date);
                            imageView = view.findViewById(R.id.pic);

                            showTitle.setText(title[position]);
                            datetime.setText(date[position]);
                            imageView.setImageBitmap(bitmap);
                            return view;
                        }
                    }
                    //获得BaseAdapter对象
                    MyAdapter adapter = new MyAdapter(NewsActivity.this);
                    listView.setAdapter(adapter);//添加适配器到listView
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            webView.setWebViewClient(new WebViewClient() {
                                @Override
                                public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                                    return false;
                                }

                                @Override
                                public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                                    searchKey.setVisibility(View.VISIBLE);
                                    search.setVisibility(View.VISIBLE);
                                    listView.setVisibility(View.VISIBLE);
                                    webView.setVisibility(View.GONE);
                                    isVisible = false;
                                    super.onPageStarted(view, url, favicon);
                                }

                                @Override
                                public void onPageFinished(final WebView view, final String url) {
                                    searchKey.setVisibility(View.GONE);
                                    search.setVisibility(View.GONE);
                                    listView.setVisibility(View.GONE);
                                    webView.setVisibility(View.VISIBLE);
                                    isVisible = true;
                                    super.onPageFinished(view, url);
                                }
                            });
                            webView.loadUrl(urlStrs[position]);
                        }
                    });
                }
            }
        };
        sendKey(key, handler);//初始化新闻列表

        search.setOnClickListener(view -> {//搜索事件监听器
            key = searchKey.getText().toString();
            sendKey(key, handler);//向服务器发出请求
        });
    }

    private void sendKey(String key, Handler handler) {
        new Thread(() -> {
            //发送key并将返回的信息放入response
            response = GetUtil.sendPost(
                    "http://192.168.62.1:8888/NewsServlet",
                    "key=" + key);
            //向handler发送消息
            handler.sendEmptyMessage(0x123);
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (isVisible){
            searchKey.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            isVisible = false;
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewGroup parent = findViewById(R.id.linearview);
        parent.removeView(webView);
    }
}
