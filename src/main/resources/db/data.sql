/*insert into users (name, email, phone, password, role, company_id)
values (
           'Admin',
           'admin@gmail.com',
           999999999,
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
           'ADMIN',
           1
       )
    on conflict (email) do nothing; */