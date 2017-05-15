package vaadin.front.form;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import vaadin.back.entity.HotelCategory;
import vaadin.back.service.HotelCategoryService;
import vaadin.front.view.CategoryView;

import javax.persistence.OptimisticLockException;

import static vaadin.util.HotelUtils.validationErrorsListToString;

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
    private final CategoryView ui;

    //entity to display
    private HotelCategory hotelCategory;


    //binder
    private Binder<HotelCategory> hotelCategoryBinder;




    public HotelCategoryForm(CategoryView ui, HotelCategoryService hotelCategoryService) {
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
            Notification.show(validationErrorsListToString(validationStatus.getValidationErrors()));
            return;
        }

        //save
        try{
            hotelCategoryService.save(this.hotelCategory);
        }
        catch (OptimisticLockException e){
            Notification.show("This category data is out of date, changes not saved.");
        }
        //update owner content
        ui.refresh();
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
        //update owner content
        ui.refresh();
        //hide
        setVisible(false);
    }




}
