package com.example.messageservice.repository;

import com.example.messageservice.domain.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MessageRepository extends ReactiveMongoRepository<Message, String> {
    Flux<Message> findAllBySenderIdAndReceiverId(Long senderId, Long receiverId);
    Flux<Message> findAllBySenderIdAndReceiverIdOrderBySentDateAsc(Long senderId, Long receiverId);

    Flux<Message> findAllBySenderId(Long senderId);

    Flux<Message> findAllBySenderIdOrReceiverIdOrderBySentDateAsc(Long senderId, Long receiverId);

    Flux<Message> findAllByReceiverIdOrderBySentDateDesc(Long receiverId);
}