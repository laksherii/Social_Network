package com.senla.resource_server.data.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "group_chat")
public class GroupChat {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Group name cannot be blank")
    @Size(min = 3, max = 100, message = "Group name must be between 3 and 100 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Users set cannot be null")
    @Size(min = 1, message = "Group must have at least one member")
    @ManyToMany
    @JoinTable(
            name = "group_chat_user",
            joinColumns = @JoinColumn(name = "group_chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    @NotNull(message = "Messages list cannot be null")
    @OneToMany(mappedBy = "groupChat", cascade = CascadeType.ALL)
    private List<GroupChatMessage> messages = new ArrayList<>();
}
