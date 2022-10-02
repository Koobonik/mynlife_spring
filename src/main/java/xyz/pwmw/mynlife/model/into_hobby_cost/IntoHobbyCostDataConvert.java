package xyz.pwmw.mynlife.model.into_hobby_cost;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
@RequiredArgsConstructor
public class IntoHobbyCostDataConvert implements AttributeConverter<IntoHobbyCostData, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(IntoHobbyCostData attribute) {
        //Information 객체 -> Json 문자열로 변환
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public IntoHobbyCostData convertToEntityAttribute(String jsonString) {
        //Json 문자열 Information 객체로 변환
        try {
            return objectMapper.readValue(jsonString, IntoHobbyCostData.class);
        } catch (Exception e) {
            return null;
        }
    }
}