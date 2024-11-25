package com.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
