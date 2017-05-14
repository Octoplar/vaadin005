package vaadin.front.validator;

import com.vaadin.server.SerializablePredicate;

import java.time.LocalDate;

/**
 * Created by Octoplar on 14.05.2017.
 */
public class HotelOperatesFromPredicate implements SerializablePredicate<LocalDate> {
    public  static final String MESSAGE="Date can not be future";
    @Override
    public boolean test(LocalDate d) {
        return d.compareTo(LocalDate.now())<0;
    }
}
