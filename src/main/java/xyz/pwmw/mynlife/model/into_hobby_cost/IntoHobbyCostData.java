package xyz.pwmw.mynlife.model.into_hobby_cost;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Converter;
import javax.persistence.Entity;
import java.util.HashMap;

//@Entity
@Getter
@Setter
//@AllArgsConstructor
// 취미 테이블
//@Converter
public class IntoHobbyCostData {
    private HashMap<String, Integer> data = new HashMap<String, Integer>();



    public IntoHobbyCostData(){

    }
}
