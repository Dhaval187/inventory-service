package com.aspire.blog.inventory.web.rest;

import static com.aspire.blog.inventory.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import com.aspire.blog.inventory.InventoryApp;
import com.aspire.blog.inventory.domain.Inventory;
import com.aspire.blog.inventory.repository.InventoryRepository;
import com.aspire.blog.inventory.service.InventoryService;
import com.aspire.blog.inventory.service.dto.InventoryDTO;
import com.aspire.blog.inventory.service.mapper.InventoryMapper;
import com.aspire.blog.inventory.web.rest.errors.ExceptionTranslator;

/**
 * Integration tests for the {@link InventoryResource} REST controller.
 */
@EmbeddedKafka
@SpringBootTest(classes = InventoryApp.class)
public class InventoryResourceIT {

	private static final String DEFAULT_NAME = "AAAAAAAAAA";
	private static final String UPDATED_NAME = "BBBBBBBBBB";

	private static final Double DEFAULT_PRICE = 1D;
	private static final Double UPDATED_PRICE = 2D;
	private static final Double SMALLER_PRICE = 1D - 1D;

	private static final Long DEFAULT_QUANTITY = 1L;
	private static final Long UPDATED_QUANTITY = 2L;
	private static final Long SMALLER_QUANTITY = 1L - 1L;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private InventoryMapper inventoryMapper;

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Autowired
	private ExceptionTranslator exceptionTranslator;

	@Autowired
	private EntityManager em;

	@Autowired
	private Validator validator;

	private MockMvc restInventoryMockMvc;

	private Inventory inventory;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final InventoryResource inventoryResource = new InventoryResource(inventoryService);
		this.restInventoryMockMvc = MockMvcBuilders.standaloneSetup(inventoryResource)
				.setCustomArgumentResolvers(pageableArgumentResolver).setControllerAdvice(exceptionTranslator)
				.setConversionService(createFormattingConversionService()).setMessageConverters(jacksonMessageConverter)
				.setValidator(validator).build();
	}

	/**
	 * Create an entity for this test.
	 *
	 * This is a static method, as tests for other entities might also need it, if
	 * they test an entity which requires the current entity.
	 */
	public static Inventory createEntity(EntityManager em) {
		Inventory inventory = new Inventory().name(DEFAULT_NAME).price(DEFAULT_PRICE).quantity(DEFAULT_QUANTITY);
		return inventory;
	}

	/**
	 * Create an updated entity for this test.
	 *
	 * This is a static method, as tests for other entities might also need it, if
	 * they test an entity which requires the current entity.
	 */
	public static Inventory createUpdatedEntity(EntityManager em) {
		Inventory inventory = new Inventory().name(UPDATED_NAME).price(UPDATED_PRICE).quantity(UPDATED_QUANTITY);
		return inventory;
	}

	@BeforeEach
	public void initTest() {
		inventory = createEntity(em);
	}

//	@Test
	@Transactional
	public void createInventory() throws Exception {
		int databaseSizeBeforeCreate = inventoryRepository.findAll().size();

		// Create the Inventory
		InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);
		restInventoryMockMvc.perform(post("/api/inventories").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(inventoryDTO))).andExpect(status().isCreated());

		// Validate the Inventory in the database
		List<Inventory> inventoryList = inventoryRepository.findAll();
		assertThat(inventoryList).hasSize(databaseSizeBeforeCreate + 1);
		Inventory testInventory = inventoryList.get(inventoryList.size() - 1);
		assertThat(testInventory.getName()).isEqualTo(DEFAULT_NAME);
		assertThat(testInventory.getPrice()).isEqualTo(DEFAULT_PRICE);
		assertThat(testInventory.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
	}

//	@Test
	@Transactional
	public void createInventoryWithExistingId() throws Exception {
		int databaseSizeBeforeCreate = inventoryRepository.findAll().size();

		// Create the Inventory with an existing ID
		inventory.setId(1L);
		InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

		// An entity with an existing ID cannot be created, so this API call must fail
		restInventoryMockMvc.perform(post("/api/inventories").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(inventoryDTO))).andExpect(status().isBadRequest());

		// Validate the Inventory in the database
		List<Inventory> inventoryList = inventoryRepository.findAll();
		assertThat(inventoryList).hasSize(databaseSizeBeforeCreate);
	}

	@Test
	@Transactional
	public void getAllInventories() throws Exception {
		// Initialize the database
		inventoryRepository.saveAndFlush(inventory);

		// Get all the inventoryList
		restInventoryMockMvc.perform(get("/api/inventories?sort=id,desc")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.[*].id").value(hasItem(inventory.getId().intValue())))
				.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
				.andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
				.andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.intValue())));
	}

