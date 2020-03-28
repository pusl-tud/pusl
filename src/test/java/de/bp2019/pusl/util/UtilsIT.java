package de.bp2019.pusl.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import de.bp2019.pusl.ui.interfaces.AccessibleByAdmin;
import de.bp2019.pusl.ui.views.WorkView;
import de.bp2019.pusl.ui.views.user.EditUserView;

/**
 * @author Leon Chemnitz
 */
@SpringBootTest
public class UtilsIT {

    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testImplementsInterface() {
        assertTrue(Utils.implementsInterface(EditUserView.class, AccessibleByAdmin.class));
        assertFalse(Utils.implementsInterface(WorkView.class, AccessibleByAdmin.class));
    }
}