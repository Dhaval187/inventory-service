package com.aspire.blog.inventory.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.aspire.blog.inventory.domain.Inventory} entity.
 */
public class InventoryDTO implements Serializable {

	private Long id;

	private String name;

	private Double price;

	private Long quantity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		InventoryDTO inventoryDTO = (InventoryDTO) o;
		if (inventoryDTO.getId() == null || getId() == null) {
			return false;
		}
		return Objects.equals(getId(), inventoryDTO.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}

	@Override
	public String toString() {
		return "InventoryDTO{" + "id=" + getId() + ", name='" + getName() + "'" + ", price=" + getPrice()
				+ ", quantity=" + getQuantity() + "}";
	}
}
