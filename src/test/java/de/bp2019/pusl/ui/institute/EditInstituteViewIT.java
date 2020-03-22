package de.bp2019.pusl.ui.institute;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.ui.views.institute.EditInstituteView;

/**
 * UI test for {@link EditInstituteView}
 * 
 * @author Leon Chemnitz
 */
public class EditInstituteViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditInstituteViewIT.class);

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testAccess() throws Exception {
        LOGGER.info("Testing access");

        LOGGER.info("Testing access as SUPERADMIN");
        login(UserType.SUPERADMIN);
        goToURL(EditInstituteView.ROUTE + "/new");
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURLandWaitForRedirect(EditInstituteView.ROUTE + "/new", PuslProperties.ROOT_ROUTE);
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURLandWaitForRedirect(EditInstituteView.ROUTE + "/new", PuslProperties.ROOT_ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURLandWaitForRedirect(EditInstituteView.ROUTE + "/new", PuslProperties.ROOT_ROUTE);
    }
}