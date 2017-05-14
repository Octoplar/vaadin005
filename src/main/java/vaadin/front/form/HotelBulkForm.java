package vaadin.front.form;

import com.vaadin.ui.*;
import vaadin.MyUI;
import vaadin.back.entity.Hotel;
import vaadin.back.service.HotelCategoryService;
import vaadin.back.service.HotelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private NativeSelect<String> fieldSelector;
    private Map<String, Layout> content;




    public HotelBulkForm(HotelService hotelService, HotelCategoryService hotelCategoryService, MyUI ui) {
        this.hotelService = hotelService;
        this.hotelCategoryService = hotelCategoryService;
        this.ui = ui;
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








    //=============================================class members=======================================================
    private abstract class RefreshableVerticalLayout extends VerticalLayout{
        abstract void refreshContent();
    }

    private class NameChangerLayout extends RefreshableVerticalLayout{
        private TextField name;

        @Override
        void refreshContent() {

        }

    }

}
