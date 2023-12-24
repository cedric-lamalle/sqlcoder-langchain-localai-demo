CREATE TABLE products
(
    product_id INTEGER PRIMARY KEY, -- Unique ID for each product
    name       VARCHAR(50),         -- Name of the product
    price      DECIMAL(10, 2),      -- Price of each unit of the product
    quantity   INTEGER              -- Current quantity in stock
);

comment on column products.product_id is 'Unique ID for each product';
comment on column products.name is 'Name of the product';
comment on column products.price is 'Price of each unit of the product';
comment on column products.quantity is 'Current quantity in stock';

CREATE TABLE customers
(
    customer_id INTEGER PRIMARY KEY, -- Unique ID for each customer
    name        VARCHAR(50),         -- Name of the customer
    address     VARCHAR(100)         -- Mailing address of the customer
);

comment on column customers.customer_id is 'Unique ID for each customer';
comment on column customers.name is 'Name of the customer';
comment on column customers.address is 'Mailing address of the customer';

CREATE TABLE salespeople
(
    salesperson_id INTEGER PRIMARY KEY, -- Unique ID for each salesperson
    name           VARCHAR(50),         -- Name of the salesperson
    region         VARCHAR(50)          -- Geographic sales region
);

comment on column salespeople.salesperson_id is 'Unique ID for each salesperson';
comment on column salespeople.name is 'Name of the salesperson';
comment on column salespeople.region is 'Geographic sales region';

CREATE TABLE sales
(
    sale_id        INTEGER PRIMARY KEY, -- Unique ID for each sale
    product_id     INTEGER,             -- ID of product sold
    customer_id    INTEGER,             -- ID of customer who made purchase
    salesperson_id INTEGER,             -- ID of salesperson who made the sale
    sale_date      DATE,                -- Date the sale occurred
    quantity       INTEGER              -- Quantity of product sold
);

comment on column sales.sale_id is 'Unique ID for each sale';
comment on column sales.product_id is 'ID of product sold';
comment on column sales.customer_id is 'ID of customer who made purchase';
comment on column sales.salesperson_id is 'ID of salesperson who made the sale';
comment on column sales.sale_date is 'Date the sale occurred';
comment on column sales.quantity is 'Quantity of product sold';

CREATE TABLE product_suppliers
(
    supplier_id  INTEGER PRIMARY KEY, -- Unique ID for each supplier
    product_id   INTEGER,             -- Product ID supplied
    supply_price DECIMAL(10, 2)       -- Unit price charged by supplier
);

comment on column product_suppliers.supplier_id is 'Unique ID for each supplier';
comment on column product_suppliers.product_id is 'Product ID supplied';
comment on column product_suppliers.supply_price is 'Unit price charged by supplier';


comment on table products is 'Products';
comment on table customers is 'Customers';
comment on table salespeople is 'Sales Person';
comment on table sales is 'Sales';
comment on table product_suppliers is 'Product Suppliers';

ALTER TABLE sales ADD CONSTRAINT sales_product_fk
    FOREIGN KEY (product_id) REFERENCES products (product_id);

ALTER TABLE sales ADD CONSTRAINT sales_customers_fk
    FOREIGN KEY (customer_id) REFERENCES customers (customer_id);

ALTER TABLE sales ADD CONSTRAINT sales_persons_fk
    FOREIGN KEY (salesperson_id) REFERENCES salespeople (salesperson_id);

ALTER TABLE product_suppliers ADD CONSTRAINT product_suppliers_product_fk
    FOREIGN KEY (product_id) REFERENCES products (product_id);
