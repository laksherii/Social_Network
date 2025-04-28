package com.senla.resource_server.data.dao.impl;

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
public class CommunityDaoImpl {

    private final PaginationUtil paginationUtil;
    @PersistenceContext
    private EntityManager entityManager;

    public Community save(Community community) {
        log.info("Saving community with ID {}", community.getId());
        entityManager.persist(community);
        log.info("Successfully saved community with ID {}", community.getId());
        return community;
    }

    public Optional<Community> findById(Long id) {
        log.info("Finding community with ID {}", id);
        Community community = entityManager.find(Community.class, id);
        log.info("Successfully found community with ID {}", id);
        return Optional.ofNullable(community);
    }

    public List<Community> findAll(Integer page, Integer size) {
        log.info("Fetching all communities with pagination, page: {}, size: {}", page, size);
        Pagination pagination = paginationUtil.calculate(page, size);

        String query = "SELECT c FROM Community c ORDER BY c.id";
        List<Community> communities = entityManager.createQuery(query, Community.class)
                .setFirstResult((int) pagination.offset())
                .setMaxResults(pagination.limit())
                .getResultList();

        log.info("Fetched {} communities", communities.size());
        return communities;
    }
}

