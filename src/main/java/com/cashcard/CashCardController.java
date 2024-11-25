package com.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

// Tell Spring that this class is a "Component" of type "RestController" and capable of handling HTTP requests
@RestController
// Companion to @RestController that indicates which address requests must have to access this Controller
@RequestMapping("/cashcards")
// Controller gets injected into Spring Web, which routes API requests to the correct method
public class CashCardController {
    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    // Tells Spring to route requests to the method only on GET(read) requests that match "cashcards/{requestedId}"
    @GetMapping("/{requestedId}")
    // Handler method - gets called when a request that the method knows how to handle (matching request) is received
    // @PathVariable - Tells Spring how to get the value of the "requestedId" parameter
    // Because parameter matches the {requestedId} text within @GetMapping, Spring assigns(injects) the correct value
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        // Calling 'CrudRepository.findById' which returns an Optional
        // Might or might not contain the 'CashCard' for which we're searching
        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);

        // Determines if 'findById' did or did not find the 'CashCard' with the supplied 'id'
        // If true, repository has found the 'CashCard' and it can be retrieved
        // else, it has not been found
        return cashCardOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // @RequestBody - POST expects a request "body", which contains the data submitted to the API
    // Spring Web will deserialize the data into a "CashCard"
    // UriComponentsBuilder - Automatically passed in by being injected from Spring's IoC Container
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb) {
        // Spring Data's CrudRepository provides methods for creating, reading, updating, and deleted data from a data store
        // Saves a new "CashCard" and returns the saved object with a unique "id" provided by the database
        CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);

        // Constructs a URI to the newly-created "CashCard"
        // Caller can use this URI to GET the newly-created "CashCard"
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();

        // Return "201 CREATED" with the correct Location header
        return ResponseEntity.created(locationOfNewCashCard).build();
    }
}
