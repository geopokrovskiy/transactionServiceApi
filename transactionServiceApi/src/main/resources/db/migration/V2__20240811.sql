INSERT INTO transaction_service.wallet_types(currency_code, name, status, user_type, creator)
VALUES ('RUB', 'Russian Rouble', 'ACTIVE', 'BASIC', 'geopokrovskiy'),
       ('EUR', 'Euro', 'ACTIVE', 'BASIC', 'geopokrovskiy'),
       ('USD', 'United States Dollar', 'ACTIVE', 'BASIC', 'geopokrovskiy'),
       ('CHF', 'Swiss Franc', 'ACTIVE', 'BASIC', 'geopokrovskiy'),
       ('CNY', 'Chinese Yuan', 'ACTIVE', 'BASIC', 'geopokrovskiy'),
       ('JPY', 'Japanese Yen', 'ACTIVE', 'BASIC', 'geopokrovskiy'),
       ('GBP', 'Pound Sterling', 'ACTIVE', 'BASIC', 'geopokrovskiy')
ON CONFLICT DO NOTHING;