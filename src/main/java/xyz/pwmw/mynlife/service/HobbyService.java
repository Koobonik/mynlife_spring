package xyz.pwmw.mynlife.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import xyz.pwmw.mynlife.model.hobby.Hobby;
import xyz.pwmw.mynlife.model.hobby.HobbyRepository;
import xyz.pwmw.mynlife.util.yml.ApplicationNaverSENS;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@Component
public class HobbyService {

    private final ApplicationNaverSENS applicationNaverSENS;
    private final HobbyRepository hobbyRepository;

    public List<Hobby> findAll(){
        return hobbyRepository.findAll();
    }
}
