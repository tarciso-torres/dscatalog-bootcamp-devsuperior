package com.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
class ProductResourceTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper ObjectMapper;
	
	private PageImpl<ProductDTO> page;
	private ProductDTO productDTO;
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		
		when(service.findAllPaged(any())).thenReturn(page);
		
		when(service.insert(any())).thenReturn(productDTO);
		
		when(service.findById(existingId)).thenReturn(productDTO);
		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		
		when(service.update(eq(existingId), any())).thenReturn(productDTO);
		when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
		
		doNothing().when(service).delete(existingId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		doThrow(DatabaseException.class).when(service).delete(dependentId);
	}
	
	@Test
	void shouldReturnNotFoundStatusWhenIdDoesNotExists() throws Exception {
		ResultActions result = 
				mockMvc.perform(delete("/products/{id}", nonExistingId)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	void shouldReturnNoContentStatusWhenIdExists() throws Exception {
		ResultActions result = 
				mockMvc.perform(delete("/products/{id}", existingId)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNoContent());
	}
	
	@Test
	void shouldReturnProductDTO() throws Exception {
		String jsonBody = ObjectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(post("/products")
					.content(jsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}

	@Test
	void shouldReturnProductDTOWhenIdExist() throws Exception {
		String jsonBody = ObjectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", existingId)
					.content(jsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {
		
		String jsonBody = ObjectMapper.writeValueAsString(productDTO);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", nonExistingId)
					.content(jsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() throws Exception {
		ResultActions result = 
				mockMvc.perform(get("/products/{id}", nonExistingId)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	void shouldReturnProductWhenIdExists() throws Exception {
		ResultActions result = 
				mockMvc.perform(get("/products/{id}", existingId)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	void shouldReturnPage() throws Exception {
		ResultActions result = 
				mockMvc.perform(get("/products")
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
	}

}
