package vaadin.front.view;

import com.vaadin.data.HasValue;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import vaadin.back.entity.HotelCategory;
import vaadin.back.service.HotelCategoryService;
import vaadin.front.form.HotelCategoryForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Octoplar on 16.05.2017.
 */
@org.springframework.stereotype.Component
@UIScope
public class CategoryView extends VerticalLayout implements View {
    //Field_ID=================
    public static final String B_ADD_CATEGORY="B_ADD_CATEGORY";
    public static final String B_DELETE_SELECTED_CATEGORIES="B_DELETE_SELECTED_CATEGORIES";


    //for navigator
    public static final String NAME="CategoryView";

    //service
    public final HotelCategoryService hotelCategoryService;

    //menuItem 2 components===============================================
    private Grid<HotelCategory> hotelCategoryGrid;
    private HotelCategoryForm hotelCategoryForm;
    private Button createNewHotelCategoryButton;
    private Button deleteSelectedHotelCategoryButton;


    @Autowired
    public CategoryView(HotelCategoryService hotelCategoryService) {
        this.hotelCategoryService = hotelCategoryService;
        //init and add components
        this.addComponents(configureHotelCategoryPageLayout());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        refresh();
    }


    //set up content configuration to default
    public void refresh(){
        hotelCategoryGrid.setItems(hotelCategoryService.findAll());
        //hide form
        hotelCategoryForm.setVisible(false);
    }
    //=====================================privates===================================================================


    //==============build and configure components
    private Layout configureHotelCategoryPageLayout(){
        //construct
        hotelCategoryGrid=new Grid<>();
        hotelCategoryForm=new HotelCategoryForm(this, hotelCategoryService);
        createNewHotelCategoryButton=new Button();
        deleteSelectedHotelCategoryButton =new Button();

        //grid config
        //content
        hotelCategoryGrid.setItems(hotelCategoryService.findAll());
        //columns
        hotelCategoryGrid.addColumn(HotelCategory::getName).setCaption("Category name");
        //selection
        hotelCategoryGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        hotelCategoryGrid.asMultiSelect().addValueChangeListener(this::hotelCategoryGridOnValueChange);


        //form config
        hotelCategoryForm.setVisible(false);

        //toolbar config
        createNewHotelCategoryButton.setCaption("Create new hotel category");
        createNewHotelCategoryButton.addClickListener(e->createNewHotelCategoryButtonClick());
        createNewHotelCategoryButton.setId(B_ADD_CATEGORY);

        deleteSelectedHotelCategoryButton.setCaption("Delete selected");
        deleteSelectedHotelCategoryButton.addClickListener(e-> deleteSelectedHotelCategoryButtonClick());
        deleteSelectedHotelCategoryButton.setVisible(false);
        deleteSelectedHotelCategoryButton.setId(B_DELETE_SELECTED_CATEGORIES);


        //layouts===========
        HorizontalLayout gridAndFormLayout=new HorizontalLayout(hotelCategoryGrid, hotelCategoryForm);
        HorizontalLayout toolbar=new HorizontalLayout(createNewHotelCategoryButton, deleteSelectedHotelCategoryButton);
        Layout result=new VerticalLayout(toolbar, gridAndFormLayout);

        //resize=============
        hotelCategoryForm.setSizeUndefined();
        hotelCategoryGrid.setSizeFull();
        gridAndFormLayout.setSizeFull();
        gridAndFormLayout.setExpandRatio(hotelCategoryGrid, 1);

        return  result;
    }

    //==============listeners
    private void createNewHotelCategoryButtonClick(){
        //clear selection
        hotelCategoryGrid.asMultiSelect().clear();
        //new instance for manage
        hotelCategoryForm.setHotelCategory(new HotelCategory(""));

        hotelCategoryForm.setVisible(true);
    }

    private void deleteSelectedHotelCategoryButtonClick() {
        List<HotelCategory> markToDelete=new ArrayList<>(hotelCategoryGrid.getSelectedItems());
        //clear selection
        hotelCategoryGrid.asMultiSelect().clear();
        //hide button
        deleteSelectedHotelCategoryButton.setVisible(false);
        //delete
        hotelCategoryService.deleteAll(markToDelete);
        //refresh
        refresh();



    }

    //hotel category grid value change listener
    private void hotelCategoryGridOnValueChange(HasValue.ValueChangeEvent<Set<HotelCategory>> e){
        List<HotelCategory> values=new ArrayList<>(e.getValue());
        //selected one
        if (values.size()==1){
            //transfer value to form
            hotelCategoryForm.setHotelCategory(values.get(0));
            hotelCategoryForm.setVisible(true);
            deleteSelectedHotelCategoryButton.setVisible(true);
            return;
        }
        //selected none
        if (values.size()==0){
            hotelCategoryForm.setVisible(false);
            deleteSelectedHotelCategoryButton.setVisible(false);
            return;
        }
        //selected multi
        else {
            hotelCategoryForm.setVisible(false);
            deleteSelectedHotelCategoryButton.setVisible(true);
        }

    }
}
