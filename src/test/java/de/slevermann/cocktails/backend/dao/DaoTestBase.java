package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.ContainerTestBase;
import de.slevermann.cocktails.backend.JdbiTest;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

/*
 * Test base class for testing DAOs. Automatically provides a clean
 * database for testing, and cleans the database again after tests are through.
 */
@JdbiTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class DaoTestBase extends ContainerTestBase {

    @Autowired
    protected Jdbi jdbi;

    private void truncate() {
        jdbi.useHandle(h -> {
            h.execute("TRUNCATE TABLE user_ingredient CASCADE");
            h.execute("TRUNCATE TABLE \"user\" CASCADE");
            h.execute("TRUNCATE TABLE cocktail_ingredient CASCADE");
            h.execute("TRUNCATE TABLE cocktail CASCADE");
            h.execute("TRUNCATE TABLE ingredient CASCADE");
            h.execute("TRUNCATE TABLE ingredient_type CASCADE");
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
