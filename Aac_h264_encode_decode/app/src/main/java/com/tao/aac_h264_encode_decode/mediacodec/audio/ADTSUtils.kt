package com.tao.aac_h264_encode_decode.mediacodec.audio

object ADTSUtils {

    /**
     * 0: 96000 Hz
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
     */
    public external fun addADTStoPacket(
            packet: ByteArray,
            packetLen: Int = 7,
            profile: Int = 2,
            freqIdx: Int = 4,
            chanCfgCounts: Int = 1
    )
}