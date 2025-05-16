package com.senla.resource_server.data.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private String email;

    @EqualsAndHashCode.Include
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @EqualsAndHashCode.Include
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @EqualsAndHashCode.Include
    @Column(name = "birth_day", nullable = false)
    private LocalDate birthDay;

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private boolean enabled;

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Wall wall;

    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    @Builder.Default
    @ToString.Exclude
    private Set<User> friends = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_community",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "community_id")
    )
    @ToString.Exclude
    @Builder.Default
    private Set<Community> communities = new HashSet<>();

    @OneToMany(mappedBy = "sender")
    @Builder.Default
    @ToString.Exclude
    private Set<FriendRequest> senderRequest = new HashSet<>();

    @OneToMany(mappedBy = "recipient")
    @Builder.Default
    @ToString.Exclude
    private Set<FriendRequest> recipientRequest = new HashSet<>();

    @OneToMany(mappedBy = "sender")
    @Builder.Default
    @ToString.Exclude
    private Set<PrivateMessage> sentPrivateMessages = new HashSet<>();

    @OneToMany(mappedBy = "recipient")
    @Builder.Default
    @ToString.Exclude
    private Set<PrivateMessage> recipientPrivateMessages = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenderType gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    public enum GenderType {
        MALE,
        FEMALE
    }

    public enum RoleType {
        ROLE_USER,
        ROLE_ADMIN,
        ROLE_MODERATOR
    }
}


