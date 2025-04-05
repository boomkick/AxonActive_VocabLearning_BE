package org.acme.service.account;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import org.acme.base.dao.BaseDAO;
import java.util.Optional;

@ApplicationScoped
public class AccountDAO extends BaseDAO<Account> {
    public AccountDAO() {
        super(Account.class);
    }

    public Optional<Account> findByUsername(String username) {
        TypedQuery<Account> query = entityManager.createQuery(
                "SELECT a FROM Account a WHERE a.username =: username", Account.class
        );
        query.setParameter("username", username);

        return query.getResultStream().findFirst();
    }
}
