package org.acme.service.auth;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.base.dao.BaseDAO;

@ApplicationScoped
public class RefreshTokenDAO extends BaseDAO<RefreshToken> {
    public RefreshTokenDAO() {
        super(RefreshToken.class);
    }
}
