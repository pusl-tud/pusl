package de.bp2019.pusl.ui;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.ui.views.WorkView;

/**
 * UI test for {@link WorkView}
 * 
 * @author Leon Chemnitz
 */
public class WorkViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkViewIT.class);

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testAccess() throws Exception {
        LOGGER.info("Testing access");

        LOGGER.info("Testing access as SUPERADMIN");
        login(UserType.SUPERADMIN);
        goToURL(WorkView.ROUTE);
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURL(WorkView.ROUTE);
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURL(WorkView.ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURL(WorkView.ROUTE);
    }
}