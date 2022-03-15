//
// Created by wei tao on 2022/3/15.
//

#include "pthread_test.h"

#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include "simple_log.h"
#define NUM_THREADS 5

// 类似于 Java Runnable
void *run(void *arg){
    // 取传入当前线程的参数
    char *thread_tag = (char*)arg;

    for (int i = 0; i < 5; ++i) {
        LOGD("%s thread %d", thread_tag, i);
        sleep(1);
        if (i == 4) {
            // 结束当前线程,参数为线程结束后的返回值
            pthread_exit(thread_tag);
            //pthread_cancel(); send a cancellation request to a thread
        }
    }

    return 0; // 线程正常执行完成后的返回值
}

void create_threads(){
    LOGD("Main thread");
    char tag_arr[][5] = {"No.1","No.2","No.3","No.4","No.5"};

    //线程 id ，用于区分线程，一个线程对应一个唯一的 id
    pthread_t tids[NUM_THREADS];
    for (int i = 0; i < NUM_THREADS; ++i) {
        // 创建线程，指定 run 方法，传入参数 tags[i]
        pthread_create(&tids[i], NULL, run, (void *) tag_arr[i]);
    }

    void *return_val[NUM_THREADS];
    for (int i = 0; i < NUM_THREADS; ++i) {
        // 阻塞当前线程，等待指定 tid 的线程结束，并获取线程返回值
        // join with a terminated thread
        pthread_join(tids[i], &return_val[i]);
        LOGD("thread %s terminated.", (char*)return_val[i]);
    }
}