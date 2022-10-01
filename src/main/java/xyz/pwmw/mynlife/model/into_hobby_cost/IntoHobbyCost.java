package xyz.pwmw.mynlife.model.into_hobby_cost;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xyz.pwmw.mynlife.model.hobby.Hobby;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@AllArgsConstructor
// 취미 테이블
public class IntoHobbyCost {
//    @Column(nullable = false)
//    private long hobbyId;
//
//    @Column(nullable = false)
//    private long userId;

    @EmbeddedId
    private IntoHobbyCostId intoHobbyCostId;

//    @ElementCollection
//    @CollectionTable(name = "order_item_mapping",
//            joinColumns = {@JoinColumn(name = "order_id", referencedColumnName = "id")})
//    @MapKeyColumn(name = "item_name")
//    @Column(name = "price")
//    private Map<String, Object> hobbyCostData;



    public IntoHobbyCost(){

    }
}
