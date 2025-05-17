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

    public Optional<Account> findByEmail(String email) {
        TypedQuery<Account> query = entityManager.createQuery(
                "SELECT a FROM Account a WHERE a.email =: email", Account.class
        );
        query.setParameter("email", email);

        return query.getResultStream().findFirst();
    }
}
