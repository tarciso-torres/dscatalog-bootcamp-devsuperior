package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository;

	private Long existingId;
	private Long nomExistingId;
	private Long countTotalProducts;
	private Product product;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1l;
		nomExistingId = 1000l;
		countTotalProducts = 25L;
		product = Factory.createProduct();
	}

	@Test
	void shouldPersistWithAutoincrementWhenIdIsNull() {
		product.setId(null);

		product = repository.save(product);

		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
	}

	@Test
	void shouldFindProductWithIdExist() {

		Optional<Product> result = repository.findById(product.getId());
		Assertions.assertTrue(result.isPresent());
	}

	@Test
	void shouldReturnEmptyProductWithIdIsNotExist() {

		Optional<Product> result = repository.findById(nomExistingId);
		Assertions.assertTrue(result.isEmpty());
	}

	@Test
	void shouldDeleteObjectWhenIdExists() {

		repository.deleteById(existingId);

		Optional<Product> result = repository.findById(existingId);

		Assertions.assertFalse(result.isPresent());
	}

	@Test
	void shouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nomExistingId);
		});
	}

}
