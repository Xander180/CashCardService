package com.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

// Start Spring Boot application and makes it available for our test to perform it
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Causes Spring to start with a clean slate, as if all other tests were not ran
//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
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
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("wilson", "abc123")
				.getForEntity("/cashcards/99", String.class);

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
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("wilson", "abc123")
				.getForEntity("/cashcards/1000", String.class);

		// Expects "404 NOT_FOUND"
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

	@Test
	@DirtiesContext
	void shouldCreateANewCashCard() {
		// Database will create and manage all unique "CashCard.id" values. No need to provide one
		CashCard newCashCard = new CashCard(null, 250.00, null);

		// Similar to "restTemplate.getForEntity", except "newCashCard" data for new "CashCard" must be provided
		// Expect a "Void" response body as a CashCard does not need to be returned
		ResponseEntity<Void> createResponse = restTemplate
				.withBasicAuth("wilson", "abc123")
				.postForEntity("/cashcards", newCashCard, Void.class);

		// HTTP response status code should be "201 CREATED" if new "CashCard" is created
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// When a POST request results in successful creation of a new "CashCard", response should include info for how
		// to retrieve that resource. A URI in the Response Header named "Location" is supplied
		URI locationOfNewCashCard = createResponse.getHeaders().getLocation();

		// Use the Location header's info to fetch newly created "CashCard"
		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("wilson", "abc123")
				.getForEntity(locationOfNewCashCard, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		Double amount = documentContext.read("$.amount");

		// Verify that new "CashCard.id" is not null, and that "CashCard.amount" is 250.00
		assertThat(id).isNotNull();
		assertThat(amount).isEqualTo(250.00);
	}

	// Since this is requested the entire list of cards, no additional information is necessary in the request
	@Test
	void shouldReturnAllCashCardsWhenListIsRequested() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("wilson", "abc123")
				.getForEntity("/cashcards", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		// Calculates the length of the array
		int cashCardCount = documentContext.read("$.length()");
		assertThat(cashCardCount).isEqualTo(3);

		// Retrieves the list of all "id" values returned
		JSONArray ids = documentContext.read("$..id");
		assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

		// Retrieves the list of all "amount" values returned
		JSONArray amounts = documentContext.read("$..amount");
		assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.00, 150.00);
	}

	@Test
	void shouldReturnAPageOfCashCards() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("wilson", "abc123")
				.getForEntity("/cashcards?page=0&size=1", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(1);
	}

	@Test
	void shouldReturnASortedPageOfCashCards() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("wilson", "abc123")
				.getForEntity("/cashcards?page=0&size=1&sort=amount,desc", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray read = documentContext.read("$[*]");
		assertThat(read.size()).isEqualTo(1);

		double amount = documentContext.read("$[0].amount");
		assertThat(amount).isEqualTo(150.00);
	}

	@Test
	void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("wilson", "abc123")
				.getForEntity("/cashcards", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(3);

		JSONArray amounts = documentContext.read("$..amount");
		assertThat(amounts).containsExactly(1.00, 123.45, 150.00);
	}

	@Test
	void shouldNotReturnACashCardWhenUsingBadCredentials() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("BAD-USER", "abc123")
				.getForEntity("/cashcards/99", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

		response = restTemplate
				.withBasicAuth("wilson", "BAD-PASSWORD")
				.getForEntity("/cashcards/99", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void shouldRejectUsersWhoAreNotCardOwners() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("hank-owns-no-cards", "qrs456")
				.getForEntity("/cashcards/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	void shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
		ResponseEntity<String > response = restTemplate
				.withBasicAuth("wilson", "abc123")
				.getForEntity("/cashcards/102", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@DirtiesContext
	void shouldUpdateAnExistingCashCard() {
		CashCard cashCardUpdate = new CashCard(null, 19.99, null);
		// Needed for the "exchange()" method below.
		HttpEntity<CashCard> request = new HttpEntity<>(cashCardUpdate);
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("wilson", "abc123")
				// More general version of the "xyzForEntity()" methods used in other tests.
				// Sends a "PUT" request for the target ID of 99 and updated Cash Card data.
				.exchange("/cashcards/99", HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// Fetch "CashCard 99" to verify that it was updated
		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("wilson", "abc123")
				.getForEntity("/cashcards/99", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		Double amount = documentContext.read("$.amount");
		// Assert that the correct "CashCard 99" was retrieved and that "amount" was updated tp "19.99".
		assertThat(id).isEqualTo(99);
		assertThat(amount).isEqualTo(19.99);
	}

	@Test
	void shouldNotUpdateACashCardThatDoesNotExist() {
		CashCard unknownCashCard = new CashCard(null, 19.99, null);
		HttpEntity<CashCard> request = new HttpEntity<>(unknownCashCard);
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("wilson", "abc123")
				.exchange("/cashcards/99999", HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldNotUpdateACashCardThatIsOwnedBySomeoneElse() {
		CashCard brendasCard = new CashCard(null, 333.33, null);
		HttpEntity<CashCard> request = new HttpEntity<>(brendasCard);
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("wilson", "abc123")
				.exchange("/cashcards/102", HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

}
