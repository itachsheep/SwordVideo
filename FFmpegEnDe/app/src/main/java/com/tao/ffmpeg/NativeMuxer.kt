/**
 * @ClassName:      NativeMuxer
 * @Description:
 *
 * @author          taowei
 * @version         V1.0
 * @Date           2022/3/2 8:53 上午
 */
package com.tao.ffmpeg

class NativeMuxer {

    external fun aac_h264_muxerToMp4(h264Path: String,
                                     audioPath: String,
                                     outputPath: String)

}