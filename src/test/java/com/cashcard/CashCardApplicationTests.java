package com.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

// Start Spring Boot application and makes it available for our test to perform it
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardApplicationTests {
	// Ask Spring to inject a test helper that will allow HTTP requests to be made in locally running application
	// @Autowired is best used only in tests
	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void shouldReturnACashCardWhenDataIsSaved() {
		// Use "restTemplate" to make an HTTP GET request for application endpoint "/cashcards/99"
		// "restTemplate" will return a "ResponseBody" which is captured in this variable
		// ResponseBody - Spring object that provides valuable info about what happened in the request
		ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/99", String.class);

		// Can inspect many aspects of "response", including the HTTP Response Status code
		// Expects "200 OK"
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Convert the response String into a JSON-aware object with lots of helper methods
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		// Expect that when a Cash Card with "id" of 99 is requested, a JSON object with be returned with "something"
		// in the "id" field
		Number id = documentContext.read("$.id");
		assertThat(id).isEqualTo(99);

		// Expect that when a Cash Card with "amount" of 123.45 is requested, a JSON object with be returned with "something"
		// in the "amount" field
		Double amount = documentContext.read("$.amount");
		assertThat(amount).isEqualTo(123.45);
	}

	@Test
	void shouldNotReturnACashCardWithAnUnknownId() {
		ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/1000", String.class);

		// Expects "404 NOT_FOUND"
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

	@Test
	void shouldCreateANewCashCard() {
		// Database will create and manage all unique "CashCard.id" values. No need to provide one
		CashCard newCashCard = new CashCard(null, 250.00);

		// Similar to "restTemplate.getForEntity", except "newCashCard" data for new "CashCard" must be provided
		// Expect a "Void" response body as a CashCard does not need to be returned
		ResponseEntity<Void> createResponse = restTemplate.postForEntity("/cashcards", newCashCard, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

}
