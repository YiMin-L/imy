package com.example.common.netty;

import com.example.common.constants.Constants;
import com.example.common.enums.RequestType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Response extends Message {

    public Response(Request request, byte[] bodyBytes) {
        super(request.getHeaderLength(),
                request.getAppSdkVersion(),
                request.getMessageType(),
                request.getSequence(),
                RequestType.RESPONSE.getVal(),
                bodyBytes.length,
                bodyBytes
        );
    }

    public Response(int headerLength, int appSdkVersion, int messageType, int sequence, int bodyLength, byte[] body) {
        super(headerLength, appSdkVersion, messageType, sequence, RequestType.RESPONSE.getVal(), bodyLength, body);
    }

    public Response(ByteBuf byteBuf) {
        super(byteBuf);
    }
}
