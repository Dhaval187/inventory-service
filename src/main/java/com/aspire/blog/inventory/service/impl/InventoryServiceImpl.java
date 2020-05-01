package com.aspire.blog.inventory.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aspire.blog.inventory.config.Constants;
import com.aspire.blog.inventory.domain.Inventory;
import com.aspire.blog.inventory.repository.InventoryRepository;
import com.aspire.blog.inventory.service.InventoryKafkaProducer;
import com.aspire.blog.inventory.service.InventoryService;
import com.aspire.blog.inventory.service.dto.InventoryDTO;
import com.aspire.blog.inventory.service.mapper.InventoryMapper;

/**
 * Service Implementation for managing {@link Inventory}.
 */
@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

	private final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

	private final InventoryRepository inventoryRepository;

	private final InventoryMapper inventoryMapper;

	@Autowired
	private InventoryKafkaProducer inventoryKafkaProducer;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	public InventoryServiceImpl(InventoryRepository inventoryRepository, InventoryMapper inventoryMapper) {
		this.inventoryRepository = inventoryRepository;
		this.inventoryMapper = inventoryMapper;
	}

	/**
	 * Save a inventory.
	 *
	 * @param inventoryDTO the entity to save.
	 * @return the persisted entity.
	 */
	@Override
	public InventoryDTO save(InventoryDTO inventoryDTO, Long orderId) {
		log.debug("Request to save Inventory : {}", inventoryDTO);
		Inventory inventory = inventoryMapper.toEntity(inventoryDTO);
		inventory = inventoryRepository.save(inventory);
		return inventoryMapper.toDto(inventory);
	}

	/**
	 * Get all the inventories.
	 *
	 * @return the list of entities.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<InventoryDTO> findAll() {
		log.debug("Request to get all Inventories");
		return inventoryRepository.findAll().stream().map(inventoryMapper::toDto)
				.collect(Collectors.toCollection(LinkedList::new));
	}

	/**
	 * Get one inventory by id.
	 *
	 * @param id the id of the entity.
	 * @return the entity.
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<InventoryDTO> findOne(Long id) {
		log.debug("Request to get Inventory : {}", id);
		return inventoryRepository.findById(id).map(inventoryMapper::toDto);
	}

	/**
	 * occupyInventory
	 * 
	 * @param inventoryId
	 * @param orderId
	 */
	@Transactional
	public void occupyInventory(Long inventoryId, Long orderId) {
		log.debug("Request to occupy Inventory : {} & {}", inventoryId, orderId);
		this.findOne(inventoryId).ifPresent(inventory -> {
			Long quantity = inventory.getQuantity();
			if (quantity > 0) {
				inventory.setQuantity(quantity - 1);
				save(inventory, orderId);
				inventoryKafkaProducer.sendMessage(Constants.TOPIC_INVENTORY_SUCCESS, orderId.toString());
			} else {
				inventoryKafkaProducer.sendMessage(Constants.TOPIC_INVENTORY_FAILED, orderId.toString());
			}
		});
	}
}
