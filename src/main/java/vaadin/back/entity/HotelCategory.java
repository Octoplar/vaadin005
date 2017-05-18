package vaadin.back.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Octoplar on 05.05.2017.
 */
@Entity
@Table(name="CATEGORY")
public class HotelCategory extends AbstractEntity{



    @NotNull(message = "Name is required")
    @Size(min = 1, max = 256, message = "Category name must be longer than 1 and less than 256 characters")
    private String name;

    public HotelCategory() {
    }

    public HotelCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HotelCategory{");
        sb.append("id=").append(getId());
        sb.append(", categoryName='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public HotelCategory clone() throws CloneNotSupportedException {
        return (HotelCategory) super.clone();
    }



}
