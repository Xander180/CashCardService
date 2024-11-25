package com.cashcard;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

// CrudRepository - interface supplied by Spring Data
// When extended, Spring Boot and Spring Data work together to automatically generate CRUD methods to interact with a database
public interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {
}
