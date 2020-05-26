package com.zzu.hechenbo.shiyan9;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;


public class LiveWallpaper extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new MyEngine();
    }

    public class MyEngine extends Engine {//壁纸的生命周期、Surface状态的变化以及对用户的输入事件进行响应
        private MyGLSurfaceView myGLSurfaceView;
        private MyRenderer myRenderer;
        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            myGLSurfaceView = new MyGLSurfaceView(LiveWallpaper.this);

                myRenderer = new MyRenderer(LiveWallpaper.this);
                // 设置渲染
                myGLSurfaceView.setRenderer(myRenderer);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.d("LSeven", "MyEngine#onVisibilityChanged visible = " + visible);
               if (visible){
                   myGLSurfaceView.onResume();
               }else {
                   myGLSurfaceView.onPause();
               }

        }
        public class MyGLSurfaceView extends GLSurfaceView {
            public MyGLSurfaceView(Context context) {
                super(context);
            }
            @Override
            public SurfaceHolder getHolder() {
                return MyEngine.this.getSurfaceHolder();
            }
            public void onWallpaperDestroy(){
                super.onDetachedFromWindow();
            }

        }
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            myGLSurfaceView.onWallpaperDestroy();
        }
    }

}
