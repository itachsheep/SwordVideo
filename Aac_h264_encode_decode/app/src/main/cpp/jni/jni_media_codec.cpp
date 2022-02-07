//
// Created by wei tao on 2/7/22.
//

#include <jni.h>
#include "put_bits.h"


/**
 *
 * ADTS packet 信息可以参考：https://www.jianshu.com/p/5c770a22e8f8
 *
 * 参考ffmpeg源码中 添加 ADTS 头部
 *
 * @param packetLen 该帧的长度，包括header的长度
 * @param profile  0-Main profile, 1-AAC LC，2-SSR
 * @param freqIdx    采样率
                    0: 96000 Hz
                    1: 88200 Hz
                    2: 64000 Hz
                    3: 48000 Hz
                    4: 44100 Hz
                    5: 32000 Hz
                    6: 24000 Hz
                    7: 22050 Hz
                    8: 16000 Hz
                    9: 12000 Hz
                    10: 11025 Hz
                    11: 8000 Hz
                    12: 7350 Hz
                    13: Reserved
                    14: Reserved
                    15: frequency is written explictly
* @param chanCfg    通道
                    2：L+R
                    3：C+L+R
*/
void addADTStoPacket(uint8_t *packet, int packetLen, int sampleInHz, int chanCfgCounts, int profile) {
    int freqIdx = 4; // 16KHz    39=MediaCodecInfo.CodecProfileLevel.AACObjectELD;
    switch (sampleInHz) {
        case 8000: {
            freqIdx = 11;
            break;
        }
        case 16000: {
            freqIdx = 8;
            break;
        }
        case 32000: {
            freqIdx = 5;
            break;
        }
        case 44100: {
            freqIdx = 4;
            break;
        }
        case 48000: {
            freqIdx = 3;
            break;
        }
        case 96000: {
            freqIdx = 0;
            break;
        }
        default:
            break;
    }
    int chanCfg = chanCfgCounts; // CPE
    // fill in ADTS data
    packet[0] = 0xFF;
    packet[1] = 0xF1;
    packet[2] = (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
    packet[3] = (((chanCfg & 3) << 6) + (packetLen >> 11));
    packet[4] = ((packetLen & 0x7FF) >> 3);
    packet[5] = (((packetLen & 7) << 5) + 0x1F);
    packet[6] = 0xFC;

}



extern "C"
JNIEXPORT void JNICALL
Java_com_tao_aac_1h264_1encode_1decode_mediacodec_ADTSUtils_addADTStoPacket(JNIEnv *env,
        jobject thiz,jbyteArray byteArray,jint len,
        jint profile,jint sampleRate,jint channels) {
    jbyte *arr = env->GetByteArrayElements(byteArray, 0);
    addADTStoPacket(reinterpret_cast<uint8_t *>(arr), len, sampleRate, channels, profile);
    env->ReleaseByteArrayElements(byteArray, arr, 0);
}

