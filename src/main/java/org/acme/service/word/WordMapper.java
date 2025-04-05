package org.acme.service.word;

import org.acme.service.word.dto.RequestWordDTO;
import org.acme.service.word.dto.ResponseWordDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface WordMapper {
    WordMapper INSTANCE = Mappers.getMapper(WordMapper.class);

    Word toWord(RequestWordDTO dto);

    RequestWordDTO toRequestWordDTO(Word word);
    ResponseWordDTO toResponseWordDTO(Word word);
    List<RequestWordDTO> toRequestWordDTOList(List<Word> words);
}
