package com.example.localhistory.token;

import com.example.localhistory.token.dto.response.TokenDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenMapper {
    TokenDTO toDTO(Token model);
}
