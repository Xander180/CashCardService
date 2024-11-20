package com.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Tell Spring that this class is a "Component" of type "RestController" and capable of handling HTTP requests
@RestController
// Companion to @RestController that indicates which address requests must have to access this Controller
@RequestMapping("/cashcards")
// Controller gets injected into Spring Web, which routes API requests to the correct method
public class CashCardController {

    // Tells Spring to route requests to the method only on GET(read) requests that match "cashcards/{requestedId}"
    @GetMapping("/{requestedId}")
    // Handler method - gets called when a request that the method knows how to handle (matching request) is received
    // @PathVariable - Tells Spring how to get the value of the "requestedId" parameter
    // Because parameter matches the {requestedId} text within @GetMapping, Spring assigns(injects) the correct value
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        if (requestedId.equals(99L)) {
            CashCard cashCard = new CashCard(99L, 123.45);
            return ResponseEntity.ok(cashCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
