#include <jni.h>
#include <com_parking_PlateProc.h>
JNIEXPORT jstring JNICALL Java_com_parking_PlateProc_plateProc
  (JNIEnv *env, jclass thiz, jintArray piex, jint width, jint height)
{
	return env->NewStringUTF("��A12345");
}
