package com.cashcard;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

// Marks class as a test class which uses the Jackson framework (included as part of Spring)
// Provides extensive JSON testing and parsing support
// Establishes all related behavior to test JSON objects
@JsonTest
public class CashCardJsonTest {

    // Annotation that directs Spring to create an object of requested type
    @Autowired
    // JacksonTester - a convenience wrapper to the Jackson JSON parsing library
    // Handles serialization/deserialization of JSON objects
    private JacksonTester<CashCard> json;

    @Autowired
    private JacksonTester<CashCard[]> jsonList;

    private CashCard[] cashCards;

    @BeforeEach
    void setUp() {
        cashCards = Arrays.array(
                new CashCard(99L, 123.45, "wilson"),
                new CashCard(100L, 1.00, "wilson"),
                new CashCard(101L, 150.00, "wilson")
        );
    }

    @Test
    void cashCardSerializationTest() throws IOException {
        CashCard cashCard = new CashCard(99L, 123.45, "wilson");

        assertThat(json.write(cashCard)).isStrictlyEqualToJson("single.json");
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.id").isEqualTo(99);
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.amount");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.amount").isEqualTo(123.45);
    }

    @Test
    // Deserializes data so that it converts from JSON to Java after previous test passes
    // Makes it possible for an object serialized on one platform to be deserialized on a different one
    // (ex. Object is serialized on Windows while the backend would deserialize it on Linux)
    void cashCardDeserializationTest() throws IOException {
        String expected = """
                {
                    "id": 99,
                    "amount": 123.45,
                    "owner":  "wilson"
                }
                """;

        assertThat(json.parse(expected)).isEqualTo(new CashCard(99L, 123.45, "wilson"));
        assertThat(json.parseObject(expected).id()).isEqualTo(99);
        assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
    }

    // Serializes "cashCards" variable into JSON,
    // then asserts that "list.json" should contain the same data as the variable
    @Test
    void cashCardListSerializationTest() throws IOException {
        assertThat(jsonList.write(cashCards)).isStrictlyEqualToJson("list.json");
    }

    @Test
    void cashCardListDeserializationTest() throws IOException {
        String expected = """
                [
                  {"id":  99, "amount":  123.45, "owner":  "wilson"},
                  {"id":  100, "amount":  1.00, "owner":  "wilson"},
                  {"id":  101, "amount":  150.00, "owner":  "wilson"}
                ]
                """;

        assertThat(jsonList.parse(expected)).isEqualTo(cashCards);
    }

}
