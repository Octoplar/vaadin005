package vaadin.front.form;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import vaadin.back.entity.Hotel;
import vaadin.back.entity.HotelCategory;
import vaadin.back.service.HotelCategoryService;
import vaadin.back.service.HotelService;
import vaadin.front.converter.LocalDateToLongDaysConverter;
import vaadin.front.validator.*;
import vaadin.front.view.HotelView;
import vaadin.util.SingleContainer;

import javax.persistence.OptimisticLockException;
import java.util.*;

import static vaadin.util.HotelUtils.validationErrorsListToString;

/**
 * Created by Octoplar on 14.05.2017.
 */

public class HotelBulkForm extends FormLayout {
    //services
    private HotelService hotelService;
    private HotelCategoryService hotelCategoryService;

    //owner reference
    private HotelView ui;

    private List<Hotel> managedItems;


    //components
    private NativeSelect<String> fieldSelector;
    private Button updateButton = new Button("Update");
    private Button cancelButton = new Button("Cancel");
    private Layout buttons=new HorizontalLayout(updateButton, cancelButton);

    private UpdateFieldState currentState;

    //state mapping
    private Map<String, UpdateFieldState> contentMap;

    //field names

    private static String NAME="Name";
    private static String ADDRESS="Address";
    private static String RATING="Rating";
    private static String URL="Url";
    private static String OPERATES_FROM="Operates from";
    private static String DESCRIPTION="Description";
    private static String CATEGORY="Category";





    public HotelBulkForm(HotelService hotelService, HotelCategoryService hotelCategoryService, HotelView ui) {
        this.hotelService = hotelService;
        this.hotelCategoryService = hotelCategoryService;
        this.ui = ui;

        //size/visibility
//        this.setHeight(500f, Unit.PIXELS);
//        this.setWidth(500f, Unit.PIXELS);
        this.setSizeUndefined();
        this.setVisible(true);

        //buttons config
        updateButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        updateButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        updateButton.addClickListener(e->onUpdateClick());
        cancelButton.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
        cancelButton.addClickListener(e->onCancelClick());

        //mapping
        contentMap=new HashMap<>();
        contentMap.put(null, new NonSelectedState());
        contentMap.put(NAME, new UpdateNameFieldState());
        contentMap.put(ADDRESS, new UpdateAddressFieldState());
        contentMap.put(RATING, new UpdateRatingFieldState());
        contentMap.put(URL, new UpdateUrlFieldState());
        contentMap.put(OPERATES_FROM, new UpdateOperatesFromFieldState());
        contentMap.put(DESCRIPTION, new UpdateDescriptionFieldState());
        contentMap.put(CATEGORY, new UpdateCategoryFieldState());

        //native select
        fieldSelector=new NativeSelect<>();
        fieldSelector.setItems(Arrays.asList(NAME, ADDRESS, RATING, URL, OPERATES_FROM, DESCRIPTION, CATEGORY));
        fieldSelector.setSelectedItem(null);
        fieldSelector.addValueChangeListener(e->fieldSelectorOnValueChange());
        //current state

        Optional o=fieldSelector.getSelectedItem();
        currentState=contentMap.get(o.isPresent()?o.get():null);
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

        fieldSelector.setSelectedItem(null);
        Optional o=fieldSelector.getSelectedItem();
        currentState=contentMap.get(o.isPresent()?o.get():null);
        repaint();
    }

    //============================================privates=============================================================


    private void repaint(){
        //reset new content
        currentState.refreshContent();

        //reassemble
        this.removeAllComponents();
        this.addComponents(fieldSelector, currentState.getContent(), buttons);

    }
    private void fieldSelectorOnValueChange(){
        //switch state

        Optional o=fieldSelector.getSelectedItem();
        currentState=contentMap.get(o.isPresent()?o.get():null);
        repaint();
    }

