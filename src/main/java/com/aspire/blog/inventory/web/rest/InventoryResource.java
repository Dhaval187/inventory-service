package com.aspire.blog.inventory.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aspire.blog.inventory.service.InventoryService;
import com.aspire.blog.inventory.service.dto.InventoryDTO;
import com.aspire.blog.inventory.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing
 * {@link com.aspire.blog.inventory.domain.Inventory}.
 */
@RestController
@RequestMapping("/api")
public class InventoryResource {

	private final Logger log = LoggerFactory.getLogger(InventoryResource.class);

	private static final String ENTITY_NAME = "inventoryInventory";

	@Value("${jhipster.clientApp.name}")
	private String applicationName;

	private final InventoryService inventoryService;

	public InventoryResource(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	/**
	 * {@code POST  /inventories} : Create a new inventory.
	 *
	 * @param inventoryDTO the inventoryDTO to create.
	 * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
	 *         body the new inventoryDTO, or with status {@code 400 (Bad Request)}
	 *         if the inventory has already an ID.
	 * @throws URISyntaxException if the Location URI syntax is incorrect.
	 */
	@PostMapping("/inventories/{inventoryId}/{orderId}")
	public ResponseEntity<Long> occupyInventory(@PathVariable Long inventoryId, @PathVariable Long orderId)
			throws URISyntaxException {
		log.debug("REST request to occupy Inventory : {}", inventoryId);
		if (inventoryId == null || orderId == null) {
			throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
		}
		inventoryService.occupyInventory(inventoryId, orderId);
		return ResponseEntity.created(new URI("/api/inventories/" + inventoryId))
				.headers(
						HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, inventoryId.toString()))
				.body(inventoryId);
	}

	/**
	 * {@code GET  /inventories} : get all the inventories.
	 *
	 * 
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
	 *         of inventories in body.
	 */
	@GetMapping("/inventories")
	public List<InventoryDTO> getAllInventories() {
		log.debug("REST request to get all Inventories");
		return inventoryService.findAll();
	}

}
