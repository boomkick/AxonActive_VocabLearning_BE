package org.acme.base.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.acme.base.entity.BaseEntity;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseDAO<T extends BaseEntity> {
    @PersistenceContext
    protected EntityManager entityManager;
    protected final Class<T> entityClass;

    public List<T> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<T> q = cb.createQuery(entityClass);
        Root<T> c = q.from(entityClass);

        return entityManager.createQuery(q).getResultList();
    }

    public T add(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    public T update(T entity) {
        entityManager.merge(entity);
        return entity;
    }

    public Optional<T> findById(String id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    public void delete(String id) {
        T entity = this.findById(id).get();
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }
}
