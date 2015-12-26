
#include <jni.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#include <GLES/gl.h>
#include <GLES/glext.h>
#include <android/log.h>

#include "Live2D.h"
#include "Live2DModelAndroid.h"
#include "util/UtSystem.h"

using namespace live2d;
//////////////////////////////////////////////////////////////////////////////

#define LOG_TAG    "jni"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,  LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

//////////////////////////////////////////////////////////////////////////////

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT void JNICALL Java_jp_live2d_sample_JniBridge_nativeLoadTexture(JNIEnv* env, jobject thiz,jint no,jintArray pixels, jint width, jint height);
JNIEXPORT void JNICALL Java_jp_live2d_sample_JniBridge_nativeLoadLive2DModel(JNIEnv* env, jobject thiz,jbyteArray bytes, jint len);

JNIEXPORT void JNICALL Java_jp_live2d_sample_JniBridge_nativeOnSurfaceCreated(JNIEnv* env, jobject thiz);
JNIEXPORT void JNICALL Java_jp_live2d_sample_JniBridge_nativeOnSurfaceChanged(JNIEnv* env, jobject thiz, jint width, jint height);
JNIEXPORT void JNICALL Java_jp_live2d_sample_JniBridge_nativeOnDrawFrame(JNIEnv* env, jobject thiz);

#ifdef __cplusplus
}
#endif

//////////////////////////////////////////////////////////////////////////////

static void printGLString(const char *name, GLenum s) {
	const char *v = (const char *) glGetString(s);
	LOGI("GL %s = %s\n", name, v);
}



//////////////////////////////////////////////////////////////////////////////


static Live2DModelAndroid* live2DModel;


static float matrix[] = {
	1,0,0,0,
	0,1,0,0,
	0,0,1,0,
	0,0,0,1
};


JNIEXPORT void JNICALL Java_jp_live2d_sample_JniBridge_nativeLoadLive2DModel(JNIEnv* env, jobject thiz, jbyteArray bytes, jint len) {
	LOGI("nativeLoadLive2DModel");

	void* _bytes=  (void*) env->GetPrimitiveArrayCritical(bytes, 0);

	live2DModel=Live2DModelAndroid::loadModel( _bytes, (int)len);


	live2DModel->setPremultipliedAlpha(false);

	env->ReleasePrimitiveArrayCritical(bytes, _bytes, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_jp_live2d_sample_JniBridge_nativeLoadTexture(JNIEnv* env, jobject thiz,jint no, jintArray pixels, jint width, jint height) {
	LOGI("nativeLoadTexture");

	// テクスチャを作成します。(サーフェスが作成される度にこれを行う必要があります)

	unsigned int*  _pixels = (unsigned int*) env->GetPrimitiveArrayCritical(pixels, 0);

	// ARGB ⇒ RGBA へ変換します。
	const int size = width * height;
	for (int i = 0; i < size; i++) {
		unsigned int px = _pixels[i];
		_pixels[i] = (
				((px      ) & 0xFF000000) | // A
				((px << 16) & 0x00FF0000) | // R
				((px      ) & 0x0000FF00) | // G
				((px >> 16) & 0x000000FF)	// B
			);
	}
	static GLuint textures[1];

	// テクスチャオブジェクトを作成して画像を与えます。
	glGenTextures(1, textures);
	glBindTexture(GL_TEXTURE_2D, textures[0]);
	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, _pixels);

	// テクスチャを拡大/縮小する方法を設定します。
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);		// 縮小するときピクセルの中心に最も近いテクスチャ要素で補完
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);		// 拡大するときピクセルの中心付近の線形で補完

	LOGI("texture : %d ",no);

	live2DModel->setTexture(no,textures[0]);
	env->ReleasePrimitiveArrayCritical(pixels, _pixels, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_jp_live2d_sample_JniBridge_nativeOnSurfaceCreated(JNIEnv* env, jobject thiz) {
	LOGI("nativeOnSurfaceCreated");

	Live2D::init();

	printGLString("Version",    GL_VERSION);
	printGLString("Vendor",     GL_VENDOR);
	printGLString("Renderer",   GL_RENDERER);
	printGLString("Extensions", GL_EXTENSIONS);

}


JNIEXPORT void JNICALL Java_jp_live2d_sample_JniBridge_nativeOnSurfaceChanged(JNIEnv* env, jobject thiz, jint width, jint height) {
	// ビューポートを設定します。
	glViewport(0, 0, width, height);

	float w=live2DModel->getCanvasWidth();
	float h=live2DModel->getCanvasHeight();
	LOGI("Live2D Model w: %f  h: %f \n", w, h);

	matrix[0]=1.0/w*2;
	matrix[5]=-1.0/w*2.0*((float)width/height);

	matrix[12]=-1;
	matrix[13]=1;
	//live2DModel->setMatrix(matrix);
	//  簡易的にプロジェクション行列一つですべての変換を行う。
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();

	float modelWidth = live2DModel->getCanvasWidth(); //  モデラーで設定したキャンバス幅

	//  描画範囲の設定。引数はleft, right, bottom, top の順。
	glOrthof(
		 0,
		 modelWidth,
		 modelWidth * height / width,
		 0,
		 0.5f, -0.5f
		 );
}


JNIEXPORT void JNICALL Java_jp_live2d_sample_JniBridge_nativeOnDrawFrame(JNIEnv* env, jobject thiz) {
	// XXX - このサンプルではテクスチャの簡単な描画だけなので深さ関連の有効/無効や指定は一切していません。
	//  モデル用のOpenGLの描画関係を設定
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();

	// 背景色を指定して背景を描画します。
	glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
	glClear(GL_COLOR_BUFFER_BIT);

	double t = (UtSystem::getUserTimeMSec()/1000.0) * 2 * M_PI ;
	live2DModel->setParamFloat("PARAM_ANGLE_X", (float)(30 * sin( t/3.0 )) );

	live2DModel->update();
	live2DModel->draw();

}
