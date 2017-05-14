package vaadin.front.form;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import vaadin.MyUI;
import vaadin.back.entity.Hotel;
import vaadin.back.service.HotelCategoryService;
import vaadin.back.service.HotelService;
import vaadin.back.util.SingleContainer;
import vaadin.front.converter.LocalDateToLongDaysConverter;
import vaadin.front.validator.*;

import javax.persistence.OptimisticLockException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static vaadin.back.util.HotelUtils.ValidationErrorsListToString;

/**
 * Created by Octoplar on 14.05.2017.
 */
public class HotelBulkForm extends FormLayout {
    //services
    private HotelService hotelService;
    private HotelCategoryService hotelCategoryService;

    //owner reference
    private MyUI ui;

    private List<Hotel> managedItems;


    //components
    private NativeSelect<String> fieldSelector;
    private Button updateButton = new Button("Update");
    private Button cancelButton = new Button("Cancel");
    private Layout buttons=new HorizontalLayout(updateButton, cancelButton);

    private UpdateFieldState currentState;

    //state mapping
    private Map<String, UpdateFieldState> content;




    public HotelBulkForm(HotelService hotelService, HotelCategoryService hotelCategoryService, MyUI ui) {
        this.hotelService = hotelService;
        this.hotelCategoryService = hotelCategoryService;
        this.ui = ui;

        //buttons config
        updateButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        updateButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        updateButton.addClickListener(e->currentState.onUpdateClick());
        cancelButton.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
        cancelButton.addClickListener(e->currentState.onCancelClick());

        // TODO: 14.05.2017
        //native select
        //current state
        //
        //set content

    }

