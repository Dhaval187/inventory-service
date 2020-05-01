package com.aspire.blog.inventory.service.mapper;

import org.mapstruct.Mapper;

import com.aspire.blog.inventory.domain.Inventory;
import com.aspire.blog.inventory.service.dto.InventoryDTO;

/**
 * Mapper for the entity {@link Inventory} and its DTO {@link InventoryDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface InventoryMapper extends EntityMapper<InventoryDTO, Inventory> {

	default Inventory fromId(Long id) {
		if (id == null) {
			return null;
		}
		Inventory inventory = new Inventory();
		inventory.setId(id);
		return inventory;
	}
}
