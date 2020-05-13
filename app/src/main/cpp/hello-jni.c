/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>
#include <stdlib.h>
#include <android/log.h>
#include <signal.h>
#include <unistd.h>
#include <linux/fcntl.h>
#include <fcntl.h>
#include <errno.h>
#include <android/multinetwork.h>

#define  LOG_TAG    "testjni"
#define  ALOG(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

void initDaemon();

JNIEXPORT void JNICALL
Java_com_isotope11_erlanglauncher_NativeInterface_startTask(JNIEnv *env,
                                                          jobject thiz,
                                                          jstring a) {
    const char *command = (*env)->GetStringUTFChars(env, a, 0);
    FILE *fp;
    char path[1035];

    /* Open the command for reading. */
    fp = popen(command, "r");
    if (fp == NULL) {
        ALOG("Failed to run command\n" );
        exit(1);
    }

    /* Read the output a line at a time - output it. */
    while (fgets(path, sizeof(path), fp) != NULL) {
        ALOG("%s", path);
    }

    /* close */
    pclose(fp);

}