/* This file is stored in the test resources folder because we only want the following data to be used in our tests, not
in a production system

Spring allows for the separation of test-only resources from the main resources when needed
*/

INSERT INTO CASH_CARD(ID, AMOUNT, OWNER) VALUES (99, 123.45, 'wilson');
INSERT INTO CASH_CARD(ID, AMOUNT, OWNER) VALUES (100, 1.00, 'wilson');
INSERT INTO CASH_CARD(ID, AMOUNT, OWNER) VALUES (101, 150.00, 'wilson');
INSERT INTO CASH_CARD(ID, AMOUNT, OWNER) VALUES (102, 200.00, 'brenda');
