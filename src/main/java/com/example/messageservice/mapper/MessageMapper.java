package com.example.messageservice.mapper;
import com.example.messageservice.domain.Message;
import com.example.messageservice.dto.MessageDto;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface MessageMapper {
    Message dtoToEntity(MessageDto messageDto);
    MessageDto entityToDto(Message message);
}