    public void setManagedItems(Set<Hotel> items){
        //setItems
        managedItems=new ArrayList<>(items.size());
        try{
        for (Hotel h : items) {
            managedItems.add(h.clone());
        }
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        //todo
        //visibility
        //reset nativeSelect
        //show empty content
    }







    private void hideForm(){
        //todo
    }
    //=============================================class members=======================================================

    private interface UpdateFieldState{
        Layout getContent();
        void refreshContent();
        void onUpdateClick();
        void onCancelClick();
    }


    private class UpdateNameFieldState implements UpdateFieldState{

        //binder
        Binder<SingleContainer<String>> binder;

        //entry
        SingleContainer<String> entry;

        //components
        Layout layout;
        TextField field;

        public UpdateNameFieldState() {

            field=new TextField("Name");
            field.setPlaceholder("Enter new name here");
            field.setDescription("Any string up to 255");

            layout=new VerticalLayout(field);

            entry=new SingleContainer<>("");

            binder=new Binder<>();
            binder.forField(field)
                    .asRequired("Name is required")
                    .withValidator(new HotelNamePredicate(), HotelNamePredicate.MESSAGE)
                    .bind(SingleContainer::getValue, SingleContainer::setValue);

            refreshContent();
        }

        @Override
        public Layout getContent() {
            return layout;
        }

        @Override
        public void refreshContent() {
            entry.setValue("");
            binder.setBean(this.entry);
        }

        @Override
        public void onUpdateClick() {
            //validate
            BinderValidationStatus<SingleContainer<String>> validationStatus = binder.validate();

            if (validationStatus.hasErrors()) {
                Notification.show(ValidationErrorsListToString(validationStatus.getValidationErrors()));
                return;
            }
            String newValue=entry.getValue();

            //set new value
            for (Hotel h : managedItems) {
                h.setName(newValue);
            }
            //save
            try{
                hotelService.saveAll(managedItems);
            }
            catch (OptimisticLockException e){
                Notification.show("Data is out of date, changes not saved.");
            }

            //enclosing class method
            hideForm();

        }

        @Override
        public void onCancelClick() {
            //enclosing class method
            hideForm();
        }
    }

    private class UpdateAddressFieldState implements UpdateFieldState{

        //binder
        Binder<SingleContainer<String>> binder;

        //entry
        SingleContainer<String> entry;

        //components
        Layout layout;
        TextField field;

        public UpdateAddressFieldState() {

            field=new TextField("Address");
            field.setPlaceholder("Enter new address here");
            field.setDescription("Any string up to 255");

            layout=new VerticalLayout(field);

            binder=new Binder<>();
            binder.forField(field)
                    .asRequired("Address is required")
                    .withValidator(new HotelAddressPredicate(), HotelAddressPredicate.MESSAGE)
                    .bind(SingleContainer::getValue, SingleContainer::setValue);

            refreshContent();
        }

        @Override
        public Layout getContent() {
            return layout;
        }

        @Override
        public void refreshContent() {
            entry.setValue("");
            binder.setBean(this.entry);
        }

        @Override
        public void onUpdateClick() {
            //validate
            BinderValidationStatus<SingleContainer<String>> validationStatus = binder.validate();

            if (validationStatus.hasErrors()) {
                Notification.show(ValidationErrorsListToString(validationStatus.getValidationErrors()));
                return;
            }
            String newValue=entry.getValue();

            //set new value
            for (Hotel h : managedItems) {
                h.setAddress(newValue);
            }
            //save
            try{
                hotelService.saveAll(managedItems);
            }
            catch (OptimisticLockException e){
                Notification.show("Data is out of date, changes not saved.");
            }

            //enclosing class method
            hideForm();

        }

        @Override
        public void onCancelClick() {
            //enclosing class method
            hideForm();
        }
    }

    private class UpdateUrlFieldState implements UpdateFieldState{

        //binder
        Binder<SingleContainer<String>> binder;

        //entry
        SingleContainer<String> entry;

        //components
        Layout layout;
        TextField field;

        public UpdateUrlFieldState() {

            field=new TextField("URL");
            field.setPlaceholder("Enter new URL here");
            field.setDescription("Any string up to 255");

            layout=new VerticalLayout(field);

            entry=new SingleContainer<>("");

            binder=new Binder<>();
            binder.forField(field)
                    .asRequired("URL is required")
                    .withValidator(new HotelUrlPredicate(), HotelUrlPredicate.MESSAGE)
                    .bind(SingleContainer::getValue, SingleContainer::setValue);

            refreshContent();
        }

        @Override
        public Layout getContent() {
            return layout;
        }

        @Override
        public void refreshContent() {
            entry.setValue("");
            binder.setBean(this.entry);
        }

        @Override
        public void onUpdateClick() {
            //validate
            BinderValidationStatus<SingleContainer<String>> validationStatus = binder.validate();

            if (validationStatus.hasErrors()) {
                Notification.show(ValidationErrorsListToString(validationStatus.getValidationErrors()));
                return;
            }
            String newValue=entry.getValue();

            //set new value
            for (Hotel h : managedItems) {
                h.setUrl(newValue);
            }
            //save
            try{
                hotelService.saveAll(managedItems);
            }
            catch (OptimisticLockException e){
                Notification.show("Data is out of date, changes not saved.");
            }

            //enclosing class method
            hideForm();

        }

        @Override
        public void onCancelClick() {
            //enclosing class method
            hideForm();
        }
    }

    private class UpdateDescriptionFieldState implements UpdateFieldState{

        //binder
        Binder<SingleContainer<String>> binder;

        //entry
        SingleContainer<String> entry;

        //components
        Layout layout;
        TextArea field;

        public UpdateDescriptionFieldState() {

            field=new TextArea("Description");
            field.setPlaceholder("Enter new Description here");
            field.setDescription("Any string up to 65535");

            layout=new VerticalLayout(field);

            entry=new SingleContainer<>("");

            binder=new Binder<>();
            binder.forField(field)
                    .asRequired("Description is required")
                    .withValidator(new HotelDescriptionPredicate(), HotelDescriptionPredicate.MESSAGE)
                    .bind(SingleContainer::getValue, SingleContainer::setValue);

            refreshContent();
        }

        @Override
        public Layout getContent() {
            return layout;
        }

        @Override
        public void refreshContent() {
            entry.setValue("");
            binder.setBean(this.entry);
        }

        @Override
        public void onUpdateClick() {
            //validate
            BinderValidationStatus<SingleContainer<String>> validationStatus = binder.validate();

            if (validationStatus.hasErrors()) {
                Notification.show(ValidationErrorsListToString(validationStatus.getValidationErrors()));
                return;
            }
            String newValue=entry.getValue();

            //set new value
            for (Hotel h : managedItems) {
                h.setDescription(newValue);
            }
            //save
            try{
                hotelService.saveAll(managedItems);
            }
            catch (OptimisticLockException e){
                Notification.show("Data is out of date, changes not saved.");
            }

            //enclosing class method
            hideForm();

        }

        @Override
        public void onCancelClick() {
            //enclosing class method
            hideForm();
        }
    }

    private class UpdateOperatesFromFieldState implements UpdateFieldState{

        //binder
        Binder<SingleContainer<Long>> binder;

        //entry
        SingleContainer<Long> entry;

        //components
        Layout layout;
        DateField field;

        public UpdateOperatesFromFieldState() {

            field=new DateField();
            field.setDescription("Date from Dec 02 BDT 292269055 to now exclusive");

            layout=new VerticalLayout(field);

            entry=new SingleContainer<>(1L);

            binder=new Binder<>();
            binder.forField(field)
                    .asRequired("Date is required")
                    .withValidator(new HotelOperatesFromPredicate(), HotelOperatesFromPredicate.MESSAGE)
                    .withConverter(new LocalDateToLongDaysConverter())
                    .bind(SingleContainer::getValue, SingleContainer::setValue);

            refreshContent();
        }

        @Override
        public Layout getContent() {
            return layout;
        }

        @Override
        public void refreshContent() {
            entry.setValue(1L);
            binder.setBean(this.entry);
        }

        @Override
        public void onUpdateClick() {
            //validate
            BinderValidationStatus<SingleContainer<Long>> validationStatus = binder.validate();

            if (validationStatus.hasErrors()) {
                Notification.show(ValidationErrorsListToString(validationStatus.getValidationErrors()));
                return;
            }
            Long newValue=entry.getValue();

            //set new value
            for (Hotel h : managedItems) {
                h.setOperatesFrom(newValue);
            }
            //save
            try{
                hotelService.saveAll(managedItems);
            }
            catch (OptimisticLockException e){
                Notification.show("Data is out of date, changes not saved.");
            }

            //enclosing class method
            hideForm();

        }

        @Override
        public void onCancelClick() {
            //enclosing class method
            hideForm();
        }
    }

    private class UpdateRatingFieldState implements UpdateFieldState{

        //binder
        Binder<SingleContainer<Integer>> binder;

        //entry
        SingleContainer<Integer> entry;

        //components
        Layout layout;
        TextField field;

        public UpdateRatingFieldState() {

            field=new TextField("Rating");
            field.setPlaceholder("Enter new Rating here");
            field.setDescription("Integer value from 1 to 5 inclusive");

            layout=new VerticalLayout(field);

            entry=new SingleContainer<>(1);

            binder=new Binder<>();
            binder.forField(field)
                    .asRequired("Rating is required")
                    .withConverter(new StringToIntegerConverter(0, "Invalid integer value"))
                    .withValidator(new HotelRatingPredicate(), HotelRatingPredicate.MESSAGE)
                    .bind(SingleContainer::getValue, SingleContainer::setValue);

            refreshContent();
        }

        @Override
        public Layout getContent() {
            return layout;
        }

        @Override
        public void refreshContent() {
            entry.setValue(1);
            binder.setBean(this.entry);
        }

        @Override
        public void onUpdateClick() {
            //validate
            BinderValidationStatus<SingleContainer<Integer>> validationStatus = binder.validate();

            if (validationStatus.hasErrors()) {
                Notification.show(ValidationErrorsListToString(validationStatus.getValidationErrors()));
                return;
            }
            Integer newValue=entry.getValue();

            //set new value
            for (Hotel h : managedItems) {
                h.setRating(newValue);
            }
            //save
            try{
                hotelService.saveAll(managedItems);
            }
            catch (OptimisticLockException e){
                Notification.show("Data is out of date, changes not saved.");
            }

            //enclosing class method
            hideForm();

        }

        @Override
        public void onCancelClick() {
            //enclosing class method
            hideForm();
        }
    }

    private class UpdateCategoryFieldState implements UpdateFieldState{

        //binder
        Binder<SingleContainer<String>> binder;

        //entry
        SingleContainer<String> entry;

        //components
        Layout layout;
        TextField field;

        public UpdateCategoryFieldState() {

            field=new TextField("Name");
            field.setPlaceholder("Enter new name here");
            field.setDescription("Any string up to 255");

            layout=new VerticalLayout(field);

            entry=new SingleContainer<>("");

            binder=new Binder<>();
            binder.forField(field)
                    .asRequired("Name is required")
                    .withValidator(new HotelNamePredicate(), HotelNamePredicate.MESSAGE)
                    .bind(SingleContainer::getValue, SingleContainer::setValue);

            refreshContent();
        }

        @Override
        public Layout getContent() {
            return layout;
        }

        @Override
        public void refreshContent() {
            entry.setValue("");
            binder.setBean(this.entry);
        }

        @Override
        public void onUpdateClick() {
            //validate
            BinderValidationStatus<SingleContainer<String>> validationStatus = binder.validate();

            if (validationStatus.hasErrors()) {
                Notification.show(ValidationErrorsListToString(validationStatus.getValidationErrors()));
                return;
            }
            String newValue=entry.getValue();

            //set new value
            for (Hotel h : managedItems) {
                h.setName(newValue);
            }
            //save
            try{
                hotelService.saveAll(managedItems);
            }
            catch (OptimisticLockException e){
                Notification.show("Data is out of date, changes not saved.");
            }

            //enclosing class method
            hideForm();

        }

        @Override
        public void onCancelClick() {
            //enclosing class method
            hideForm();
        }
    }




}
