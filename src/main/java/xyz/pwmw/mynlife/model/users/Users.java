package xyz.pwmw.mynlife.model.users;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Users implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userId;

    @Column(columnDefinition = "TEXT", nullable = false, unique = true)
    private String userEmail;
    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private String userNickname;
    @Column(columnDefinition = "TEXT")
    private String userPassword;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
    @Column(columnDefinition = "TEXT")
    private String backgroundImageUrl;


    @Column(columnDefinition = "TEXT")
    private String birthDay;

    @Column(columnDefinition = "TEXT")
    private String gender; // male, female

    @Column(columnDefinition = "VARCHAR(20)")
    private String socialType;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Timestamp lastLogin;


    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL) // (1)
    @JoinColumn(name="hobby_id")
    private Collection<UsersHobby> usersHobbies;



    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    public Users(String userEmail, String userPassword, String userNickname, List<String> roles){
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userNickname = userNickname;
        this.roles = roles;
    }

    public Users(String userEmail, String userPassword, String userNickname, List<String> roles, String socialType){
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userNickname = userNickname;
        this.roles = roles;
        this.socialType = socialType;
    }

    public Users() {

    }

    @Builder
    public Users(String email, String socialType, String userNickname, String gender, String imageUrl, String backgroundImageUrl){
        this.userEmail = email;
        this.socialType = socialType;
        this.userNickname = userNickname;
        this.gender = gender;
        this.imageUrl = imageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
    }

    @Override
    @Transactional
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.userPassword;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
