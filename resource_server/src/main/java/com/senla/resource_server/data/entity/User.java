package com.senla.resource_server.data.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email must not be blank")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "First name must not be blank")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull(message = "Birth day must not be null")
    @Column(name = "birth_day", nullable = false)
    private LocalDate birthDay;

    @Column(nullable = false)
    private boolean enabled;

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private Wall wall;

    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_community",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "community_id")
    )
    private Set<Community> communities = new HashSet<>();

    @OneToMany(mappedBy = "sender")
    private Set<FriendRequest> senderRequest = new HashSet<>();

    @OneToMany(mappedBy = "recipient")
    private Set<FriendRequest> recipientRequest = new HashSet<>();

    @OneToMany(mappedBy = "sender")
    private Set<PrivateMessage> sentPrivateMessages = new HashSet<>();

    @OneToMany(mappedBy = "recipient")
    private Set<PrivateMessage> recipientPrivateMessages = new HashSet<>();

    @NotNull(message = "Gender must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenderType gender;

    @NotNull(message = "Role must not be null")
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


