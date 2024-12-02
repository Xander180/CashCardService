/* A blueprint for how data is stored in a database
    Reflects the CashCard object that we understand, which contains `id` and an `amount`
 */

CREATE TABLE cash_card
(
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    AMOUNT NUMBER NOT NULL DEFAULT 0,
    OWNER VARCHAR(256) NOT NULL
);