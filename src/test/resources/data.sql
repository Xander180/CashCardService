/* This file is stored in the test resources folder because we only want the following data to be used in our tests, not
in a production system to load Cash Card '99'

Spring allows for the separation of test-only resources from the main resources when needed
*/

INSERT INTO CASH_CARD(ID, AMOUNT) VALUES (99, 123.45);
