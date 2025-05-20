package com.senla.resource_server.data.dao.impl;

import com.senla.resource_server.data.dao.CommunityDao;
import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.util.PaginationUtil;
import com.senla.resource_server.util.PaginationUtil.Pagination;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommunityDaoImpl implements CommunityDao {

    private final PaginationUtil paginationUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Community save(Community community) {
        entityManager.persist(community);
        return community;
    }

    @Override
    public Optional<Community> findById(Long id) {
        Community community = entityManager.find(Community.class, id);
        return Optional.ofNullable(community);
    }

    @Override
    public List<Community> findAll(Integer page, Integer size) {
        Pagination pagination = paginationUtil.calculate(page, size);

        String query = "SELECT c FROM Community c ORDER BY c.id";

        return entityManager.createQuery(query, Community.class)
                .setFirstResult((int) pagination.offset())
                .setMaxResults(pagination.limit())
                .getResultList();
    }
}

