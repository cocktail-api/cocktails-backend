package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.ContainerTestBase;
import de.slevermann.cocktails.backend.JdbiTest;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

/*
 * Test base class for testing DAOs. Automatically provides a clean
 * database for testing, and cleans the database again after tests are through.
 */
@JdbiTest
@Rollback(false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class DaoTestBase extends ContainerTestBase {

    @Autowired
    protected Jdbi jdbi;

    private void truncate() {
        jdbi.useHandle(h -> {
            h.execute("truncate table user_ingredient cascade");
            h.execute("truncate table \"user\" cascade");
            h.execute("truncate table cocktail_ingredient cascade");
            h.execute("truncate table cocktail cascade");
            h.execute("truncate table ingredient cascade");
            h.execute("truncate table ingredient_type cascade");
        });
    }

    @BeforeAll
    public final void beforeAll() {
        truncate();
        customInit();
    }

    @AfterAll
    public final void afterAll() {
        truncate();
        customTearDown();
    }

    /**
     * Implement this method to perform any tasks needed before tests are started
     */
    protected void customInit() {

    }

    /**
     * Implement this method to perform any tasks needed after the tests are done
     */
    protected void customTearDown() {

    }
}
