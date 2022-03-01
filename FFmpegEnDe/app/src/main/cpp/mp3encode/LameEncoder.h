//
// Created by wei tao on 2/25/22.
//

#ifndef FFMPEGENDE_LAMEENCODER_H
#define FFMPEGENDE_LAMEENCODER_H
#include "my_log.h"

extern "C" {
#include "lame.h"
};

class LameEncoder {
public:
    int init(const char *mp3SavePath, int sampleRate, int channels, uint64_t bitRate);

    int encode(uint8_t *pcm,int size);

    void release();

public:
    FILE *mp3File = NULL;
    lame_t lameClient;
    int sampleRate;
    int channel;
    int bit;
};


#endif //FFMPEGENDE_LAMEENCODER_H
