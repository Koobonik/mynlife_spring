package xyz.pwmw.mynlife.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import xyz.pwmw.mynlife.model.hobby.Hobby;
import xyz.pwmw.mynlife.model.hobby.HobbyRepository;
import xyz.pwmw.mynlife.model.into_hobby_cost.IntoHobbyCost;
import xyz.pwmw.mynlife.model.into_hobby_cost.IntoHobbyCostRepository;
import xyz.pwmw.mynlife.util.yml.ApplicationNaverSENS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@Component
public class HobbyService {

    private final ApplicationNaverSENS applicationNaverSENS;
    private final HobbyRepository hobbyRepository;
    private final IntoHobbyCostRepository intoHobbyCostRepository;

    public List<Hobby> findAll(){
        return hobbyRepository.findAll();
    }

    public List<HashMap> getIntoHobbyCostData(long hobbyId){
        List<IntoHobbyCost> intoHobbyCosts = intoHobbyCostRepository.findAllByHobbyId(hobbyId);
        List<HashMap> hashMapList = new ArrayList<>();
        intoHobbyCosts.forEach((element) ->{
            hashMapList.add(element.getIntoHobbyCostData().getData());
        });
        return hashMapList;

    }
}
