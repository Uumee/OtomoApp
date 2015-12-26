package ume.oite.jp.otomoapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.AttributeSet;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import jp.live2d.android.Live2DModelAndroid;
import jp.live2d.android.UtOpenGL;
import jp.live2d.util.UtSystem;


/**
 * Created by Ume on 2015/07/17.
 */
public class Live2DSurfaceView extends GLSurfaceView {

    private SampleGLRenderer2 renderer;
    private int surfaceWidth,surfaceHeight;

    public Live2DSurfaceView(Context context) {
        super(context);
        renderer = new SampleGLRenderer2();
        setRenderer(renderer);
        surfaceWidth = this.getWidth();
        surfaceHeight = this.getHeight();
    }

    public Live2DSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        renderer = new SampleGLRenderer2();
        setRenderer(renderer);
        surfaceWidth = this.getWidth();
        surfaceHeight = this.getHeight();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        surfaceWidth = this.getWidth();
        surfaceHeight = this.getHeight();
    }

    class SampleGLRenderer implements GLSurfaceView.Renderer {

        private Live2DModelAndroid live2Dmodel = null;
        private final String MODEL_PATH = "haru/haru.moc";
        private final String TEXTURE_PATHS[] = {
                "haru/haru.1024/texture_00.png",
                "haru/haru.1024/texture_01.png",
                "haru/haru.1024/texture_02.png"
        };

        public SampleGLRenderer() {

        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // initialize model

            try {
                InputStream in1 = getContext().getAssets().open(MODEL_PATH);

                live2Dmodel = Live2DModelAndroid.loadModel(in1);
                in1.close();
                for (int i = 0; i < TEXTURE_PATHS.length; i++) {
                    InputStream in2 = getContext().getAssets().open(TEXTURE_PATHS[i]);
                    int texNo = UtOpenGL.loadTexture(gl, in2, true);
                    live2Dmodel.setTexture(i, texNo);
                }
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();

            float modelWidth = live2Dmodel.getCanvasWidth();
            float aspect = (float) width / height;

            gl.glOrthof(0, modelWidth, modelWidth / aspect, 0, 0.5f, -0.5f);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

            double t = (UtSystem.getUserTimeMSec() / 1000.0) * 2 * Math.PI;
            double cycle = 3.0;
            double sin = Math.sin(t / cycle);

            live2Dmodel.setParamFloat("PARAM_ANGLE_X", (float) (30 * sin));
            live2Dmodel.setGL(gl);
            live2Dmodel.update();
            live2Dmodel.draw();

            Log.d("d",surfaceWidth+","+surfaceHeight);

        }

    }

    class SampleRenderer implements Renderer{

        private Context context;
        SampleSprite sprite = new SampleSprite();

        public SampleRenderer(Context context){
            this.context = context;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glClearColor(1.0f,1.0f,1.0f,1.0f);
            gl.glDisable(GL10.GL_DITHER);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glEnable(GL10.GL_TEXTURE_2D);
            gl.glEnable(GL10.GL_ALPHA_TEST);
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);

            sprite.setTexture(gl,context.getResources(), R.drawable.surface_background_1024_768);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0,0,width,height);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            GLU.gluOrtho2D(gl,0.0f,width,0.0f,height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            sprite.draw(gl);
        }
    }


    class SampleSprite {

        //テクスチャID
        public int id;
        //表示位置
        private float[] position = new float[3];
        //テクスチャの位置
        private int[] texPos = new int[2];
        //テクスチャのサイズ
        private int[] texSize = new int[2];
        //配置時のサイズ
        private int[] size = new int[2];

        public void setTexture(GL10 gl , Resources res ,int id){
            InputStream is = res.openRawResource(id);
            Bitmap bitmap = null;
            try{
                bitmap = BitmapFactory.decodeStream(is);
            }finally{
                try{
                    is.close();
                }catch(IOException e){
                    //none
                }
            }
            gl.glEnable(GL10.GL_ALPHA_TEST);
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl.glTexEnvf(GL10.GL_TEXTURE_ENV,GL10.GL_TEXTURE_ENV_MODE,GL10.GL_MODULATE);
            int[] texture_id = new int[1];
            gl.glGenTextures(1,texture_id,0);
            id = texture_id[0];

            gl.glBindTexture(GL10.GL_TEXTURE_2D,id);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,bitmap,0);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_WRAP_S,GL10.GL_REPEAT);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_WRAP_T,GL10.GL_REPEAT);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);

            texPos[0] = 0;
            texPos[1] = bitmap.getHeight();
            texSize[0] = bitmap.getWidth();
            texSize[1] = -bitmap.getHeight();
            position[0] = 0;
            position[1] = 0;
            position[2] = 0;

        }

        public void draw(GL10 gl){
            gl.glDisable(GL10.GL_DEPTH_TEST);
            gl.glColor4f(0x10000,0x10000,0x10000,0x10000);
            gl.glActiveTexture(GL10.GL_TEXTURE0);
            gl.glBindTexture(GL10.GL_TEXTURE_2D,id);
            int rect[] = {texPos[0],texPos[1],size[0],size[1]};
            ((GL11)gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES,rect,0);
            ((GL11Ext)gl).glDrawTexfOES(position[0],position[1],position[2],size[0],size[1]);

            gl.glEnable(GL10.GL_DEPTH_TEST);
        }



    }
}