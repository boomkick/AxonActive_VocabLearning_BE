package org.acme.service.word;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.service.word.dto.BulkRequestWordDTO;
import org.acme.service.word.dto.RequestWordDTO;
import org.acme.service.word.dto.ResponseWordDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WordService {
    @Inject
    private WordDAO wordDAO;

    @Transactional
    public ResponseWordDTO add(RequestWordDTO requestWordDTO){
        Word word = Word.builder()
                .name(requestWordDTO.getName())
                .pronounce(requestWordDTO.getPronounce())
                .meaning(requestWordDTO.getMeaning())
                .studiedDay(requestWordDTO.getStudiedDay())
                .repeatDay1(requestWordDTO.getStudiedDay().plusDays(1L))
                .repeatDay2(requestWordDTO.getStudiedDay().plusDays(3L))
                .repeatDay3(requestWordDTO.getStudiedDay().plusDays(7L))
                .story(requestWordDTO.getStory())
                .build();

        return WordMapper.INSTANCE.toResponseWordDTO(wordDAO.add(word));
    }

    public ResponseWordDTO findWordById(String id){
        Optional<Word> wordOptional = wordDAO.findById(id);
        return wordOptional.map(WordMapper.INSTANCE::toResponseWordDTO).orElse(null);
    }

    public List<Word> findAllByAccountId() {
        return wordDAO.findAll();
    }

    public List<Word> findWordsRepeatTodayByAccountId() {
        return wordDAO.findWordsRepeatTodayByAccountId();
    }

    @Transactional
    public void removeWord(String id) {
        wordDAO.delete(id);
    }

    @Transactional
    public Word updateWord(String id, RequestWordDTO c) {
        Word word = WordMapper.INSTANCE.toWord(c);
        word.setId(id);
        return wordDAO.update(word);
    }

    @Transactional
    public List<ResponseWordDTO> createBulkWords(BulkRequestWordDTO bulkRequest) {
        List<ResponseWordDTO> words = new ArrayList<>();
        bulkRequest.getWords().forEach(requestWordDTO -> {
            words.add(this.add(requestWordDTO));
        });
        return words;
    }
}
