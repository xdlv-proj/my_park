LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := plate_proc
LOCAL_SRC_FILES := plate_proc.cpp

include $(BUILD_SHARED_LIBRARY)
