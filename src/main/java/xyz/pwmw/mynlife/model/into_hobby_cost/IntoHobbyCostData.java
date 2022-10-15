package xyz.pwmw.mynlife.model.into_hobby_cost;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

//@Entity
@Getter
@Setter
//@AllArgsConstructor
// 취미 테이블
//@Converter
//@JsonIgnoreProperties(ignoreUnknown = true)
public class IntoHobbyCostData {
    private HashMap<String, Integer> data = new HashMap<String, Integer>();



    public IntoHobbyCostData(){

    }
}