//	@Test
	@Transactional
	public void getInventory() throws Exception {
		// Initialize the database
		inventoryRepository.saveAndFlush(inventory);

		// Get the inventory
		restInventoryMockMvc.perform(get("/api/inventories/{id}", inventory.getId())).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.id").value(inventory.getId().intValue()))
				.andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
				.andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
				.andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY.intValue()));
	}

	@Test
	@Transactional
	public void getNonExistingInventory() throws Exception {
		// Get the inventory
		restInventoryMockMvc.perform(get("/api/inventories/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
	}

//	@Test
	@Transactional
	public void updateInventory() throws Exception {
		// Initialize the database
		inventoryRepository.saveAndFlush(inventory);

		int databaseSizeBeforeUpdate = inventoryRepository.findAll().size();

		// Update the inventory
		Inventory updatedInventory = inventoryRepository.findById(inventory.getId()).get();
		// Disconnect from session so that the updates on updatedInventory are not
		// directly saved in db
		em.detach(updatedInventory);
		updatedInventory.name(UPDATED_NAME).price(UPDATED_PRICE).quantity(UPDATED_QUANTITY);
		InventoryDTO inventoryDTO = inventoryMapper.toDto(updatedInventory);

		restInventoryMockMvc.perform(put("/api/inventories").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(inventoryDTO))).andExpect(status().isOk());

		// Validate the Inventory in the database
		List<Inventory> inventoryList = inventoryRepository.findAll();
		assertThat(inventoryList).hasSize(databaseSizeBeforeUpdate);
		Inventory testInventory = inventoryList.get(inventoryList.size() - 1);
		assertThat(testInventory.getName()).isEqualTo(UPDATED_NAME);
		assertThat(testInventory.getPrice()).isEqualTo(UPDATED_PRICE);
		assertThat(testInventory.getQuantity()).isEqualTo(UPDATED_QUANTITY);
	}

//	@Test
	@Transactional
	public void updateNonExistingInventory() throws Exception {
		int databaseSizeBeforeUpdate = inventoryRepository.findAll().size();

		// Create the Inventory
		InventoryDTO inventoryDTO = inventoryMapper.toDto(inventory);

		// If the entity doesn't have an ID, it will throw BadRequestAlertException
		restInventoryMockMvc.perform(put("/api/inventories").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(inventoryDTO))).andExpect(status().isBadRequest());

		// Validate the Inventory in the database
		List<Inventory> inventoryList = inventoryRepository.findAll();
		assertThat(inventoryList).hasSize(databaseSizeBeforeUpdate);
	}

//	@Test
	@Transactional
	public void deleteInventory() throws Exception {
		// Initialize the database
		inventoryRepository.saveAndFlush(inventory);

		int databaseSizeBeforeDelete = inventoryRepository.findAll().size();

		// Delete the inventory
		restInventoryMockMvc
				.perform(delete("/api/inventories/{id}", inventory.getId()).accept(TestUtil.APPLICATION_JSON_UTF8))
				.andExpect(status().isNoContent());

		// Validate the database contains one less item
		List<Inventory> inventoryList = inventoryRepository.findAll();
		assertThat(inventoryList).hasSize(databaseSizeBeforeDelete - 1);
	}

	@Test
	@Transactional
	public void equalsVerifier() throws Exception {
		TestUtil.equalsVerifier(Inventory.class);
		Inventory inventory1 = new Inventory();
		inventory1.setId(1L);
		Inventory inventory2 = new Inventory();
		inventory2.setId(inventory1.getId());
		assertThat(inventory1).isEqualTo(inventory2);
		inventory2.setId(2L);
		assertThat(inventory1).isNotEqualTo(inventory2);
		inventory1.setId(null);
		assertThat(inventory1).isNotEqualTo(inventory2);
	}

	@Test
	@Transactional
	public void dtoEqualsVerifier() throws Exception {
		TestUtil.equalsVerifier(InventoryDTO.class);
		InventoryDTO inventoryDTO1 = new InventoryDTO();
		inventoryDTO1.setId(1L);
		InventoryDTO inventoryDTO2 = new InventoryDTO();
		assertThat(inventoryDTO1).isNotEqualTo(inventoryDTO2);
		inventoryDTO2.setId(inventoryDTO1.getId());
		assertThat(inventoryDTO1).isEqualTo(inventoryDTO2);
		inventoryDTO2.setId(2L);
		assertThat(inventoryDTO1).isNotEqualTo(inventoryDTO2);
		inventoryDTO1.setId(null);
		assertThat(inventoryDTO1).isNotEqualTo(inventoryDTO2);
	}

	@Test
	@Transactional
	public void testEntityFromId() {
		assertThat(inventoryMapper.fromId(42L).getId()).isEqualTo(42);
		assertThat(inventoryMapper.fromId(null)).isNull();
	}
}
