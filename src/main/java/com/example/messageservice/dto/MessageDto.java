package com.example.messageservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private String id;
    @NonNull
    private Long senderId;
    @NonNull
    private Long receiverId;
    @NonNull
    @NotBlank
    private String content;
    @CreatedDate
    private LocalDateTime sentDate;
}

