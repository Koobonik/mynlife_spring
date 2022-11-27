package xyz.pwmw.mynlife.model.users;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class UsersHobbyId implements Serializable {
    private static final long serialVersionUID = -7022358498514909958L;
    @Column(name = "hobby_id")
    private long hobbyId;
    @Column(name = "user_id")
    private long userId;
}
