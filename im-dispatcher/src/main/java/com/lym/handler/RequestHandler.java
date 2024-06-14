package com.lym.handler;


import com.example.common.kafka.Producer;
import com.example.common.netty.Message;
import com.example.common.netty.Request;
import com.google.protobuf.InvalidProtocolBufferException;
import com.lym.protobuf.AuthenticateRequestProto;
import com.lym.protobuf.AuthenticateResponseProto;
import com.lym.protobuf.MessageSendRequestProto;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

import static com.example.common.constants.Constants.channelIdFunc;

@Slf4j
public class RequestHandler {

    private RequestHandler() {
    }

    private static final RequestHandler requestHandler = new RequestHandler();

    public static RequestHandler getInstance() {
        return requestHandler;
    }

    public AuthenticateResponseProto.AuthenticateResponse authenticate(AuthenticateRequestProto.AuthenticateRequest authenticateRequest) {
        long timestamp = authenticateRequest.getTimestamp();
        String toke = authenticateRequest.getToke();
        String uid = authenticateRequest.getUid();
        AuthenticateResponseProto.AuthenticateResponse.Builder builder = new AuthenticateResponseProto.AuthenticateResponse.Builder();
        builder.setToke(toke);
        builder.setUid(uid);
        builder.setTimestamp(timestamp);
        try {
            // todo sso
            log.info("authenticate time {} toke {} uid {}", timestamp, toke, uid);
            builder.setStatus(100);
        } catch (Exception e) {//todo
            builder.setStatus(500);
            builder.setErrorCode(-1);
            builder.setErrorMessage(e.getMessage());
        }
        return builder.build();


    }

    public void sendMessage(SocketChannel channel, Message message) throws InvalidProtocolBufferException {
        String channelId = channelIdFunc.apply(channel);
        Request request = message.toRequest();
        MessageSendRequestProto.MessageSendRequest messageSendRequest =
                MessageSendRequestProto.MessageSendRequest.parseFrom(request.getBody());

//        Producer producer = new Producer()
    }
}
