package de.bp2019.zentraldatei.UI.views;

import java.util.*;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import de.bp2019.zentraldatei.model.exercise.Grade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.User;
import de.bp2019.zentraldatei.model.exercise.Exercise;
import de.bp2019.zentraldatei.model.exercise.ExerciseScheme;
import de.bp2019.zentraldatei.model.exercise.Token;
import de.bp2019.zentraldatei.model.module.Module;
import de.bp2019.zentraldatei.repository.ExerciseSchemeRepository;
import de.bp2019.zentraldatei.repository.GradeRepository;
import de.bp2019.zentraldatei.repository.HandoutRepository;
import de.bp2019.zentraldatei.repository.InstituteRepository;
import de.bp2019.zentraldatei.repository.ModuleRepository;
import de.bp2019.zentraldatei.repository.UserRepository;

/**
 * Demo View currently just empties and refills the database.
 * 
 * @author Leon Chemnitz
 */
@Route(value = DemoView.ROUTE, layout = MainAppView.class)
public class DemoView extends VerticalLayout {

        private static final long serialVersionUID = 1240260329860093364L;

        public static final String ROUTE = "demo";

        private static final Logger LOGGER = LoggerFactory.getLogger(DemoView.class);

        @Autowired
        public DemoView(InstituteRepository instituteRepository, UserRepository userRepository,
                        ExerciseSchemeRepository exerciseSchemeRepository, ModuleRepository moduleRepository,
                        GradeRepository gradeRepository, HandoutRepository handoutRepository) {

                add(new Text("Befülle die Datenbank mit Testdaten!"));

                LOGGER.info("deleting all Database entries");
                instituteRepository.deleteAll();
                userRepository.deleteAll();
                exerciseSchemeRepository.deleteAll();
                gradeRepository.deleteAll();
                moduleRepository.deleteAll();
                handoutRepository.deleteAll();

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

                userRepository.save(new User("Walter", "Frosch", null, null, instituteSet3, null));
                userRepository.save(new User("Peter", "Pan", null, null, instituteSet1, null));
                userRepository.save(new User("Angela", "Merkel", null, null, instituteSet3, null));
                userRepository.save(new User("John", "Lennon", null, null, instituteSet1, null));
                userRepository.save(new User("Helene", "Fischer", null, null, instituteSet1, null));
                userRepository.save(new User("Walter", "Gropius", null, null, instituteSet2, null));

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

                exerciseSchemeRepository.save(
                                new ExerciseScheme("Testat", false, false, "5", tokenSet1, instituteSet3, userSet2));
                exerciseSchemeRepository.save(
                                new ExerciseScheme("Übung", true, false, "1", tokenSet1, instituteSet1, userSet1));
                exerciseSchemeRepository.save(
                                new ExerciseScheme("Klausur", true, true, "test", tokenSet1, instituteSet2, userSet3));

                List<ExerciseScheme> exerciseSchemes = exerciseSchemeRepository.findAll();

                String berechnungsRegel = "function calcuate(results) { \n";
                berechnungsRegel += "    //ziemlich komplizierte Berechnungsregel... \n";
                berechnungsRegel += "    return ergebnis;\n";
                berechnungsRegel += "}";

                List<Exercise> exerciseList = Arrays.asList(new Exercise("Übung 1", exerciseSchemes.get(1), true),
                                new Exercise("1. Testat", exerciseSchemes.get(0), true),
                                new Exercise("2. Testat", exerciseSchemes.get(0), true),
                                new Exercise("Klausur", exerciseSchemes.get(2), false));

                moduleRepository.save(new Module("Einführung in den Compilerbau", instituteSet1, userSet1, exerciseList,
                                berechnungsRegel));

                exerciseList = Arrays.asList(new Exercise("1. Übung", exerciseSchemes.get(1), true),
                                new Exercise("2.Übung", exerciseSchemes.get(1), true),
                                new Exercise("Zwischenprüfung", exerciseSchemes.get(2), true),
                                new Exercise("3.Übung", exerciseSchemes.get(1), false),
                                new Exercise("4.Übung", exerciseSchemes.get(1), false),
                                new Exercise("Klausur", exerciseSchemes.get(2),false));

                moduleRepository.save(
                                new Module("Mathematik I", instituteSet2, userSet2, exerciseList, berechnungsRegel));

                exerciseList = Arrays.asList(new Exercise("Testat 1", exerciseSchemes.get(0), false),
                                new Exercise("Testat 2", exerciseSchemes.get(0),true),
                                new Exercise("Testat 3", exerciseSchemes.get(0),true),
                                new Exercise("Klausur", exerciseSchemes.get(2),false));
                moduleRepository.save(new Module("Visuelle Trendanalyse", instituteSet3, userSet3, exerciseList,
                                berechnungsRegel));

                //gradeRepository.save(
                        //new Grade(new Module("Mathematik I", instituteSet2, userSet2, exerciseList, berechnungsRegel), new Exercise("Testat 2", exerciseSchemes.get(0),true),
                                //253642259, "bestanden", new Date().toInstant()));
                        
                LOGGER.info("refilling Database done.");
        }

}