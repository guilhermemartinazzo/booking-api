INSERT INTO USERS (email,user_type) values('guest@email.com','GUEST');
INSERT INTO USERS (email,user_type) values('manager@email.com','MANAGER');
INSERT INTO USERS (email,user_type) values('owner@email.com','OWNER');

INSERT INTO PROPERTY (manager_id, owner_id, description) VALUES (2,3,'Property 1');
INSERT INTO PROPERTY (manager_id, owner_id, description) VALUES (2,3,'Property 2');
INSERT INTO PROPERTY (manager_id, owner_id, description) VALUES (2,3,'Property 3');

INSERT INTO BOOKING (start_date, end_date, property_id, user_id, status) values ('2099-12-01','2099-12-05',1,1,'ACTIVE');