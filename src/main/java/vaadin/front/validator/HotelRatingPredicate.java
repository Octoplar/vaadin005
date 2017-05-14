package vaadin.front.validator;

import com.vaadin.server.SerializablePredicate;

/**
 * Created by Octoplar on 14.05.2017.
 */
public class HotelRatingPredicate implements SerializablePredicate<Integer> {
    public  static final String MESSAGE="Rating must be integer between 1..5 inclusive";
    @Override
    public boolean test(Integer i) {
        return i>0&&i<6;
    }
}
