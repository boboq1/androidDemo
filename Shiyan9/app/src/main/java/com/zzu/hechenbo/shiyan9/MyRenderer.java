package com.zzu.hechenbo.shiyan9;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer
{
    private long startTime;                            //保存开始时间
    // 立方体的顶点坐标（一共是36个顶点，组成12个三角形）
    private float[] cubeVertices = {
            -0.6f, -0.6f, -0.6f, -0.6f, 0.6f, -0.6f,
            0.6f, 0.6f, -0.6f, 0.6f, 0.6f, -0.6f,
            0.6f, -0.6f, -0.6f, -0.6f, -0.6f, -0.6f,
            -0.6f, -0.6f, 0.6f, 0.6f, -0.6f, 0.6f,
            0.6f, 0.6f, 0.6f, 0.6f, 0.6f, 0.6f,
            -0.6f, 0.6f, 0.6f, -0.6f, -0.6f, 0.6f,
            -0.6f, -0.6f, -0.6f, 0.6f, -0.6f, -0.6f,
            0.6f, -0.6f, 0.6f, 0.6f, -0.6f, 0.6f,
            -0.6f, -0.6f, 0.6f, -0.6f, -0.6f, -0.6f,
            0.6f, -0.6f, -0.6f, 0.6f, 0.6f, -0.6f,
            0.6f, 0.6f, 0.6f, 0.6f, 0.6f,0.6f,
            0.6f, -0.6f, 0.6f, 0.6f, -0.6f, -0.6f,
            0.6f, 0.6f, -0.6f, -0.6f, 0.6f, -0.6f,
            -0.6f, 0.6f, 0.6f, -0.6f, 0.6f, 0.6f,
            0.6f, 0.6f, 0.6f, 0.6f, 0.6f, -0.6f,
            -0.6f, 0.6f, -0.6f, -0.6f, -0.6f,-0.6f,
            -0.6f, -0.6f, 0.6f, -0.6f, -0.6f, 0.6f,
            -0.6f, 0.6f, 0.6f, -0.6f, 0.6f, -0.6f
    };
    // 定义立方体所需要的6个面（一共是12个三角形所需的顶点）
    private byte[] cubeFacets = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
            13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
            30, 31, 32, 33, 34, 35, };
    // 定义纹理贴图的坐标数据
    private float[] cubeTextures = {
            1.0000f, 1.0000f,   1.0000f, 0.0000f,   0.0000f, 0.0000f,
            0.0000f, 0.0000f,   0.0000f, 1.0000f,   1.0000f, 1.0000f,
            0.0000f, 1.0000f,   1.0000f, 1.0000f,   1.0000f, 0.0000f,
            1.0000f, 0.0000f,   0.0000f, 0.0000f,   0.0000f, 1.0000f,
            0.0000f, 1.0000f,   1.0000f, 1.0000f,   1.0000f, 0.0000f,
            1.0000f, 0.0000f,   0.0000f, 0.0000f,   0.0000f, 1.0000f,
            0.0000f, 1.0000f,   1.0000f, 1.0000f,   1.0000f, 0.0000f,
            1.0000f, 0.0000f,   0.0000f, 0.0000f,   0.0000f, 1.0000f,
            0.0000f, 1.0000f,   1.0000f, 1.0000f,   1.0000f, 0.0000f,
            1.0000f, 0.0000f,   0.0000f, 0.0000f,   0.0000f, 1.0000f,
            0.0000f, 1.0000f,   1.0000f, 1.0000f,   1.0000f, 0.0000f,
            1.0000f, 0.0000f,   0.0000f, 0.0000f,   0.0000f, 1.0000f
    };
    private FloatBuffer cubeVerticesBuffer;
    private ByteBuffer cubeFacetsBuffer;
    private FloatBuffer cubeTexturesBuffer;
    // 定义本程序所使用的纹理
    private int texture;
    Context context;
    public MyRenderer(Context main)
    {
        context = main;
        // 将立方体的顶点位置数据数组包装成FloatBuffer;
        cubeVerticesBuffer = floatBufferUtil(cubeVertices);
        // 将立方体的6个面（12个三角形）的数组包装成ByteBuffer
        cubeFacetsBuffer = ByteBuffer.wrap(cubeFacets);
        // 将立方体的纹理贴图的坐标数据包装成FloatBuffer
        cubeTexturesBuffer = floatBufferUtil(cubeTextures);
        startTime=System.currentTimeMillis();      //系统当前时间


    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        // 关闭抗抖动
        gl.glDisable(GL10.GL_DITHER);
        // 设置系统对透视进行修正
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glClearColor(0, 0, 0, 0);
        // 设置阴影平滑模式
        gl.glShadeModel(GL10.GL_SMOOTH);
        // 启用深度测试
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // 设置深度测试的类型
        gl.glDepthFunc(GL10.GL_LEQUAL);
        // 启用2D纹理贴图
        gl.glEnable(GL10.GL_TEXTURE_2D);
        // 装载纹理
        loadTexture(gl);

        float matAmbient[]=new float[]{1,1,1,1};         //定义材质的环境光
        float matDiffuse[]=new float[]{1,1,1,1};         //定义材质的散射光
        //设置材质的环境光
        // gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient,0);
        //设置材质的散射光
        //gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse,0);
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        // 设置3D视窗的大小及位置
        gl.glViewport(0, 0, width, height);
        // 将当前矩阵模式设为投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // 初始化单位矩阵
        gl.glLoadIdentity();
        // 计算透视视窗的宽度、高度比
        float ratio = (float) width / height;
        // 调用此方法设置透视视窗的空间大小。
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);

        float lightAmbient[]=new float[]{0.2f,0.2f,0.2f,1};             //定义环境光
        float lightDiffuse[]=new float[]{1,1,1,1};                    //定义散射光
        float lightSpecular[]=new float[]{1,1,1,1};                    //定义散射光
        float lightPos[]=new float[]{1,1,1,1};                      //定义光源的位置
        gl.glEnable(GL10.GL_LIGHTING);                            //启用光源
        gl.glEnable(GL10.GL_LIGHT0);                              //启用0号光源
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient,0); //设置环境光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);    //设置散射光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, lightSpecular, 0);    //设置散射光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);      //设置光源的位置
    }
    public void onDrawFrame(GL10 gl)
    {
        // 清除屏幕缓存和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // 启用顶点坐标数据
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // 启用贴图坐标数组数据
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);   // ①
        // 设置当前矩阵模式为模型视图。
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        // 把绘图中心移入屏幕2个单位
        gl.glTranslatef(0f, 0.0f, -2.0f);

        //旋转
        long elapsed = System.currentTimeMillis() - startTime;       //计算逝去的时间
        gl.glRotatef(elapsed * (30f / 1000f), 0, 1, 0);                //在y轴上旋转30度
        gl.glRotatef(elapsed * (15f / 1000f), 1, 0, 0);                //在x轴上旋转15度

        // 设置顶点的位置数据
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeVerticesBuffer);
        // 设置贴图的坐标数据
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, cubeTexturesBuffer);  // ②
        // 执行纹理贴图
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);  // ③
        // 按cubeFacetsBuffer指定的面绘制三角形
        gl.glDrawElements(GL10.GL_TRIANGLES, cubeFacetsBuffer.remaining(),
                GL10.GL_UNSIGNED_BYTE, cubeFacetsBuffer);
        // 绘制结束
        gl.glFinish();
        // 禁用顶点、纹理坐标数组
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        // 递增角度值以便每次以不同角度绘制
    }
    private void loadTexture(GL10 gl)
    {
        Bitmap bitmap = null;
        try
        {
            // 加载位图

            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.bg);
            int[] textures = new int[1];
            // 指定生成N个纹理（第一个参数指定生成一个纹理）
            // textures数组将负责存储所有纹理的代号
            gl.glGenTextures(1, textures, 0);
            // 获取textures纹理数组中的第一个纹理
            texture = textures[0];
            // 通知OpenGL将texture纹理绑定到GL10.GL_TEXTURE_2D目标中
            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
            // 设置纹理被缩小（距离视点很远时被缩小）时的滤波方式
            gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                    GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            // 设置纹理被放大（距离视点很近时被方法）时的滤波方式
            gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                    GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            // 设置在横向、纵向上都是平铺纹理
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                    GL10.GL_REPEAT);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                    GL10.GL_REPEAT);
            // 加载位图生成纹理
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        }
        finally
        {
            // 生成纹理之后，回收位图
            if (bitmap != null)
                bitmap.recycle();
        }
    }
    // 定义一个工具方法，将float[]数组转换为OpenGL ES所需的FloatBuffer
    private FloatBuffer floatBufferUtil(float[] arr)
    {
        FloatBuffer mBuffer;
        // 初始化ByteBuffer，长度为arr数组的长度*4，因为一个int占4个字节
        ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * 4);
        // 数组排列用nativeOrder
        qbb.order(ByteOrder.nativeOrder());
        mBuffer = qbb.asFloatBuffer();
        mBuffer.put(arr);
        mBuffer.position(0);
        return mBuffer;
    }
}
