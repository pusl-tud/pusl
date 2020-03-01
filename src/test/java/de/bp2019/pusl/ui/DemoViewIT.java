package de.bp2019.pusl.ui;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.ui.views.DemoView;
import de.bp2019.pusl.ui.views.LecturesView;

/**
 * UI test for {@link DemoView}
 * 
 * @author Leon Chemnitz
 */
public class DemoViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoViewIT.class);

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testAccess() throws Exception {
        LOGGER.info("Testing access");

        LOGGER.info("Testing access as SUPERADMIN");
        login(UserType.SUPERADMIN);
        goToURL(DemoView.ROUTE);
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURLandWaitForRedirect(DemoView.ROUTE, LecturesView.ROUTE);
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURLandWaitForRedirect(DemoView.ROUTE, LecturesView.ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURLandWaitForRedirect(DemoView.ROUTE, LecturesView.ROUTE);
    }
}