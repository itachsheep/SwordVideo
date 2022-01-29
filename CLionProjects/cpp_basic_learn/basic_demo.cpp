//
// Created by wei tao on 2022/1/29.
//

#include "basic_demo.h"

void calculate_char_length() {
    char *source = "taowei 123";
    int len = strlen(source);
    char *out = new char[len];
    memcpy(out,source,len);
    cout << "out = " << out << endl;
}