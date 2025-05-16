package com.senla.resource_server.data.dao;

import com.senla.resource_server.data.entity.Community;
import com.senla.resource_server.data.entity.PublicMessage;

import java.util.List;
import java.util.Optional;

public interface CommunityDao {
    Community save(Community community);

    Optional<Community> findById(Long id);

    List<Community> findAll(Integer page, Integer size);

    PublicMessage sendMessage(PublicMessage publicMessage);
}
