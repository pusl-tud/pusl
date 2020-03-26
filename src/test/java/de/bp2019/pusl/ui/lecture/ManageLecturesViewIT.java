package de.bp2019.pusl.ui.lecture;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.ui.views.lecture.EditLectureView;
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
     */
    private Lecture addLecture() {
        Lecture lecture = new Lecture();
        lecture.setName(RandomStringUtils.randomAlphanumeric(12));
        lectureRepository.save(lecture);

        return lecture;
    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testNewLectureButton() throws Exception {
        LOGGER.info("Testing new Lecture button");
        login(UserType.SUPERADMIN);

        goToURL(ManageLecturesView.ROUTE);

        findButtonContainingText("Neue Veranstaltung").click();

        waitForURL(EditLectureView.ROUTE + "/new");
    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testNameButton() throws Exception {
        LOGGER.info("Testing Lecture name button");
        login(UserType.SUPERADMIN);

        Lecture lecture = addLecture();

        goToURL(ManageLecturesView.ROUTE);

        String name = lecture.getName();
        ObjectId id = lecture.getId();

        findButtonContainingText(name).click();

        waitForURL(EditLectureView.ROUTE + "/" + id.toString());
    }

    /**
     * @author Luca Dinies
     * @throws Exception
     */
    @Test
    public void testDeleteButton() throws Exception {
        LOGGER.info("Testing Delete ExerciseScheme Button");
        login(UserType.SUPERADMIN);

        Lecture lecture = addLecture();

        goToURL(ManageLecturesView.ROUTE);

        String name = lecture.getName();
        ObjectId id = lecture.getId();

        /* click delete button */
        findElementById("delete-" + id.toString()).click();

        acceptConfirmDeletionDialog(name);

        assertTrue(lectureRepository.findById(id.toString()).isEmpty());
    }

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
        goToURLandWaitForRedirect(ManageLecturesView.ROUTE, PuslProperties.ROOT_ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURLandWaitForRedirect(ManageLecturesView.ROUTE, PuslProperties.ROOT_ROUTE);
    }
}