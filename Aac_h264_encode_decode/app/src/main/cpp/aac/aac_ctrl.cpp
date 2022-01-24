//
// Created by wei tao on 2022/1/24.
//

#include "aac_ctrl.h"
#include "decode/aac_decoder.h"

AACDecoder *aacDecoder = 0;

int initWithADTSFormat() {
    simpleLog();
    int ret = 0;
    if(!aacDecoder) {
        aacDecoder = new AACDecoder();
    }
    ret = aacDecoder->initWithADTSFormat();
    return ret;
}
