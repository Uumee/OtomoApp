package ume.oite.jp.otomoapp;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.live2d.android.Live2DModelAndroid;
import jp.live2d.android.UtOpenGL;
import jp.live2d.util.UtSystem;


/**
 * Created by Ume on 2015/07/17.
 */
public class Live2DSurfaceView extends GLSurfaceView {

    private SampleGLRenderer renderer;


    public Live2DSurfaceView(Context context) {
        super(context);
        renderer = new SampleGLRenderer();
        setRenderer(renderer);

    }

    public Live2DSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        renderer = new SampleGLRenderer();
        setRenderer(renderer);

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

        }

    }
}