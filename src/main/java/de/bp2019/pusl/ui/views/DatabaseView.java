package de.bp2019.pusl.ui.views;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;

import org.apache.commons.math3.random.RandomDataGenerator;
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
import de.bp2019.pusl.util.Service;
import de.bp2019.pusl.util.Utils;

/**
 * Demo View currently just empties and refills the database.
 * 
 * @author Leon Chemnitz
 */
@Route(value = DatabaseView.ROUTE, layout = MainAppView.class)
public class DatabaseView extends BaseView implements AccessibleBySuperadmin {

        private static final long serialVersionUID = 1240260329860093364L;

        public static final String ROUTE = "admin/database";

        private InstituteRepository instituteRepository;
        private UserRepository userRepository;
        private ExerciseSchemeRepository exerciseSchemeRepository;
        private LectureRepository lectureRepository;
        private GradeRepository gradeRepository;
        private PasswordEncoder passwordEncoder;

        IntegerField numGradesField;

        public DatabaseView() {
                super("Datenbank");

                instituteRepository = Service.get(InstituteRepository.class);
                userRepository = Service.get(UserRepository.class);
                exerciseSchemeRepository = Service.get(ExerciseSchemeRepository.class);
                lectureRepository = Service.get(LectureRepository.class);
                gradeRepository = Service.get(GradeRepository.class);
                passwordEncoder = Service.get(PasswordEncoder.class);

                Button refillDatabaseButton = new Button("Datenbank neu befüllen");
                add(refillDatabaseButton);

                numGradesField = new IntegerField();
            numGradesField.setId("numGrades");
            add(numGradesField);

                Button refillGradesButton = new Button("Noten generieren");
                add(refillGradesButton);

                refillDatabaseButton.addClickListener(event -> {
                        refillDatabase();
                });

                refillGradesButton.addClickListener(event -> {
                        refillGrades();
                });
        }

        private void refillDatabase() {

                LOGGER.info("deleting all Database entries");
                instituteRepository.deleteAll();
                userRepository.deleteAll();
                exerciseSchemeRepository.deleteAll();
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
                                user.setEmailAddress("hiwi" + i + "@" + institute.getName().toLowerCase() + ".de");
                                user.setFirstName("hiwi" + i);
                                user.setLastName(institute.getName());
                                user.setPassword(password);
                                user.setInstitutes(instituteSet);
                                userRepository.save(user);

                                user = new User();
                                user.setType(UserType.WIMI);
                                user.setEmailAddress("wimi" + i + "@" + institute.getName().toLowerCase() + ".de");
                                user.setFirstName("wimi" + i);
                                user.setLastName(institute.getName());
                                user.setPassword(password);
                                user.setInstitutes(instituteSet);
                                userRepository.save(user);
                        }
                }

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

                List<Exercise> exerciseList = Arrays.asList(new Exercise("Übung 1", exerciseSchemes.get(0), true),
                                new Exercise("Übung 2", exerciseSchemes.get(0), true),
                                new Exercise("Übung 3", exerciseSchemes.get(0), true),
                                new Exercise("Übung 4", exerciseSchemes.get(0), true),
                                new Exercise("Übung 5", exerciseSchemes.get(0), true),
                                new Exercise("Exkursion", exerciseSchemes.get(1), false),
                                new Exercise("Klausur", exerciseSchemes.get(2), false));

                String defaultValue = "function calculate(results) { \n";
                defaultValue += "     \n";
                defaultValue += "    return 'nicht definiert';\n";
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

                exerciseList = Arrays.asList(new Exercise("Übung 1", exerciseSchemes.get(0), true),
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

                exerciseList = Arrays.asList(new Exercise("Übung 1", exerciseSchemes.get(0), true),
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

                LOGGER.info("refilling Database done.");
        }

        private void refillGrades() {
                int numGrades = numGradesField.getValue();
                gradeRepository.deleteAll();

                List<User> users = userRepository.findAll();
                List<Lecture> lectures = lectureRepository.findAll();

                int currentGrade = 0;
                int matrInt = 1524750;
                while (currentGrade < numGrades) {

                        while (!Utils.isMatrNumber(matrInt)) {
                                matrInt++;
                        }

                        for (Lecture lecture : lectures) {
                                for (Exercise exercise : lecture.getExercises()) {

                                        int skip = new RandomDataGenerator().nextInt(1, 5);
                                        if (skip == 1 || currentGrade >= numGrades) {
                                                continue;
                                        }

                                        String value;
                                        if (exercise.getScheme().getIsNumeric()) {
                                                int generatedInt = new RandomDataGenerator().nextInt(10, 50);
                                                value = Float.toString(generatedInt / 10.0F);
                                        } else {
                                                List<Token> tokens = exercise.getScheme().getTokens().stream()
                                                                .collect(Collectors.toList());
                                                int index = new RandomDataGenerator().nextInt(0, tokens.size() - 1);

                                                value = tokens.get(index).getName();
                                        }

                                        String matrNumber = Integer.toString(matrInt);

                                        int userIndex = new RandomDataGenerator().nextInt(0, users.size() - 1);
                                        User gradedBy = users.get(userIndex);

                                        LocalDate handIn = Utils.randomDateBetween(LocalDate.now().minusYears(5),
                                                        LocalDate.now());

                                        Grade grade = new Grade();
                                        grade.setGradedBy(gradedBy);
                                        grade.setLecture(lecture);
                                        grade.setExercise(exercise);
                                        grade.setValue(value);
                                        grade.setMatrNumber(matrNumber);
                                        grade.setHandIn(handIn);

                                        gradeRepository.save(grade);
                                        currentGrade++;
                                }
                        }

                        matrInt++;
                }
        }
}