package br.com.basis.langchain.dbmetadata.sales;

import com.github.javafaker.Faker;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@Profile("initial-load")
@Transactional
public class InitSalesDb implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitSalesDb.class);
    private final static String[] REGIONS = {"Centroeste", "Nordeste", "Norte", "Sul", "Sudeste"};
    @Language("PostgreSQL")
    private final static String TEMPLATE_SALES_PEOPLE = "insert into salespeople (salesperson_id, name, region) values (?, ?, ?)";
    @Language("PostgreSQL")
    private final static String TEMPLATE_PRODUCTS = "insert into products (product_id, name, price, quantity) VALUES (?, ?, ?, ?)";
    @Language("PostgreSQL")
    private final static String TEMPLATE_CUSTOMERS = "insert into customers (customer_id, name, address) VALUES (?, ?, ?)";
    @Language("PostgreSQL")
    private final static String TEMPLATE_PRODUCT_SUPPLIERS = "insert into product_suppliers (supplier_id, product_id, supply_price) VALUES (?, ?, ?)";
    @Language("PostgreSQL")
    private final static String TEMPLATE_SALES = "insert into sales (sale_id, product_id, customer_id, salesperson_id, sale_date, quantity) VALUES (?, ?, ?, ?, ?, ?)";

    private final static Random RANDOM = new SecureRandom();
    public static final int NUM_SALESPERSONS = 100;
    public static final int NUM_PRODUCTS = 1000;
    public static final int NUM_CUSTOMERS = 1000;
    private final JdbcTemplate jdbcTemplate;
    private final Faker faker;

    public InitSalesDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.faker = new Faker();
    }

    @Override
    public void run(String... args) {
        logger.debug("Iniciando cargas...");
        fillSalesPeople();
        fillProducts();
        fillCustomers();
        fillSales();
        fillProductSuppliers();
    }

    private void fillProductSuppliers() {
        for(int i = 1; i <= 100; i++) {
            jdbcTemplate.update(
                    TEMPLATE_PRODUCT_SUPPLIERS,
                    i,
                    RANDOM.nextInt(1, NUM_PRODUCTS),
                    RANDOM.nextFloat(2, 90)
            );
        }
    }


    private void fillSales() {
        for(int i = 1; i <= 10000; i++) {
            jdbcTemplate.update(
                    TEMPLATE_SALES,
                    i,
                    RANDOM.nextInt(1, NUM_PRODUCTS),
                    RANDOM.nextInt(1, NUM_CUSTOMERS),
                    RANDOM.nextInt(1, NUM_SALESPERSONS),
                    faker.date().past(2*365, TimeUnit.DAYS),
                    RANDOM.nextInt(1,20)
                    );
        }
    }

    private void fillCustomers() {
        for(int i = 1; i <= NUM_CUSTOMERS; i++) {
            jdbcTemplate.update(
                    TEMPLATE_CUSTOMERS,
                    i,
                    faker.name().name(),
                    faker.address().fullAddress()
                    );
        }
    }

    private void fillProducts() {
        for(int i = 1; i <= NUM_PRODUCTS; i++) {
            jdbcTemplate.update(
                    TEMPLATE_PRODUCTS,
                    i,
                    faker.beer().name(),
                    RANDOM.nextFloat(3, 100),
                    RANDOM.nextInt(1, 1000));
        }
    }

    protected void fillSalesPeople() {
        for(int i = 1; i <= NUM_SALESPERSONS; i++) {
            jdbcTemplate.update(
                    TEMPLATE_SALES_PEOPLE,
                    i,
                    faker.name().name(),
                    REGIONS[RANDOM.nextInt(REGIONS.length)]);
        }
    }
}
