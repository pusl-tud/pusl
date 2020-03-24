package de.bp2019.pusl.ui;

import de.bp2019.pusl.repository.GradeRepository;
import de.bp2019.pusl.ui.views.WorkView;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.ui.views.DatabaseView;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * UI test for {@link DatabaseView}
 *
 * @author Leon Chemnitz
 */
public class DatabaseViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseViewIT.class);

    @Autowired
    GradeRepository gradeRepository;

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testAccess() throws Exception {
        LOGGER.info("Testing access");

        LOGGER.info("Testing access as SUPERADMIN");
        login(UserType.SUPERADMIN);
        goToURL(DatabaseView.ROUTE);
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURLandWaitForRedirect(DatabaseView.ROUTE, PuslProperties.ROOT_ROUTE);
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURLandWaitForRedirect(DatabaseView.ROUTE, PuslProperties.ROOT_ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURLandWaitForRedirect(DatabaseView.ROUTE, PuslProperties.ROOT_ROUTE);
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testDatabaseRefillButton() throws Exception {
        LOGGER.info("Testing Database Refill button");
        login(UserType.SUPERADMIN);

        goToURL(DatabaseView.ROUTE);

        findButtonContainingText("Datenbank neu befüllen").click();

        goToURL(WorkView.ROUTE);

    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testGradesRefillButton() throws Exception {
        LOGGER.info("Testing Grade Refill button");
        login(UserType.SUPERADMIN);

        goToURL(DatabaseView.ROUTE);

        findElementById("numGrades").sendKeys("500");
        findButtonContainingText("Noten generieren").click();

        goToURL(WorkView.ROUTE);

    }
}