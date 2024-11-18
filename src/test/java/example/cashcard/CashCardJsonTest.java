package example.cashcard;

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

    @Test
    void cashCardSerializationTest() throws IOException {
        CashCard cashCard = new CashCard(99L, 123.45);

        assertThat(json.write(cashCard)).isStrictlyEqualToJson("expected.json");
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
                    "amount": 123.45
                }
                """;

        assertThat(json.parse(expected)).isEqualTo(new CashCard(99L, 123.45));
        assertThat(json.parseObject(expected).id()).isEqualTo(99);
        assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
    }

}
