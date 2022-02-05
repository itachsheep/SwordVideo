/**
 * @ClassName:      H264Manager.kt
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2/5/22 3:46 PM
 */
package com.tao.aac_h264_encode_decode

object H264Manager {

    /*************************** encode *****************************/
    external fun enc_init(h264Path: String, width: Int, height: Int,
                          videoBitRate: Int, frameRate: Int)
    external fun enc_encode(byteArray: ByteArray,yuvType:Int)
    external fun enc_destroy()
}