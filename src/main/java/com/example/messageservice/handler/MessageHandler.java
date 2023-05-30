package com.example.messageservice.handler;

import com.example.messageservice.domain.Message;
import com.example.messageservice.mapper.MessageMapper;
import com.example.messageservice.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageHandler {
    private final MessageRepository messageRepository;
    private final MessageMapper mapper;

    Sinks.Many<Message> messagesSink = Sinks.many().replay().latest();

    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> addMessage(ServerRequest serverRequest){
        return serverRequest.bodyToMono(Message.class)
                .map(message -> {
                    message.setSentDate(LocalDateTime.now());
                    return message;
                })
                .flatMap(messageRepository::save)
                .map(mapper::entityToDto)
//                .doOnNext(message -> messagesSink.tryEmitNext(message))
                .flatMap(savedMessage -> ServerResponse.status(HttpStatus.CREATED)
                        .bodyValue(savedMessage));
    }


    public Mono<ServerResponse> getMessages(ServerRequest serverRequest) {
        Long receiverId = Long.valueOf(serverRequest.pathVariable("receiverId"));
        Long senderId = Long.valueOf(serverRequest.pathVariable("senderId"));
        var messages = messageRepository.findAllBySenderIdAndReceiverId(senderId, receiverId).mergeWith(
                messageRepository.findAllBySenderIdAndReceiverId(receiverId, senderId)
        ).collectSortedList(Comparator.comparing(Message::getSentDate));
        return ServerResponse.ok().body(messages, List.class).log();
    }
    private Mono<ServerResponse> buildMessageResponse(Flux<Message> messages) {
        return ServerResponse.ok()
                .body(messages, Message.class).log();
    }

    public Mono<ServerResponse> getAllMessages(ServerRequest serverRequest) {
        Long userId = Long.valueOf(serverRequest.pathVariable("userId"));
        var messages = messageRepository.findAllBySenderIdOrReceiverIdOrderBySentDateAsc(userId, userId);
        return buildMessageResponse(messages).log();
    }

    public Mono<ServerResponse> updateMessage(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        var existingMessage = messageRepository.findById(id);

        return existingMessage
                .flatMap(
                        message -> serverRequest.bodyToMono(Message.class)
                                .map(messageUpdated -> {
                                        message.setContent(messageUpdated.getContent());
                                        return message;
                                })
                                .flatMap(messageRepository::save)
                                .flatMap(savedMessage -> ServerResponse.ok().bodyValue(savedMessage)
                                        .switchIfEmpty(notFound))
                );

    }

    public Mono<ServerResponse> deleteMessage(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return messageRepository.findById(id)
                        .flatMap(message -> messageRepository.deleteById(id))
                                .then(ServerResponse.noContent().build());
    }
}
