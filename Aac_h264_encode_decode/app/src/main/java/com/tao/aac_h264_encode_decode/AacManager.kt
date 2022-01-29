package com.tao.aac_h264_encode_decode

object AacManager {

    /*************************** decode *****************************/
    external fun initWithADTformat(): Int
//    external fun initWithRAWformat(specInfo: ByteArray, size: ByteArray):Int
    external fun decode(byteArray: ByteArray,len: Int): ByteArray
    external fun destroy()

    /***************************** basic learn  ***************************/
    external fun modifyByteArray(data: ByteArray): ByteArray
    external fun test_memcpy()
}