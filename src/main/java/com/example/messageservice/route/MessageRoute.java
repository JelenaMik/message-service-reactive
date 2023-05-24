package com.example.messageservice.route;

import com.example.messageservice.handler.MessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class MessageRoute {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(MessageHandler messageHandler){
        return route()
                .nest(path("/v1/messages"), builder ->
                        builder
                                .POST("", messageHandler::addMessage)
                                .GET("/{receiverId}/{senderId}", messageHandler::getMessages)
                                .GET("/{userId}", messageHandler::getAllMessages)
                                .PUT("/{id}", messageHandler::updateMessage)
                                .DELETE("/{id}", messageHandler::deleteMessage)
                ).build();
    }

}
