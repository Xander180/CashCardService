package com.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

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

    private CashCard findCashCard(Long requestedId, Principal principal) {
        return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
    }

    // Tells Spring to route requests to the method only on GET(read) requests that match "cashcards/{requestedId}"
    @GetMapping("/{requestedId}")
    // Handler method - gets called when a request that the method knows how to handle (matching request) is received
    // @PathVariable - Tells Spring how to get the value of the "requestedId" parameter
    // Because parameter matches the {requestedId} text within @GetMapping, Spring assigns(injects) the correct value
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        // "principal.getName()" returns the username provided from Basic Auth
        CashCard cashCard = findCashCard(requestedId, principal);

        // Determines if 'findById' did or did not find the 'CashCard' with the supplied 'id'
        // If true, repository has found the 'CashCard' and it can be retrieved
        // else, it has not been found
        if (cashCard != null) {
            return ResponseEntity.ok(cashCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // @RequestBody - POST expects a request "body", which contains the data submitted to the API
    // Spring Web will deserialize the data into a "CashCard"
    // UriComponentsBuilder - Automatically passed in by being injected from Spring's IoC Container
    // "Principal" ensures that the correct "owner" is saved with the new "CashCard"
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb, Principal principal) {
        CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.amount(), principal.getName());
        // Spring Data's CrudRepository provides methods for creating, reading, updating, and deleted data from a data store
        // Saves a new "CashCard" and returns the saved object with a unique "id" provided by the database
        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);

        // Constructs a URI to the newly-created "CashCard"
        // Caller can use this URI to GET the newly-created "CashCard"
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();

        // Return "201 CREATED" with the correct Location header
        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                // Java Bean implementation of "Pageable"
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        // Provides default values for the "page", "size", and "sort" parameters
                        // Spring provides default "page" and "size" values (0 and 20 respectively)
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));

        return ResponseEntity.ok(page.getContent());
    }

    // Supports the "PUT" verb and supplies the target "requestId".
    @PutMapping("/{requestedId}")
    // @RequestBody - contains the updated "CashCard" data
    private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {
        CashCard cashCard = findCashCard(requestedId, principal);
        if (cashCard != null) {
            // Build a "CashCard" with the updated values and save it.
            CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
            cashCardRepository.save(updatedCashCard);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
