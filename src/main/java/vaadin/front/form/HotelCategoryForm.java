package vaadin.front.form;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.ValidationResult;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import vaadin.MyUI;
import vaadin.back.entity.HotelCategory;
import vaadin.back.service.HotelCategoryService;

import javax.persistence.OptimisticLockException;
import java.util.List;

/**
 * Created by Octoplar on 07.05.2017.
 */
public class HotelCategoryForm extends FormLayout {
    //service
    HotelCategoryService hotelCategoryService;

    //components
    private TextField hotelCategoryField;
    private Button saveButton;
    private Button deleteButton;


    //owner
    private final MyUI ui;

    //entity to display
    private HotelCategory hotelCategory;


    //binder
    private Binder<HotelCategory> hotelCategoryBinder;




    public HotelCategoryForm(MyUI ui, HotelCategoryService hotelCategoryService) {
        this.ui = ui;
        this.hotelCategoryService=hotelCategoryService;

        hotelCategoryField=new TextField("Hotel category");
        saveButton=new Button("Save");
        deleteButton=new Button("Delete");
        //components add
        Layout buttons=new HorizontalLayout(saveButton, deleteButton);
        this.addComponents(hotelCategoryField, buttons);

        //buttons
        saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        saveButton.addClickListener(e->saveButtonClick());
        deleteButton.setStyleName(ValoTheme.BUTTON_DANGER);
        deleteButton.setClickShortcut(ShortcutAction.KeyCode.DELETE);
        deleteButton.addClickListener(e->deleteButtonClick());



        //binder
        hotelCategoryBinder=new Binder<>();
        hotelCategoryBinder.forField(hotelCategoryField)
                .asRequired("Category name is required")
                .withValidator(s->(s!=null&&s.length()<=255), "Maximum name length is 255")
                .bind(HotelCategory::getName, HotelCategory::setName);

        //toolTips
        hotelCategoryField.setDescription("Any string up to 255");


    }

    public HotelCategory getHotelCategory() {
        return hotelCategory;
    }

    public void setHotelCategory(HotelCategory hotelCategory) {

        if (hotelCategory==null)
            this.hotelCategory=new HotelCategory("");
        else
            this.hotelCategory=hotelCategory;
        //binder
        hotelCategoryBinder.setBean(this.hotelCategory);
        //delete button visibility
        deleteButton.setVisible(this.hotelCategory.isPersisted());
    }




    //========================privates==================================================================================



    private void saveButtonClick(){
        //validate
        BinderValidationStatus<HotelCategory> validationStatus = hotelCategoryBinder.validate();

        if (validationStatus.hasErrors()) {
            Notification.show(ValidationErrorsListToString(validationStatus.getValidationErrors()));
            return;
        }

        //save
        try{
            hotelCategoryService.save(this.hotelCategory);
        }
        catch (OptimisticLockException e){
            Notification.show("This category data is out of date, changes not saved.");
        }
        //update
        ui.refreshHotelCategoryGridContent();
        //hide
        setVisible(false);
    }
    private void deleteButtonClick(){
        //delete
        try{
            hotelCategoryService.delete(this.hotelCategory);
        }
        catch (OptimisticLockException e){
            Notification.show("This category data is out of date, changes not saved.");
        }
        //update
        ui.refreshHotelCategoryGridContent();
        //hide
        setVisible(false);
    }

    //readable errors notification
    private String ValidationErrorsListToString(List<ValidationResult> results){
        StringBuilder sb=new StringBuilder();
        for (ValidationResult result : results) {
            sb.append(result.getErrorMessage());
            sb.append("\r\n");
        }
        return sb.toString();
    }




}
