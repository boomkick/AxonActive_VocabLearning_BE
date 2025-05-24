package org.acme.service.word;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import org.acme.base.dao.BaseDAO;

import java.util.List;

@ApplicationScoped
public class WordDAO extends BaseDAO<Word> {
    public WordDAO() {
        super(Word.class);
    }

    public List<Word> findAllByAccountId(String accountId) {
        TypedQuery<Word> query = entityManager.createQuery("SELECT w FROM Word w WHERE w.account.id = :accountId", Word.class);
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }

    public List<Word> findWordsRepeatTodayByAccountId(String accountId) {
        TypedQuery<Word> query = entityManager.createQuery(
                "SELECT w FROM Word w WHERE " +
                        "(w.studiedDay = CURRENT_DATE OR " +
                        "w.repeatDay1 = CURRENT_DATE OR " +
                        "w.repeatDay2 = CURRENT_DATE OR " +
                        "w.repeatDay3 = CURRENT_DATE) AND " +
                        "w.account.id = :accountId",
                Word.class
        );
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }
}
