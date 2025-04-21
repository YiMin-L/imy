package com.example.common.constants;

import io.netty.channel.socket.SocketChannel;

import java.util.function.Function;

public interface Constants {

    /* message*/

    // headerLength是int类型，4个字节
    int head_length_length = 4;
    int body_length_length = 4;
    /**
     * 消息头的总长度
     */
    int request_header_length = 24;

    /**
     * app sdk版本号
     */
    int app_sdk_version = 1;
    /**
     * 请求类型授权
     */
    int request_type_auth = 1;
    /**
     * 消息顺序
     */
    int request_sequence_default = 1;

    int AUTH_STATUS_SUCCESS = 200;

    int message_type_single = 1;
    int message_type_group = 2;


    Function<SocketChannel, String> channelIdFunc = (socketChannel) -> String.format("%s:%s", socketChannel.remoteAddress().getHostName(), socketChannel.remoteAddress().getPort());
}
