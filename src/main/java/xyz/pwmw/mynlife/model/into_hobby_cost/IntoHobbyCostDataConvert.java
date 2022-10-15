package xyz.pwmw.mynlife.model.into_hobby_cost;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashMap;

@Converter(autoApply = true)
//@RequiredArgsConstructor
public class IntoHobbyCostDataConvert implements AttributeConverter<IntoHobbyCostData, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(IntoHobbyCostData attribute) {
        //Information 객체 -> Json 문자열로 변환
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            System.out.println("으아아아아" + e);
            return null;
        }
    }

    @Override
    public IntoHobbyCostData convertToEntityAttribute(String jsonString) {
        //Json 문자열 Information 객체로 변환
        try {
            IntoHobbyCostData intoHobbyCostData = new IntoHobbyCostData();
            intoHobbyCostData.setData(objectMapper.readValue(jsonString, HashMap.class));
            System.out.println(intoHobbyCostData.getData());
//            objectMapper.readValue(jsonString, HashMap.class);
            return intoHobbyCostData;
        } catch (Exception e) {
            System.out.println(jsonString);
            System.out.println("여기서어어어" + e);
            return null;
        }
    }
}