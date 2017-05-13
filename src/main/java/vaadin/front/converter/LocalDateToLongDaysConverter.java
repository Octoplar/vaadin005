package vaadin.front.converter;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Created by Octoplar on 05.05.2017.
 */
public class LocalDateToLongDaysConverter implements Converter<LocalDate, Long> {
    @Override
    public Result<Long> convertToModel(LocalDate localDate, ValueContext valueContext) {
        //null to null
        if (localDate==null)
            return Result.ok(null);
        long result= Duration.between(localDate.atTime(0,0,0), LocalDate.now().atTime(0,0,0)).toDays();
        return Result.ok(result);
    }

    @Override
    public LocalDate convertToPresentation(Long aLong, ValueContext valueContext) {
        //null to null
        if (aLong==null)
            return null;
        return LocalDate.now().minusDays(aLong);
    }


}
