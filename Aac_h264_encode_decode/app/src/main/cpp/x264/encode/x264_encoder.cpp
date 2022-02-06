//
// Created by wei tao on 2/5/22.
//

#include "x264_encoder.h"

FILE *pFile = 0;
//FILE *pRFile = 0;

X264Encoder::X264Encoder(): mFps(25), pX264Codec(0), pIc_in(0), mCsp(X264_CSP_I420){

}

X264Encoder::~X264Encoder() {

}

/**
 *
 * @pParam->outH264Path
 * @pParam->width
 * @pParam->height
 * @pParam->videoBitRate
 * @pParam->frameRate
 */
void X264Encoder::init(const char *outH264Path, int width,
        int height, int videoBitRate, int frameRate) {
    simpleLog();
    this->mWidth = width;
    this->mHeight = height;
    this->mVideoBitRate = videoBitRate;
    this->mFps = frameRate;
    this->mYSize = width * height;
    this->mUVSize = mYSize / 4;

    pFile = fopen(outH264Path,"wb");
    //1. 配置编码器属性
    x264_param_t *pParam = new x264_param_t;
    x264_param_default_preset(pParam, x264_preset_names[0]/*最快*/,
            x264_tune_names[7]/*无延迟*/);
    //base_line 3.2 编码规格
    pParam->i_level_idc = 32;
    //输入数据格式
    pParam->i_csp = X264_CSP_I420;
    pParam->i_width = width;
    pParam->i_height = height;
    //无b帧
    pParam->i_bframe = 0;
    //参数i_rc_method表示码率控制，CQP(恒定质量)，CRF(恒定码率)，ABR(平均码率)
    pParam->rc.i_rc_method = X264_RC_ABR;
    //码率(比特率,单位Kbps)
    pParam->rc.i_bitrate = mVideoBitRate;
    //瞬时最大码率
    pParam->rc.i_vbv_max_bitrate = mVideoBitRate * 1.2;
    //设置了i_vbv_max_bitrate必须设置此参数，码率控制区大小,单位kbps
    pParam->rc.i_vbv_buffer_size = mVideoBitRate;

    //帧率
    pParam->i_fps_num = mFps;
    pParam->i_fps_den = 1;
    pParam->i_timebase_den = pParam->i_fps_num;
    pParam->i_timebase_num = pParam->i_fps_den;
//    pParam->pf_log = x264_log_default2;
    //用fps而不是时间戳来计算帧间距离
    pParam->b_vfr_input = 0;
    //帧距离(关键帧)  2s一个关键帧
    pParam->i_keyint_max = mFps * 2;
    // 是否复制sps和pps放在每个关键帧的前面 该参数设置是让每个关键帧(I帧)都附带sps/pps。
    pParam->b_repeat_headers = 1;
    //多线程
    pParam->i_threads = 1;

    x264_param_apply_profile(pParam, x264_profile_names[0]);
    //打开编码器
    pX264Codec = x264_encoder_open(pParam);
    //声明
    this->pIc_in = new x264_picture_t();
    //为图像结构体x264_picture_t分配内存。
    x264_picture_alloc(this->pIc_in, X264_CSP_I420, width, height);

    LogD("x264 init success!");

}

/**
 *
 * YUV 存储格式可以参考：
 * @see https://github.com/byhook/ffmpeg4android/blob/master/readme/%E5%9B%BE%E8%A7%A3YU12%E3%80%81I420%E3%80%81YV12%E3%80%81NV12%E3%80%81NV21%E3%80%81YUV420P%E3%80%81YUV420SP%E3%80%81YUV422P%E3%80%81YUV444P%E7%9A%84%E5%8C%BA%E5%88%AB.md
 *
 * 正式编码，这里的 packet 是 YUV 的包装类，接收 I420 预览数据
 * @param packet
 */
void X264Encoder::encode(AVPacket *packet) {
    if (!packet || !packet->data) {
        LogE("encode error, packet or packet->data is null!");
        return;
    }

    //copy Y 数据
    memcpy(this->pIc_in->img.plane[0], packet->data, this->mYSize);
    switch (packet->type){
        case YUV420p:
            //copy U 数据
            memcpy(this->pIc_in->img.plane[1],
                    packet->data + this->mYSize,this->mUVSize);
            //copy V 数据 packet->data + this->mYSize + this->mUVSize == packet->data + this->mYSize*5/4
            memcpy(this->pIc_in->img.plane[2],
                    packet->data + this->mYSize + this->mUVSize, this->mUVSize);
            break;

        case YUV420sp:
            for (int i = 0; i < this->mUVSize; ++i) {
                //拿到 u 数据
                *(pIc_in->img.plane[1] + i) = *(packet->data + this->mYSize + i * 2 + 1);
                //拿到 v 数据
                *(pIc_in->img.plane[2] + i) = *(packet->data + this->mYSize + i * 2);
            }
            break;
    }
    //编码出来的数据
    x264_nal_t *pp_nal;
    x264_picture_t pic_out;

    //编码出来的帧数量
    int pi_nal = 0;
    int ret = x264_encoder_encode(pX264Codec, &pp_nal, &pi_nal, pIc_in, &pic_out);
    if (!ret) {
        LogE("编码失败");
        return;
    }

    //写入文件
    fwrite(pp_nal->p_payload, ret, 1, pFile);

    //如果是关键帧
    int sps_len = 0;
    int pps_len = 0;
    uint8_t sps[100];
    uint8_t pps[100];
    for (int i = 0; i < pi_nal; ++i) {
        if (pp_nal[i].i_type == NAL_SPS) {
            //排除掉 h264的间隔 00 00 00 01
            sps_len = pp_nal[i].i_payload - 4;
            memcpy(sps, pp_nal[i].p_payload + 4, sps_len);
        } else if (pp_nal[i].i_type == NAL_PPS) {
            pps_len = pp_nal[i].i_payload - 4;
            memcpy(pps, pp_nal[i].p_payload + 4, pps_len);
            //pps肯定是跟着sps的
            sendSpsPps(sps, pps, sps_len, pps_len);
        } else {
            sendFrame(pp_nal[i].i_type, pp_nal[i].p_payload, pp_nal[i].i_payload, 0);
        }
    }
}

void X264Encoder::destroy() {
    simpleLog();
    if (pX264Codec) {
        x264_encoder_close(pX264Codec);
        pX264Codec = 0;
    }
    if (pIc_in) {
        x264_picture_clean(pIc_in);
        pIc_in = 0;
    }
    if (pFile) {
        fclose(pFile);
    }
}

/**
 *
 * @param sps 编码的第一帧
 * @param pps 编码的第二帧
 * @param sps_len
 * @param pps_len
 */
void X264Encoder::sendSpsPps(uint8_t *sps, uint8_t *pps, int sps_len, int pps_len) {
    simpleLog();
}

/**
 *
 * @param type
 * @param payload
 * @param i_payload
 * @param timestamp
 */
void X264Encoder::sendFrame(int type, uint8_t *payload, int i_payload, long timestamp) {
    simpleLog();
}