    private void onUpdateClick(){
        currentState.onUpdateClick();
    }
    private void onCancelClick(){
        currentState.onCancelClick();
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
        SingleContainer<String> entry=new SingleContainer<>();

        //components
        Layout layout;
        TextField field;

        UpdateNameFieldState() {

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

            entry.setValue("");
            binder.setBean(this.entry);
        }

        @Override
        public Layout getContent() {
            return layout;
        }

        @Override
        public void refreshContent() {
            updateButton.setVisible(true);
            entry.setValue("");
            binder.setBean(this.entry);
        }

        @Override
        public void onUpdateClick() {
            //validate
            BinderValidationStatus<SingleContainer<String>> validationStatus = binder.validate();

            if (validationStatus.hasErrors()) {
                Notification.show(validationErrorsListToString(validationStatus.getValidationErrors()));
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
            ui.refresh();
            ui.hidePopup();

        }

        @Override
        public void onCancelClick() {
            ui.hidePopup();
        }
    }

    private class UpdateAddressFieldState implements UpdateFieldState{

        //binder
        Binder<SingleContainer<String>> binder;

        //entry
        SingleContainer<String> entry=new SingleContainer<>();

        //components
        Layout layout;
        TextField field;

        UpdateAddressFieldState() {

            field=new TextField("Address");
            field.setPlaceholder("Enter new address here");
            field.setDescription("Any string up to 255");

            layout=new VerticalLayout(field);

            binder=new Binder<>();
            binder.forField(field)
                    .asRequired("Address is required")
                    .withValidator(new HotelAddressPredicate(), HotelAddressPredicate.MESSAGE)
                    .bind(SingleContainer::getValue, SingleContainer::setValue);

            entry.setValue("");
            binder.setBean(this.entry);
        }

        @Override
        public Layout getContent() {
            return layout;
        }

        @Override
        public void refreshContent() {
            updateButton.setVisible(true);
            entry.setValue("");
            binder.setBean(this.entry);
        }

        @Override
        public void onUpdateClick() {
            //validate
            BinderValidationStatus<SingleContainer<String>> validationStatus = binder.validate();

            if (validationStatus.hasErrors()) {
                Notification.show(validationErrorsListToString(validationStatus.getValidationErrors()));
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
            ui.refresh();
            ui.hidePopup();
        }

        @Override
        public void onCancelClick() {
            ui.hidePopup();
        }
    }

    private class UpdateUrlFieldState implements UpdateFieldState{

        //binder
        Binder<SingleContainer<String>> binder;

        //entry
        SingleContainer<String> entry=new SingleContainer<>();

        //components
        Layout layout;
        TextField field;

        UpdateUrlFieldState() {

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

            entry.setValue("");
            binder.setBean(this.entry);
        }

        @Override
        public Layout getContent() {
            return layout;
        }

        @Override
        public void refreshContent() {
            updateButton.setVisible(true);
            entry.setValue("");
            binder.setBean(this.entry);
        }

        @Override
        public void onUpdateClick() {
            //validate
            BinderValidationStatus<SingleContainer<String>> validationStatus = binder.validate();

            if (validationStatus.hasErrors()) {
                Notification.show(validationErrorsListToString(validationStatus.getValidationErrors()));
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
            ui.refresh();
            ui.hidePopup();
        }

        @Override
        public void onCancelClick() {
            ui.hidePopup();
        }
    }

    private class UpdateDescriptionFieldState implements UpdateFieldState{

        //binder
        Binder<SingleContainer<String>> binder;

        //entry
        SingleContainer<String> entry=new SingleContainer<>();

        //components
        Layout layout;
        TextArea field;

        UpdateDescriptionFieldState() {

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

            entry.setValue("");
            binder.setBean(this.entry);
        }

        @Override
        public Layout getContent() {
            return layout;
        }

        @Override
        public void refreshContent() {
            updateButton.setVisible(true);
            entry.setValue("");
            binder.setBean(this.entry);
        }

        @Override
        public void onUpdateClick() {
            //validate
            BinderValidationStatus<SingleContainer<String>> validationStatus = binder.validate();

            if (validationStatus.hasErrors()) {
                Notification.show(validationErrorsListToString(validationStatus.getValidationErrors()));
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
            ui.refresh();
            ui.hidePopup();
        }

        @Override
        public void onCancelClick() {
            ui.hidePopup();
        }
    }

    private class UpdateOperatesFromFieldState implements UpdateFieldState{

        //binder
        Binder<SingleContainer<Long>> binder;

        //entry
        SingleContainer<Long> entry=new SingleContainer<>();

        //components
        Layout layout;
        DateField field;

        UpdateOperatesFromFieldState() {

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

            entry.setValue(1L);
            binder.setBean(this.entry);
        }

        @Override
        public Layout getContent() {
            return layout;
        }

        @Override
        public void refreshContent() {
            updateButton.setVisible(true);
            entry.setValue(1L);
            binder.setBean(this.entry);
        }

        @Override
        public void onUpdateClick() {
            //validate
            BinderValidationStatus<SingleContainer<Long>> validationStatus = binder.validate();

            if (validationStatus.hasErrors()) {
                Notification.show(validationErrorsListToString(validationStatus.getValidationErrors()));
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
            ui.refresh();
            ui.hidePopup();
        }

        @Override
        public void onCancelClick() {
            ui.hidePopup();
        }
    }

    private class UpdateRatingFieldState implements UpdateFieldState{

        //binder
        Binder<SingleContainer<Integer>> binder;

        //entry
        SingleContainer<Integer> entry=new SingleContainer<>();

        //components
        Layout layout;
        TextField field;

        UpdateRatingFieldState() {

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

            entry.setValue(1);
            binder.setBean(this.entry);
        }

        @Override
        public Layout getContent() {
            return layout;
        }

        @Override
        public void refreshContent() {
            updateButton.setVisible(true);
            entry.setValue(1);
            binder.setBean(this.entry);
        }

        @Override
        public void onUpdateClick() {
            //validate
            BinderValidationStatus<SingleContainer<Integer>> validationStatus = binder.validate();

            if (validationStatus.hasErrors()) {
                Notification.show(validationErrorsListToString(validationStatus.getValidationErrors()));
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
            ui.refresh();
            ui.hidePopup();
        }

        @Override
        public void onCancelClick() {
            ui.hidePopup();
        }
    }

    private class UpdateCategoryFieldState implements UpdateFieldState{

        //binder
        Binder<SingleContainer<HotelCategory>> binder;

        //entry
        SingleContainer<HotelCategory> entry=new SingleContainer<>();

        //components
        Layout layout;
        private ComboBox<HotelCategory> field;

        UpdateCategoryFieldState() {

            field=new ComboBox<>("Category");
            field.setDescription("Category from drop list");
            field.setItemCaptionGenerator(
                    (ItemCaptionGenerator<HotelCategory>) (c) -> c==null||c.getName()==null?"":c.getName());

            layout=new VerticalLayout(field);

            entry=new SingleContainer<>(new HotelCategory(""));

            binder=new Binder<>();
            binder.forField(field)
                    .asRequired("Category is required")
                    .bind(SingleContainer::getValue, SingleContainer::setValue);

            entry.setValue(new HotelCategory(""));
            binder.setBean(this.entry);
            refreshCategories();
        }

        @Override
        public Layout getContent() {
            return layout;
        }

        @Override
        public void refreshContent() {
            updateButton.setVisible(true);
            entry.setValue(new HotelCategory(""));
            binder.setBean(this.entry);
            refreshCategories();
        }

        @Override
        public void onUpdateClick() {
            //validate
            BinderValidationStatus<SingleContainer<HotelCategory>> validationStatus = binder.validate();

            if (validationStatus.hasErrors()) {
                Notification.show(validationErrorsListToString(validationStatus.getValidationErrors()));
                return;
            }
            HotelCategory newValue=entry.getValue();

            //set new value
            for (Hotel h : managedItems) {
                h.setCategory(newValue);
            }
            //save
            try{
                hotelService.saveAll(managedItems);
            }
            catch (OptimisticLockException e){
                Notification.show("Data is out of date, changes not saved.");
            }
            ui.refresh();
            ui.hidePopup();
        }

        @Override
        public void onCancelClick() {
            ui.hidePopup();
        }

        private void refreshCategories(){
            //clear caption
            field.setSelectedItem(new HotelCategory(""));
            //refresh content
            field.setItems(hotelCategoryService.findAll());
        }
    }

    private class NonSelectedState implements UpdateFieldState{
        //components
        Layout layout;
        Label label;

        NonSelectedState() {
            label=new Label("Select field to manage");
            layout=new VerticalLayout(label);
        }

        @Override
        public Layout getContent() {
            return layout;
        }

        @Override
        public void refreshContent() {
            updateButton.setVisible(false);
        }

        @Override
        public void onUpdateClick() {
            ui.hidePopup();
        }

        @Override
        public void onCancelClick() {
            ui.hidePopup();
        }
    }




}
