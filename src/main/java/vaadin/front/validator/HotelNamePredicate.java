package vaadin.front.validator;

import com.vaadin.server.SerializablePredicate;

/**
 * Created by Octoplar on 14.05.2017.
 */
public class HotelNamePredicate implements SerializablePredicate<String> {
    public  static final String MESSAGE="Maximum name length is 255";

    @Override
    public boolean test(String s) {
        return s!=null&&s.length()<=255;
    }
}
