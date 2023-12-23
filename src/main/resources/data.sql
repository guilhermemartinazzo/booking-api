INSERT INTO USERS (email,user_type) values('guest1@email.com','GUEST');
INSERT INTO USERS (email,user_type) values('manager1@email.com','MANAGER');
INSERT INTO USERS (email,user_type) values('owner1@email.com','OWNER');

INSERT INTO USERS (email,user_type) values('guest2@email.com','GUEST');
INSERT INTO USERS (email,user_type) values('manager2@email.com','MANAGER');
INSERT INTO USERS (email,user_type) values('owner2@email.com','OWNER');

INSERT INTO USERS (email,user_type) values('guest3@email.com','GUEST');
INSERT INTO USERS (email,user_type) values('manager3@email.com','MANAGER');
INSERT INTO USERS (email,user_type) values('owner3@email.com','OWNER');

INSERT INTO PROPERTY (manager_id, owner_id, description) VALUES (2,3,'Property 1 - ManagerId: 2, OwnerId: 3');
INSERT INTO PROPERTY (manager_id, owner_id, description) VALUES (5,6,'Property 2 - ManagerId: 5, OwnerId: 6');
INSERT INTO PROPERTY (manager_id, owner_id, description) VALUES (8,9,'Property 3 - ManagerId: 8, OwnerId: 9');

INSERT INTO BOOKING (start_date, end_date, property_id, user_id, status, details) values ('2099-12-01','2099-12-05',1,1,'ACTIVE', 'Booking Details');