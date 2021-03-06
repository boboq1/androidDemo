package com.zzu.hechenbo.shiyan61;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class GetUtil {
    public static String sendGet(String url, String params) {
        String result = null;
        BufferedReader in = null;
        String urlname = url + "?" + params;
        try {
            URL url1 = new URL(urlname);
            URLConnection conn = url1.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SVl)");
            conn.connect();
            Map<String, List<String>> map = conn.getHeaderFields();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String sendPost(String url, String params) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SVl)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(params);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    public static void downloadPic(final String img, String path , File fileDir) {
        new Thread(() -> {
            FileOutputStream os = null;
            InputStream is = null;
            File file = new File(path,img.substring(7));
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
