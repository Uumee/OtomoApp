LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE	:= liblive2d
LOCAL_SRC_FILES	:= ../../../../lib/android/$(TARGET_ARCH_ABI)/liblive2d.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_CPPFLAGS := -DL2D_TARGET_ANDROID

LOCAL_MODULE    := Live2DSimple
LOCAL_SRC_FILES := Live2DSimple.cpp

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../../../include
					
LOCAL_LDLIBS := -llog			
LOCAL_LDLIBS += -lGLESv1_CM		

LOCAL_WHOLE_STATIC_LIBRARIES += liblive2d

include $(BUILD_SHARED_LIBRARY)
