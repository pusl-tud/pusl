package de.bp2019.pusl.ui.views;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.PerformanceScheme;
import de.bp2019.pusl.model.Token;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.ExerciseSchemeRepository;
import de.bp2019.pusl.repository.GradeRepository;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.LectureRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.interfaces.AccessibleBySuperadmin;

/**
 * Demo View currently just empties and refills the database.
 * 
 * @author Leon Chemnitz
 */
@Route(value = DemoView.ROUTE, layout = MainAppView.class)
public class DemoView extends BaseView implements AccessibleBySuperadmin {

        private static final long serialVersionUID = 1240260329860093364L;

        public static final String ROUTE = "admin/demo";

        @Autowired
        public DemoView(InstituteRepository instituteRepository, UserRepository userRepository,
                        ExerciseSchemeRepository exerciseSchemeRepository, LectureRepository lectureRepository,
                        GradeRepository gradeRepository, PasswordEncoder passwordEncoder) {
                super("Demo");

                Button refillDatabaseButton = new Button("Datenbank neu befüllen");

                add(refillDatabaseButton);

                refillDatabaseButton.addClickListener(event -> {

                        LOGGER.info("deleting all Database entries");
                        instituteRepository.deleteAll();
                        userRepository.deleteAll();
                        exerciseSchemeRepository.deleteAll();
                        gradeRepository.deleteAll();
                        lectureRepository.deleteAll();

                        LOGGER.info("refilling Database...");
                        instituteRepository.save(new Institute("Verkehrsplanung"));
                        instituteRepository.save(new Institute("Bahnsysteme"));
                        instituteRepository.save(new Institute("Straßenwesen"));
                        instituteRepository.save(new Institute("Luftverkehr"));

                        List<Institute> institutes = instituteRepository.findAll();

                        String password = passwordEncoder.encode("password");
                        String adminPassword = passwordEncoder.encode("admin");

                        userRepository.save(new User(null, null, "admin", adminPassword, new HashSet<Institute>(),
                                        UserType.SUPERADMIN));

                        User user;
                        Set<Institute> instituteSet;

                        for (Institute institute : institutes) {
                                instituteSet = new HashSet<>();
                                instituteSet.add(institute);

                                user = new User();
                                user.setType(UserType.ADMIN);
                                user.setEmailAddress("admin@" + institute.getName().toLowerCase() + ".de");
                                user.setFirstName("admin");
                                user.setLastName(institute.getName());
                                user.setPassword(password);
                                user.setInstitutes(instituteSet);
                                userRepository.save(user);

                                for (int i = 1; i <= 3; i++) {
                                        user = new User();
                                        user.setType(UserType.HIWI);
                                        user.setEmailAddress(
                                                        "hiwi" + i + "@" + institute.getName().toLowerCase() + ".de");
                                        user.setFirstName("hiwi" + i);
                                        user.setLastName(institute.getName());
                                        user.setPassword(password);
                                        user.setInstitutes(instituteSet);
                                        userRepository.save(user);

                                        user = new User();
                                        user.setType(UserType.WIMI);
                                        user.setEmailAddress(
                                                        "wimi" + i + "@" + institute.getName().toLowerCase() + ".de");
                                        user.setFirstName("wimi" + i);
                                        user.setLastName(institute.getName());
                                        user.setPassword(password);
                                        user.setInstitutes(instituteSet);
                                        userRepository.save(user);
                                }
                        }

                        List<User> users = userRepository.findAll();

                        Set<Token> tokenSet1;
                        ExerciseScheme exerciseScheme;
                        Lecture lecture;

                        tokenSet1 = new HashSet<>();
                        tokenSet1.add(new Token("O", true));
                        tokenSet1.add(new Token("T", true));
                        tokenSet1.add(new Token("J", true));
                        tokenSet1.add(new Token("N", true));

                        exerciseScheme = new ExerciseScheme();
                        exerciseScheme.setName("Hausübung");
                        exerciseScheme.setDefaultValue("O");
                        exerciseScheme.setIsNumeric(false);
                        exerciseScheme.setTokens(tokenSet1);
                        exerciseScheme.setInstitutes(Set.of(institutes.get(0), institutes.get(1)));
                        exerciseSchemeRepository.save(exerciseScheme);

                        tokenSet1 = new HashSet<>();
                        tokenSet1.add(new Token("O", true));
                        tokenSet1.add(new Token("J", true));

                        exerciseScheme = new ExerciseScheme();
                        exerciseScheme.setName("Exkursion");
                        exerciseScheme.setDefaultValue("O");
                        exerciseScheme.setIsNumeric(false);
                        exerciseScheme.setTokens(tokenSet1);
                        exerciseScheme.setInstitutes(Set.of(institutes.get(0)));
                        exerciseSchemeRepository.save(exerciseScheme);

                        exerciseScheme = new ExerciseScheme();
                        exerciseScheme.setName("Klausur");
                        exerciseScheme.setDefaultValue("5.0");
                        exerciseScheme.setIsNumeric(true);
                        exerciseScheme.setInstitutes(Set.of(institutes.get(0)));
                        exerciseSchemeRepository.save(exerciseScheme);

                        List<ExerciseScheme> exerciseSchemes = exerciseSchemeRepository.findAll();

                        List<Exercise> exerciseList = Arrays.asList(
                                        new Exercise("Übung 1", exerciseSchemes.get(0), true),
                                        new Exercise("Übung 2", exerciseSchemes.get(0), true),
                                        new Exercise("Übung 3", exerciseSchemes.get(0), true),
                                        new Exercise("Übung 4", exerciseSchemes.get(0), true),
                                        new Exercise("Übung 5", exerciseSchemes.get(0), true),
                                        new Exercise("Exkursion", exerciseSchemes.get(1), false),
                                        new Exercise("Klausur", exerciseSchemes.get(2), false));

                        String defaultValue = "function calcuate(results) { \n";
                        defaultValue += "     \n";
                        defaultValue += "    return ergebnis;\n";
                        defaultValue += "}";

                        PerformanceScheme pruefungsLeistung = new PerformanceScheme();
                        pruefungsLeistung.setName("Prüfungsleistung");
                        pruefungsLeistung.setCalculationRule(defaultValue);

                        PerformanceScheme studienLeistung = new PerformanceScheme();
                        studienLeistung.setName("Studienleistung");
                        studienLeistung.setCalculationRule(defaultValue);

                        lecture = new Lecture();
                        lecture.setName("Verkehr I");
                        lecture.setInstitutes(Set.of(institutes.get(0)));
                        lecture.setExercises(exerciseList);
                        lecture.setPerformanceSchemes(List.of(pruefungsLeistung, studienLeistung));
                        lectureRepository.save(lecture);

                        exerciseList = Arrays.asList(
                                        new Exercise("Übung 1", exerciseSchemes.get(0), true),
                                        new Exercise("Übung 2", exerciseSchemes.get(0), true),
                                        new Exercise("Übung 3", exerciseSchemes.get(0), true),
                                        new Exercise("Übung 4", exerciseSchemes.get(0), true),
                                        new Exercise("Übung 5", exerciseSchemes.get(0), true),
                                        new Exercise("Übung 6", exerciseSchemes.get(0), true),
                                        new Exercise("Klausur", exerciseSchemes.get(2), false));

                        lecture = new Lecture();
                        lecture.setName("Verkehr II");
                        lecture.setInstitutes(Set.of(institutes.get(0)));
                        lecture.setExercises(exerciseList);
                        lecture.setPerformanceSchemes(List.of(pruefungsLeistung, studienLeistung));
                        lectureRepository.save(lecture);


                        exerciseList = Arrays.asList(
                                        new Exercise("Übung 1", exerciseSchemes.get(0), true),
                                        new Exercise("Übung 2", exerciseSchemes.get(0), true),
                                        new Exercise("Übung 3", exerciseSchemes.get(0), true),
                                        new Exercise("Übung 4", exerciseSchemes.get(0), true),
                                        new Exercise("Übung 6", exerciseSchemes.get(0), true),
                                        new Exercise("Klausur", exerciseSchemes.get(2), false));

                        lecture = new Lecture();
                        lecture.setName("Bahn B");
                        lecture.setInstitutes(Set.of(institutes.get(1)));
                        lecture.setExercises(exerciseList);
                        lecture.setPerformanceSchemes(List.of(pruefungsLeistung, studienLeistung));
                        lectureRepository.save(lecture);

                        List<Lecture> lectures = lectureRepository.findAll();

                        Grade grade = new Grade(lectures.get(0), lectures.get(0).getExercises().get(0), "17762563",
                                        "2,4", LocalDate.of(2019, 12, 24));
                        gradeRepository.save(grade);

                        grade = new Grade(lectures.get(1), lectures.get(1).getExercises().get(1), "17793563", "1,3",
                                        LocalDate.of(2019, 11, 20));
                        gradeRepository.save(grade);

                        grade = new Grade(lectures.get(0), lectures.get(0).getExercises().get(2), "17762563", "2,4",
                                        LocalDate.of(2020, 01, 13));
                        gradeRepository.save(grade);

                        grade = new Grade(lectures.get(2), lectures.get(2).getExercises().get(0), "28362563", "4,4",
                                        LocalDate.of(2019, 11, 29));
                        gradeRepository.save(grade);

                        grade = new Grade(lectures.get(0), lectures.get(0).getExercises().get(2), "17762563", "1.0",
                                        LocalDate.of(2019, 12, 03));
                        gradeRepository.save(grade);

                        LOGGER.info("refilling Database done.");
                });
        }

}