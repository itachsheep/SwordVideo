//
// Created by wei tao on 2022/3/15.
//

#include "pthread_test.h"

#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include "simple_log.h"



/**
 *  POSIX 线程创建
 *  https://blog.csdn.net/Kennethdroid/article/details/86666637
 */

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


int g_count = 0;



/**
 *  POSIX 线程同步
 */
// 互斥锁
pthread_mutex_t mutex;
void *asyn_run(void *arg){
    // lock
    pthread_mutex_lock(&mutex);

    // 取传入当前线程的参数
    char *thread_tag = (char*)arg;

    for (int i = 0; i < 10; ++i) {
        // 休眠 200 ms
        usleep(200 * 1000);
        g_count++;
        LOGD("%s thread %d, g_count = %d", thread_tag, i, g_count);
    }
    // unlock
    pthread_mutex_unlock(&mutex);

    return thread_tag; // 线程正常执行完成后的返回值
}

void syn_thread(){
    LOGD("Main thread");

    // 初始化互斥锁
    pthread_mutex_init(&mutex, NULL);

    pthread_t t1, t2;

    // 创建 2 个线程
    pthread_create(&t1, NULL, asyn_run, "No.1");
    pthread_create(&t2, NULL, asyn_run, "No.2");

    void *rtn_val[2];
    pthread_join(t1, &rtn_val[0]);
    pthread_join(t2, &rtn_val[1]);
    LOGD("thread %s terminated.", (char*)rtn_val[0]);
    LOGD("thread %s terminated.", (char*)rtn_val[1]);

    // 销毁互斥锁
    pthread_mutex_destroy(&mutex);

}


/**
* POSIX 线程间通信
 * https://blog.csdn.net/Kennethdroid/article/details/86666637
*/

// 共享数据
volatile int shared_count = 0;
volatile int countTime = 0;
pthread_mutex_t pthread_mutex;

// 条件变量
pthread_cond_t pthread_cond;


void *producer(void *arg){
    char *tag = (char*)arg;
    for (;;) {
        pthread_mutex_lock(&pthread_mutex);

        // 生产者生产产品
        shared_count++;
        countTime++;
        LOGD("%s thread 生产产品, count = %d, time = %d", tag, shared_count,countTime);

        // 通知消费者线程消费
        pthread_cond_signal(&pthread_cond);


        pthread_mutex_unlock(&pthread_mutex);

        if(countTime >= 15) {
            break;
        }
        // 休眠 200 ms
        usleep(500 * 1000);
    }

    return (void*)tag;
}

void *consumer(void *arg){
    char* tag = (char*)arg;
    for(;;){
        pthread_mutex_lock(&pthread_mutex);

        while (shared_count == 0){
            // 当没有产品可以消费时，等待生产者生产（等待条件变量被唤醒，当前线程释放互斥锁）
            // 当被其他线程唤醒时，解除阻塞状态，重新申请获得互斥锁
            pthread_cond_wait(&pthread_cond, &pthread_mutex);
        }

        //shared_count;
        LOGD("%s thread 消费产品, count = %d", tag, shared_count--);

        pthread_mutex_unlock(&pthread_mutex);

        if(countTime >= 15) {
            break;
        }
        // 休眠 500 ms
        usleep(100 * 1000);
    }
    return (void*)tag;

};

// 线程间通信
void communicate_thread(){
    pthread_mutex_init(&pthread_mutex, NULL);

    // 初始化条件变量
    pthread_cond_init(&pthread_cond, NULL);

    // 线程 id
    pthread_t producer_tid, consumer_tid;

    // 创建生产者线程
    pthread_create(&producer_tid, NULL, producer, "producer");
    // 创建消费者线程
    pthread_create(&consumer_tid, NULL, consumer, "consumer");

    // 等待线程结束
    void *rtn_val[2];
    pthread_join(producer_tid, &rtn_val[0]);
    pthread_join(consumer_tid, &rtn_val[1]);
    LOGD("thread %s terminated.", (char*)rtn_val[0]);
    LOGD("thread %s terminated.", (char*)rtn_val[1]);

    // 销毁互斥锁
    pthread_mutex_destroy(&pthread_mutex);
    // 销毁条件变量
    pthread_cond_destroy(&pthread_cond);

}