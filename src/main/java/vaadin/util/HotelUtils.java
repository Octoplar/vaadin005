package vaadin.util;

import com.vaadin.data.ValidationResult;

import java.util.List;

/**
 * Created by Octoplar on 14.05.2017.
 */
public class HotelUtils {
    public static String validationErrorsListToString(List<ValidationResult> results){
        StringBuilder sb=new StringBuilder();
        for (ValidationResult result : results) {
            sb.append(result.getErrorMessage());
            sb.append("\r\n");
        }
        return sb.toString();
    }
}
