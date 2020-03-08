package de.bp2019.pusl.ui.lecture;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.ui.views.LecturesView;
import de.bp2019.pusl.ui.views.lecture.ManageLecturesView;

/**
 * UI test for {@link ManageLecturesView}
 * 
 * @author Leon Chemnitz
 */
public class ManageLecturesViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageLecturesViewIT.class);

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testAccess() throws Exception {
        LOGGER.info("Testing access");

        LOGGER.info("Testing access as SUPERADMIN");
        login(UserType.SUPERADMIN);
        goToURL(ManageLecturesView.ROUTE);
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURL(ManageLecturesView.ROUTE);
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURLandWaitForRedirect(ManageLecturesView.ROUTE, LecturesView.ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURLandWaitForRedirect(ManageLecturesView.ROUTE, LecturesView.ROUTE);
    }
}