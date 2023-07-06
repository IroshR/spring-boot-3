INSERT INTO role (role_id, role_name)
VALUES (1, 'ROLE_LOGIN'),
       (2, 'ROLE_ADD_USER'),
       (3, 'ROLE_VIEW_GROUP_LIST'),
       (4, 'ROLE_VIEW_ENTITLEMENTS'),
       (5, 'ROLE_ADD_GROUP'),
       (6, 'ROLE_VIEW_USER_LIST'),
       (7, 'ROLE_VIEW_USER_DETAILS'),
       (8, 'ROLE_UPDATE_USER'),
       (9, 'ROLE_VIEW_GROUP_DETAILS'),
       (10, 'ROLE_UPDATE_GROUP');

INSERT INTO entitlement (entitlement_id, entitlement_name)
VALUES (1, 'LOGIN'),
       (2, 'ADD_USER'),
       (3, 'ADD_GROUP'),
       (4, 'VIEW_USER_LIST'),
       (5, 'VIEW_USER_DETAILS'),
       (6, 'UPDATE_USER_DETAILS'),
       (7, 'UPDATE_GROUP_DETAILS');

INSERT INTO entitlement_has_role (entitlement_id, role_id)
VALUES (1, 1),
       (2, 2),
       (2, 3),
       (3, 4),
       (3, 5),
       (4, 6),
       (5, 7),
       (6, 7),
       (6, 8),
       (7, 9),
       (7, 3),
       (7, 10);

INSERT INTO `group` (group_id, group_name)
VALUES (1, 'ADMIN');

INSERT INTO group_has_entitlement (entitlement_id, group_id)
VALUES (1, 1),
       (2, 1),
       (3, 1),
       (4, 1),
       (5, 1),
       (6, 1),
       (7, 1);

INSERT INTO user (user_id, business_title, email, first_name, is_email_confirmed, last_name, primary_phone,
                  secondary_phone)
VALUES (1, 'Employee', 'test@gmail.com', 'Test', false, 'User', '1234567789', null);

INSERT INTO user_has_group (group_id, user_id)
VALUES (1, 1);

INSERT INTO user_login (login_id, fail_attempts, password, user_name, user_id, need_to_change_password, user_status)
VALUES (1, null, '$2b$14$K67aw6RLKxlxbrWhfTZjXuAWqMyX2tPR801S/EII0XV7Cgp7QEOGK', 'test@gmail.com', 1, false, 'ACTIVE');
