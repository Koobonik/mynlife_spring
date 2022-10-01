package xyz.pwmw.mynlife;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import xyz.pwmw.mynlife.model.into_hobby_cost.IntoHobbyCost;
import xyz.pwmw.mynlife.model.into_hobby_cost.IntoHobbyCostId;
import xyz.pwmw.mynlife.model.into_hobby_cost.IntoHobbyCostRepository;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
//@DataJpaTest
class MynlifeApplicationTests {
    @Autowired
    IntoHobbyCostRepository intoHobbyCostRepository;

    @Test
    void contextLoads() {
    }
    @Transactional
    @Test
    public void testEmbeddable() {

        IntoHobbyCostId intoHobbyCostId = new IntoHobbyCostId();

        intoHobbyCostId.setHobbyId("1");

        intoHobbyCostId.setUserId("2");

        IntoHobbyCost intoHobbyCost = new IntoHobbyCost();
        intoHobbyCost.setIntoHobbyCostId(intoHobbyCostId);

        intoHobbyCostRepository.save(intoHobbyCost);


        IntoHobbyCostId intoHobbyCostId2 = new IntoHobbyCostId();

        intoHobbyCostId2.setHobbyId("1");

        intoHobbyCostId2.setUserId("2");
        IntoHobbyCost intoHobbyCost2 = new IntoHobbyCost();
        intoHobbyCost2.setIntoHobbyCostId(intoHobbyCostId2);
        intoHobbyCostRepository.save(intoHobbyCost2);

        assertThat(intoHobbyCost.getIntoHobbyCostId()).isEqualTo(intoHobbyCost2.getIntoHobbyCostId());
//        assertThat(findMember).isEqualTo(joinMember);
    }
//        assertThat(intoHobbyCostId.equals(intoHobbyCost.getIntoHobbyCostId()));


}
