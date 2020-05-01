package com.aspire.blog.inventory.service;

import java.util.List;
import java.util.Optional;

import com.aspire.blog.inventory.service.dto.InventoryDTO;

/**
 * Service Interface for managing
 * {@link com.aspire.blog.inventory.domain.Inventory}.
 */
public interface InventoryService {

	/**
	 * Save a inventory.
	 *
	 * @param inventoryDTO the entity to save.
	 * @return the persisted entity.
	 */
	InventoryDTO save(InventoryDTO inventoryDTO, Long orderId);

	/**
	 * Get all the inventories.
	 *
	 * @return the list of entities.
	 */
	List<InventoryDTO> findAll();

	/**
	 * Get the "id" inventory.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	Optional<InventoryDTO> findOne(Long id);

	/**
	 * occupyInventory
	 * 
	 * @param inventoryId
	 * @param orderId
	 */
	void occupyInventory(Long inventoryId, Long orderId);

}
