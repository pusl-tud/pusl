package de.bp2019.pusl.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import de.bp2019.pusl.ui.interfaces.AccessibleByAdmin;
import de.bp2019.pusl.ui.views.WorkView;
import de.bp2019.pusl.ui.views.user.EditUserView;

@SpringBootTest
public class ClassUtilsIT {

    @Test
    public void testImplementsInterface() {
        assertTrue(ClassUtils.implementsInterface(EditUserView.class, AccessibleByAdmin.class));
        assertFalse(ClassUtils.implementsInterface(WorkView.class, AccessibleByAdmin.class));
    }
}