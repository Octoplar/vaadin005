package vaadin.back.entity;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@NamedQueries({
		@NamedQuery(name = "Hotel.byName", query = "SELECT e FROM Hotel AS e WHERE LOWER(e.name) LIKE :filter "),
		@NamedQuery(name = "Hotel.byAddress", query = "SELECT e FROM Hotel AS e WHERE LOWER(e.address) LIKE :filter "),
		@NamedQuery(name = "Hotel.All.Count", query = "SELECT COUNT(e.id) FROM Hotel AS e"),
		@NamedQuery(name = "Hotel.NameFilter.Count", query = "SELECT COUNT(e.id) FROM Hotel AS e WHERE LOWER(e.name) LIKE :filter "),
		@NamedQuery(name = "Hotel.AddressFilter.Count", query = "SELECT COUNT(e.id) FROM Hotel AS e WHERE LOWER(e.address) LIKE :filter ")

})
@Table(name="HOTEL")
public class Hotel extends AbstractEntity {



	@NotNull(message = "Name is required")
	@Size(min = 1, max = 256, message = "name must be longer than 1 and less than 256 characters")
	private String name;

	@NotNull(message = "Address is required")
	@Size(min = 1, max = 256, message = "Address must be longer than 1 and less than 256 characters")
	private String address;

	@NotNull(message = "Rating is required")
	@Min(value = 1, message = "Rating must be between 1 and 5")
	@Max(value = 5, message = "Rating must be between 1 and 5")
	private Integer rating;

	@NotNull(message = "Date is required")
	@Min(value = 1, message = "Date can not be future")
	@Column(name = "OPERATES_FROM")
	private Long operatesFrom;

	@NotNull
	@ManyToOne
	private HotelCategory category;

	@NotNull(message = "URL is required")
	@Size(min = 1, max = 256, message = "URL must be longer than 1 and less than 256 characters")
	private String url;

	@Size(min = 1, max = 65535, message = "Description must be longer than 1 and less than 65535 characters")
	private String description;

	@Embedded
	private PaymentType paymentType;





	public Hotel() {
	}

	public Hotel(String name, String address, Integer rating, Long operatesFrom, HotelCategory category, String url, String description, PaymentType paymentType) {
		this.name = name;
		this.address = address;
		this.rating = rating;
		this.operatesFrom = operatesFrom;
		this.category = category;
		this.url = url;
		this.description = description;
		this.paymentType = paymentType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}


	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Long getOperatesFrom() {
		return operatesFrom;
	}

	public void setOperatesFrom(Long operatesFrom) {
		this.operatesFrom = operatesFrom;
	}

	public HotelCategory getCategory() {
		return category;
	}

	public void setCategory(HotelCategory category) {
		this.category = category;
	}	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Hotel{");
		sb.append("name='").append(name).append('\'');
		sb.append(", address='").append(address).append('\'');
		sb.append(", rating=").append(rating);
		sb.append(", operatesFrom=").append(operatesFrom);
		sb.append(", category=").append(category);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public Hotel clone() throws CloneNotSupportedException {
		Hotel result = (Hotel) super.clone();
		//deep copy of mutable objects
		if (this.getCategory()!=null)
			result.setCategory(this.getCategory().clone());
		return result;
	}


}