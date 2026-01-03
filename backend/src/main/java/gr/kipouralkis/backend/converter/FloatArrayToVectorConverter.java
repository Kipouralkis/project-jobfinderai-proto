package gr.kipouralkis.backend.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter
public class FloatArrayToVectorConverter implements AttributeConverter<float[], String> {

    @Override
    public String convertToDatabaseColumn(float[] array) {
        if(array==null) return null;
        return Arrays.toString(array);
    }

    @Override
    public float[] convertToEntityAttribute(String dbData) {
        if(dbData==null) return null;

        dbData = dbData.replace("[","").replace("]","");

        if(dbData.isBlank()) return new float[0];

        String[] parts = dbData.split(",");
        float[] array = new float[parts.length];

        for(int i=0;i<parts.length;i++)
            array[i] = Float.parseFloat(parts[i].trim());

        return array;
    }

}
