package com.senla.social_network.data.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "wall_messages")
@PrimaryKeyJoinColumn(name = "message_id")
@DiscriminatorValue("WALL")
public class WallMessage extends Message {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wall_id", nullable = false)
    private Wall wall;

}
