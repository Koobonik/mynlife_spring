package xyz.pwmw.mynlife.model.into_hobby_cost;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class IntoHobbyCostId implements Serializable {

    @Column(name = "hobby_id")
    private long hobbyId;
    @Column(name = "user_id")
    private long userId;
}
