package de.bp2019.pusl.ui.views;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
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
import de.bp2019.pusl.service.AuthenticationService;
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
        private AuthenticationService authenticationService;
        private PasswordEncoder passwordEncoder;

        private IntegerField numGradesField;
        private IntegerField numUsersField;

        public DatabaseView() {
                super("Datenbank");

                instituteRepository = Service.get(InstituteRepository.class);
                userRepository = Service.get(UserRepository.class);
                exerciseSchemeRepository = Service.get(ExerciseSchemeRepository.class);
                lectureRepository = Service.get(LectureRepository.class);
                gradeRepository = Service.get(GradeRepository.class);
                authenticationService = Service.get(AuthenticationService.class);
                passwordEncoder = Service.get(PasswordEncoder.class);

                FormLayout layout = new FormLayout();
                layout.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));
                layout.setWidth("100%");

                Button refillDatabaseButton = new Button("Institute, Veranstaltungen & Übungsschemas generieren");
                layout.add(refillDatabaseButton, 2);

                numUsersField = new IntegerField();
                numUsersField.setId("numUsers");
                numUsersField.setPlaceholder("Anzahl Nutzer");
                layout.add(numUsersField, 1);

                Button generateUsersButton = new Button("Nutzer generieren");
                layout.add(generateUsersButton, 1);

                numGradesField = new IntegerField();
                numGradesField.setId("numGrades");
                numGradesField.setPlaceholder("Anzahl Noten");
                layout.add(numGradesField, 1);

                Button generateGradesButton = new Button("Noten generieren");
                layout.add(generateGradesButton, 1);

                add(layout);

                /* ############ Listeners ########## */

                refillDatabaseButton.addClickListener(event -> refillDatabase());
                generateGradesButton.addClickListener(event -> refillGrades());
                generateUsersButton.addClickListener(event -> refillUsers());
        }

        private void refillDatabase() {

                LOGGER.info("deleting all Database entries");
                instituteRepository.deleteAll();
                exerciseSchemeRepository.deleteAll();
                lectureRepository.deleteAll();

                LOGGER.info("refilling Database...");

                Institute verkehrsplanung = new Institute("Verkehrsplanung");
                instituteRepository.save(verkehrsplanung);

                Institute bahnsysteme = new Institute("Bahnsysteme");
                instituteRepository.save(bahnsysteme);

                Institute strassenwesen = new Institute("Straßenwesen");
                instituteRepository.save(strassenwesen);

                Institute luftverkehr = new Institute("Luftverkehr");
                instituteRepository.save(luftverkehr);

                Set<Token> uebungTokens = new HashSet<>();
                Token defaultTokenUebung = new Token("O", true);
                uebungTokens.add(defaultTokenUebung);
                uebungTokens.add(new Token("T", true));
                uebungTokens.add(new Token("J", true));
                uebungTokens.add(new Token("N", true));

                ExerciseScheme uebung = new ExerciseScheme();
                uebung.setName("Hausübung");
                uebung.setDefaultValueToken(defaultTokenUebung);
                uebung.setIsNumeric(false);
                uebung.setTokens(uebungTokens);
                uebung.setInstitutes(Set.of(verkehrsplanung, strassenwesen, bahnsysteme, luftverkehr));
                exerciseSchemeRepository.save(uebung);

                Set<Token> exkursionTokens = new HashSet<>();
                Token defaultTokenExkursion = new Token("O", true);
                exkursionTokens.add(defaultTokenExkursion);
                exkursionTokens.add(new Token("J", true));

                ExerciseScheme exkursion = new ExerciseScheme();
                exkursion.setName("Exkursion");
                exkursion.setDefaultValueToken(defaultTokenExkursion);
                exkursion.setIsNumeric(false);
                exkursion.setTokens(exkursionTokens);
                exkursion.setInstitutes(Set.of(verkehrsplanung, strassenwesen, bahnsysteme, luftverkehr));
                exerciseSchemeRepository.save(exkursion);

                ExerciseScheme klausur = new ExerciseScheme();
                klausur.setName("Klausur");
                klausur.setDefaultValueNumeric(5.0);
                klausur.setIsNumeric(true);
                klausur.setInstitutes(Set.of(verkehrsplanung, strassenwesen, bahnsysteme, luftverkehr));
                exerciseSchemeRepository.save(klausur);

                PerformanceScheme pruefungsLeistungVerkehr = new PerformanceScheme();
                pruefungsLeistungVerkehr.setName("Prüfungsleistung");
                String plVerkehrCR = "function calculate(results) { \n";
                plVerkehrCR += "    return results[6];\n";
                plVerkehrCR += "}";
                pruefungsLeistungVerkehr.setCalculationRule(plVerkehrCR);

                PerformanceScheme studienLeistungVerkehr = new PerformanceScheme();
                studienLeistungVerkehr.setName("Studienleistung");
                String slVerkehrCR = "function calculate(results) { \n";
                slVerkehrCR += "for(var i = 0; i < 5; i++){\n";
                slVerkehrCR += "        if(results[i] != 'J' && results[i] != 'T'){\n";
                slVerkehrCR += "                return 'nicht bestanden';\n";
                slVerkehrCR += "        }\n";
                slVerkehrCR += "}\n";
                slVerkehrCR += "return 'bestanden'\n";
                slVerkehrCR += "}";
                studienLeistungVerkehr.setCalculationRule(slVerkehrCR);

                PerformanceScheme bonusPunkteVerkehr = new PerformanceScheme();
                bonusPunkteVerkehr.setName("Bonuspunkte");
                String bpVerkehrCr = "function calculate(results) { \n";
                bpVerkehrCr += "var count = 0;\n";
                bpVerkehrCr += "for(var i = 0; i < 5; i++){\n";
                bpVerkehrCr += "        if(results[i] != 'J' && results[i] != 'T'){\n";
                bpVerkehrCr += "                return 0;\n";
                bpVerkehrCr += "        }\n";
                bpVerkehrCr += "        if(results[i] == 'T'){\n";
                bpVerkehrCr += "            count++;\n";
                bpVerkehrCr += "        }\n";
                bpVerkehrCr += "}\n";
                bpVerkehrCr += "return count;\n";
                bpVerkehrCr += "}";
                bonusPunkteVerkehr.setCalculationRule(bpVerkehrCr);

                List<Exercise> exercisesVerkehrI = Arrays.asList(new Exercise("Hausübung 1-1", uebung, true),
                                new Exercise("Hausübung 1-2", uebung, true),
                                new Exercise("Hausübung 1-3", uebung, true),
                                new Exercise("Hausübung 1-4", uebung, true), 
                                new Exercise("Hausübung 1-5", uebung, true),
                                new Exercise("Exkursion", exkursion, false),
                                new Exercise("Klausur", klausur, false));

                Lecture verkehrI = new Lecture();
                verkehrI.setName("Verkehr I");
                verkehrI.setInstitutes(Set.of(verkehrsplanung));
                verkehrI.setExercises(exercisesVerkehrI);
                verkehrI.setPerformanceSchemes(List.of(pruefungsLeistungVerkehr, studienLeistungVerkehr, bonusPunkteVerkehr));
                lectureRepository.save(verkehrI);

                List<Exercise> exercisesVerkehrII = Arrays.asList(new Exercise("Hausübung 2-1", uebung, true),
                                new Exercise("Hausübung 2-2", uebung, true),
                                new Exercise("Hausübung 2-3", uebung, true),
                                new Exercise("Hausübung 2-4", uebung, true),
                                new Exercise("Hausübung 2-5", uebung, true),
                                new Exercise("Hausübung 2-6", uebung, true), new Exercise("Klausur", klausur, false));

                Lecture verkehrII = new Lecture();
                verkehrII.setName("Verkehr II");
                verkehrII.setInstitutes(Set.of(verkehrsplanung));
                verkehrII.setExercises(exercisesVerkehrII);
                verkehrII.setPerformanceSchemes(List.of(pruefungsLeistungVerkehr, studienLeistungVerkehr, bonusPunkteVerkehr));
                lectureRepository.save(verkehrII);

                /* ########## BAHN ########## */

                PerformanceScheme bahnBpruefungsleistung = new PerformanceScheme();
                bahnBpruefungsleistung.setName("Prüfungsleistung");
                String bahnBPlCr = "function calculate(results) { \n";
                bahnBPlCr += "    return results[2];\n";
                bahnBPlCr += "}";
                bahnBpruefungsleistung.setCalculationRule(bahnBPlCr);

                PerformanceScheme bahnBbonuspunkte = new PerformanceScheme();
                bahnBbonuspunkte.setName("Prüfungsleistung");
                String bahnBBpCr = "function calculate(results) { \n";
                bahnBBpCr += "    return 'platzhalter';\n";
                bahnBBpCr += "}";
                bahnBbonuspunkte.setCalculationRule(bahnBBpCr);

                List<Exercise> exercisesBahnB = Arrays.asList(new Exercise("Entwurf", uebung, true),
                                new Exercise("Abgabekolloquium", uebung, true),
                                new Exercise("Klausur", klausur, false));

                Lecture bahnB = new Lecture();
                bahnB.setName("Bahn B");
                bahnB.setInstitutes(Set.of(bahnsysteme));
                bahnB.setExercises(exercisesBahnB);
                bahnB.setPerformanceSchemes(List.of(bahnBpruefungsleistung, bahnBbonuspunkte));
                lectureRepository.save(bahnB);

                PerformanceScheme ebwPerformanceScheme = new PerformanceScheme();
                ebwPerformanceScheme.setName("Prüfungsleistung");
                String ebwCalculationRule = "function calculate(results) { \n";
                ebwCalculationRule += "    return results[0];\n";
                ebwCalculationRule += "}";
                ebwPerformanceScheme.setCalculationRule(ebwCalculationRule);

                Lecture ebwI = new Lecture();
                ebwI.setName("EBW I");
                ebwI.setInstitutes(Set.of(bahnsysteme));
                ebwI.setExercises(Arrays.asList(new Exercise("mdl. Klausur", klausur, false)));
                ebwI.setPerformanceSchemes(Arrays.asList(ebwPerformanceScheme));
                lectureRepository.save(ebwI);

                Lecture ebwII = new Lecture();
                ebwII.setName("EBW II");
                ebwII.setInstitutes(Set.of(bahnsysteme));
                ebwII.setExercises(Arrays.asList(new Exercise("mdl. Klausur", klausur, false)));
                ebwII.setPerformanceSchemes(Arrays.asList(ebwPerformanceScheme));
                lectureRepository.save(ebwII);

                LOGGER.info("refilling Database done.");
        }

        private void refillUsers() {
                User currentUser = authenticationService.currentUser();
                userRepository.deleteAll();
                userRepository.save(currentUser);

                Integer numUsers = numUsersField.getValue();

                if (numUsers == null || numUsers == 0) {
                        return;
                }

                for (Institute institute : instituteRepository.findAll()) {
                        Set<Institute> instituteSet = new HashSet<>();
                        instituteSet.add(institute);

                        String password = passwordEncoder.encode("password");

                        User user = new User();
                        user.setType(UserType.ADMIN);
                        user.setEmailAddress("admin@" + institute.getName().toLowerCase() + ".de");
                        user.setFirstName("admin");
                        user.setLastName(institute.getName());
                        user.setPassword(password);
                        user.setInstitutes(instituteSet);
                        userRepository.save(user);

                        for (int i = 1; i <= numUsers; i++) {
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
        }

        private void refillGrades() {
                Integer numGrades = numGradesField.getValue();
                gradeRepository.deleteAll();

                List<User> users = userRepository.findAll();
                List<Lecture> lectures = lectureRepository.findAll();

                int currentGrade = 0;
                int matrInt = 1524750;

                if (lectures.size() == 0 || numGrades == null) {
                        return;
                }

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
                                        if (exercise.getScheme().isNumeric()) {
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