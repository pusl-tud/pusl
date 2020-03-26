package de.bp2019.pusl.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.config.BaseUITest;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.PerformanceScheme;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.LectureRepository;
import de.bp2019.pusl.ui.views.ExportView;

/**
 * UI test for {@link de.bp2019.pusl.ui.views.ExportView}
 *
 * @author Luca Dinies
 */
public class ExportViewIT extends BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportViewIT.class);

    @Autowired
    InstituteRepository instituteRepository;

    @Autowired
    LectureRepository lectureRepository;

    /**
     * @author Luca Dinies
     */
    private Lecture addLecture() {
        Institute institute = new Institute(RandomStringUtils.random(8, true, true));
        instituteRepository.save(institute);
        Set<Institute> instituteSet = new HashSet<>();
        instituteSet.add(instituteRepository.findAll().get(0));

        PerformanceScheme performanceScheme1 = new PerformanceScheme("performance1",
                "\"function calculate(results) { \n" +
                        "     \n" +
                        "    return 'nicht definiert';\n" +
                        "}\" ");

        PerformanceScheme performanceScheme2 = new PerformanceScheme("performance2",
                "\"function calculate(results) { \n" +
                        "     \n" +
                        "    return 'definiert';\n" +
                        "}\" ");

        List<PerformanceScheme> performanceSchemes = new ArrayList<>();
        performanceSchemes.add(performanceScheme1);
        performanceSchemes.add(performanceScheme2);

        Lecture lecture = new Lecture(RandomStringUtils.random(8, true, true),
                instituteSet, null, null, performanceSchemes);

        lectureRepository.save(lecture);

        return lecture;
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testAccess() throws Exception {
        LOGGER.info("Testing access");

        LOGGER.info("Testing access as SUPERADMIN");
        login(UserType.SUPERADMIN);
        goToURL(ExportView.ROUTE);
        logout();

        LOGGER.info("Testing access as ADMIN");
        login(UserType.ADMIN);
        goToURL(ExportView.ROUTE);
        logout();

        LOGGER.info("Testing access as WIWI");
        login(UserType.WIMI);
        goToURL(ExportView.ROUTE);
        logout();

        LOGGER.info("Testing access as HIWI");
        login(UserType.HIWI);
        goToURLandWaitForRedirect(ExportView.ROUTE, PuslProperties.ROOT_ROUTE);
    }

    /**
     * @throws Exception
     * @author Luca Dinies
     */
    @Test
    public void testLectureInput() throws Exception {
        LOGGER.info("Testing ExportView");

        Lecture lecture = addLecture();

        login(UserType.SUPERADMIN);
        goToURL(ExportView.ROUTE);

        findSelectByIdAndSelectByText("lecture", lecture.getName());
        findSelectByIdAndSelectByText("performanceScheme", lecture.getPerformanceSchemes().get(1).getName());

        // findButtonContainingText("Download Excel").click();

    }
}