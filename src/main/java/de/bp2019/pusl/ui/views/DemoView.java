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

        private static final Logger LOGGER = LoggerFactory.getLogger(DemoView.class);

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
                        instituteRepository.save(new Institute("Bahntechnik"));
                        instituteRepository.save(new Institute("Straßenwesen"));
                        instituteRepository.save(new Institute("Computergrafik"));

                        List<Institute> institutes = instituteRepository.findAll();

                        Set<Institute> instituteSet1 = new HashSet<>();
                        instituteSet1.add(institutes.get(0));
                        instituteSet1.add(institutes.get(2));

                        Set<Institute> instituteSet2 = new HashSet<>();
                        instituteSet2.add(institutes.get(1));

                        Set<Institute> instituteSet3 = new HashSet<>();
                        instituteSet3.add(institutes.get(1));
                        instituteSet3.add(institutes.get(0));

                        String password = passwordEncoder.encode("password");
                        String adminPassword = passwordEncoder.encode("admin");

                        userRepository.save(new User(null, null, "admin", adminPassword, new HashSet<Institute>(),
                                        UserType.SUPERADMIN));
                        userRepository.save(new User("Walter", "Frosch", "wf@test.de", password, instituteSet3,
                                        UserType.WIMI));
                        userRepository.save(new User("Peter", "Pan", "pp@tu-darmstadt.de", password, instituteSet1,
                                        UserType.WIMI));
                        userRepository.save(new User("Angela", "Merkel", "angie.m@deutschland.com", password,
                                        instituteSet3, UserType.WIMI));
                        userRepository.save(new User("John", "Lennon", "johnny@aol.de", password, instituteSet1,
                                        UserType.WIMI));
                        userRepository.save(new User("Helene", "Fischer", "helene@yahoo.com", password, instituteSet1,
                                        UserType.WIMI));
                        userRepository.save(new User("Walter", "Gropius", "wg@walter.de", password, instituteSet2,
                                        UserType.WIMI));

                        List<User> users = userRepository.findAll();

                        Set<User> userSet1 = new HashSet<>();
                        userSet1.add(users.get(3));
                        userSet1.add(users.get(5));
                        userSet1.add(users.get(0));
                        userSet1.add(users.get(1));

                        Set<User> userSet2 = new HashSet<>();
                        userSet2.add(users.get(2));
                        userSet2.add(users.get(4));

                        Set<User> userSet3 = new HashSet<>();
                        userSet3.add(users.get(1));
                        userSet3.add(users.get(5));
                        userSet3.add(users.get(0));

                        Set<Token> tokenSet1 = new HashSet<>();
                        tokenSet1.add(new Token("wiedervorlage", false));
                        tokenSet1.add(new Token("ausgegeben", true));
                        tokenSet1.add(new Token("abgegeben", false));

                        exerciseSchemeRepository.save(new ExerciseScheme("Testat", false, false, "5", tokenSet1,
                                        instituteSet3, userSet2));
                        exerciseSchemeRepository.save(new ExerciseScheme("Übung", true, false, "1", tokenSet1,
                                        instituteSet1, userSet1));
                        exerciseSchemeRepository.save(new ExerciseScheme("Klausur", true, true, "test", tokenSet1,
                                        instituteSet2, userSet3));

                        List<ExerciseScheme> exerciseSchemes = exerciseSchemeRepository.findAll();

                        List<Exercise> exerciseList = Arrays.asList(
                                        new Exercise("Übung 1", exerciseSchemes.get(1), true),
                                        new Exercise("1. Testat", exerciseSchemes.get(0), true),
                                        new Exercise("2. Testat", exerciseSchemes.get(0), true),
                                        new Exercise("Klausur", exerciseSchemes.get(2), false));

                        lectureRepository.save(new Lecture("Einführung in den Compilerbau", instituteSet1, userSet1,
                                        exerciseList, null));

                        exerciseList = Arrays.asList(new Exercise("1. Übung", exerciseSchemes.get(1), true),
                                        new Exercise("2.Übung", exerciseSchemes.get(1), true),
                                        new Exercise("Zwischenprüfung", exerciseSchemes.get(2), true),
                                        new Exercise("3.Übung", exerciseSchemes.get(1), false),
                                        new Exercise("4.Übung", exerciseSchemes.get(1), false),
                                        new Exercise("Klausur", exerciseSchemes.get(2), false));

                        lectureRepository
                                        .save(new Lecture("Mathematik I", instituteSet2, userSet2, exerciseList, null));

                        exerciseList = Arrays.asList(new Exercise("Testat 1", exerciseSchemes.get(0), false),
                                        new Exercise("Testat 2", exerciseSchemes.get(0), true),
                                        new Exercise("Testat 3", exerciseSchemes.get(0), true),
                                        new Exercise("Klausur", exerciseSchemes.get(2), false));
                        lectureRepository.save(new Lecture("Visuelle Trendanalyse", instituteSet3, userSet3,
                                        exerciseList, null));

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