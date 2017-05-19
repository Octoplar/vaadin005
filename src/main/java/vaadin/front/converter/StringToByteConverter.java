package vaadin.front.converter;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

/**
 * Created by Octoplar on 19.05.2017.
 */
public class StringToByteConverter implements Converter<String, Byte> {
    @Override
    public Result<Byte> convertToModel(String s, ValueContext valueContext) {
        //null to null
        if (s==null)
            return Result.ok(null);
        byte b;
        try{
           b=Byte.parseByte(s);
        }
        catch (Exception e){
            return Result.error(e.getMessage());
        }
        return Result.ok(b);

    }

    @Override
    public String convertToPresentation(Byte aByte, ValueContext valueContext) {
        //null to null
        if (aByte==null)
            return null;
        return aByte.toString();
    }
}
