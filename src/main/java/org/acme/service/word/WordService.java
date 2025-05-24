package org.acme.service.word;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.service.account.Account;
import org.acme.service.account.AccountDAO;
import org.acme.service.word.dto.BulkRequestWordDTO;
import org.acme.service.word.dto.RequestPatchWordDTO;
import org.acme.service.word.dto.RequestWordDTO;
import org.acme.service.word.dto.ResponseWordDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class WordService {
    @Inject
    private WordDAO wordDAO;

    @Inject
    private AccountDAO accountDAO;

    @Transactional
    public ResponseWordDTO add(RequestWordDTO requestWordDTO, String accountId) {
        Word word = Word.builder()
                .account(accountDAO.findById(accountId).orElse(null))
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

    public ResponseWordDTO findWordById(String id) {
        Optional<Word> wordOptional = wordDAO.findById(id);
        return wordOptional.map(WordMapper.INSTANCE::toResponseWordDTO).orElse(null);
    }

    public List<Word> findAllByAccountId(String accountId) {
        return wordDAO.findAllByAccountId(accountId);
    }

    public List<Word> findWordsRepeatTodayByAccountId(String accountId) {
        return wordDAO.findWordsRepeatTodayByAccountId(accountId);
    }

    @Transactional
    public void removeWord(String id) {
        wordDAO.delete(id);
    }

    @Transactional
    public void updateWord(String id, RequestWordDTO c) {
        Word word = WordMapper.INSTANCE.toWord(c);
        word.setId(id);
        wordDAO.update(word);
    }

    @Transactional
    public void updatePartialWord(String id, RequestPatchWordDTO requestPatchWordDTO) {
        Word word = Optional.of(wordDAO.findById(id)).orElseThrow(() -> new NotFoundException("Word not found")).get();
        if (Objects.nonNull(requestPatchWordDTO.getName())) {
            word.setName(requestPatchWordDTO.getName());
        }
        if (Objects.nonNull(requestPatchWordDTO.getPronounce())) {
            word.setPronounce(requestPatchWordDTO.getPronounce());
        }
        if (Objects.nonNull(requestPatchWordDTO.getMeaning())) {
            word.setMeaning(requestPatchWordDTO.getMeaning());
        }
        if (Objects.nonNull(requestPatchWordDTO.getStudiedDay())) {
            word.setStudiedDay(requestPatchWordDTO.getStudiedDay());
        }
        if (Objects.nonNull(requestPatchWordDTO.getRepeatDay1())) {
            word.setRepeatDay1(requestPatchWordDTO.getRepeatDay1());
        }
        if (Objects.nonNull(requestPatchWordDTO.getStatusDay1())) {
            word.setStatusDay1(String.valueOf(requestPatchWordDTO.getStatusDay1()));
        }
        if (Objects.nonNull(requestPatchWordDTO.getRepeatDay2())) {
            word.setRepeatDay2(requestPatchWordDTO.getRepeatDay2());
        }
        if (Objects.nonNull(requestPatchWordDTO.getStatusDay2())) {
            word.setStatusDay2(String.valueOf(requestPatchWordDTO.getStatusDay2()));
        }
        if (Objects.nonNull(requestPatchWordDTO.getRepeatDay3())) {
            word.setRepeatDay3(requestPatchWordDTO.getRepeatDay3());
        }
        if (Objects.nonNull(requestPatchWordDTO.getStatusDay3())) {
            word.setStatusDay3(String.valueOf(requestPatchWordDTO.getStatusDay3()));
        }
        if (Objects.nonNull(requestPatchWordDTO.getStory())) {
            word.setStory(requestPatchWordDTO.getStory());
        }
        if (Objects.nonNull(requestPatchWordDTO.getAccountId())) {
            Account account = Optional.of(accountDAO.findById(requestPatchWordDTO.getAccountId())).orElseThrow(() -> new NotFoundException("Account not found")).get();
            word.setAccount(account);
        }

    }

    @Transactional
    public List<ResponseWordDTO> createBulkWords(BulkRequestWordDTO bulkRequest, String accountId) {
        List<ResponseWordDTO> words = new ArrayList<>();
        bulkRequest.getWords().forEach(requestWordDTO -> {
            words.add(this.add(requestWordDTO, accountId));
        });
        return words;
    }
}
