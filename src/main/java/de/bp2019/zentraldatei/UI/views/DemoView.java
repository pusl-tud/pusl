package de.bp2019.zentraldatei.UI.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import de.bp2019.zentraldatei.model.exercise.ExerciseInstance;
import de.bp2019.zentraldatei.model.exercise.ExerciseScheme;
import de.bp2019.zentraldatei.model.exercise.Grade;
import de.bp2019.zentraldatei.model.exercise.Handout;
import de.bp2019.zentraldatei.model.exercise.Token;
import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.module.Module;
import de.bp2019.zentraldatei.model.User;
import de.bp2019.zentraldatei.repository.ExerciseInstanceRepository;
import de.bp2019.zentraldatei.repository.ExerciseSchemeRepository;
import de.bp2019.zentraldatei.repository.InstituteRepository;
import de.bp2019.zentraldatei.repository.ModuleRepository;
import de.bp2019.zentraldatei.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                        ExerciseInstanceRepository exerciseInstanceRepository) {

                add(new Text("Befülle die Datenbank mit Testdaten!"));

                LOGGER.info("deleting all Database entries");
                instituteRepository.deleteAll();
                userRepository.deleteAll();
                exerciseSchemeRepository.deleteAll();
                exerciseInstanceRepository.deleteAll();
                moduleRepository.deleteAll();

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

                List<Grade> gradeList1 = new ArrayList<>();
                gradeList1.add(new Grade(239876883, "5", LocalDateTime.now()));
                gradeList1.add(new Grade(239236883, "3", LocalDateTime.now()));
                gradeList1.add(new Grade(319384003, "2", LocalDateTime.now()));

                List<Handout> handoutList1 = new ArrayList<>();
                handoutList1.add(new Handout(LocalDateTime.now(), LocalDateTime.now(), 239236883));
                handoutList1.add(new Handout(LocalDateTime.now(), LocalDateTime.now(), 173847983));
                handoutList1.add(new Handout(LocalDateTime.now(), LocalDateTime.now(), 902384827));

                exerciseInstanceRepository.save(new ExerciseInstance(exerciseSchemes.get(0), gradeList1, handoutList1));
                exerciseInstanceRepository.save(new ExerciseInstance(exerciseSchemes.get(1), gradeList1, handoutList1));
                exerciseInstanceRepository.save(new ExerciseInstance(exerciseSchemes.get(2), gradeList1, handoutList1));

                List<ExerciseInstance> exerciseInstances = exerciseInstanceRepository.findAll();

                List<ExerciseInstance> exerciseInstanceList1 = new ArrayList<>();
                exerciseInstanceList1.add(exerciseInstances.get(1));
                exerciseInstanceList1.add(exerciseInstances.get(0));
                exerciseInstanceList1.add(exerciseInstances.get(0));
                exerciseInstanceList1.add(exerciseInstances.get(0));
                exerciseInstanceList1.add(exerciseInstances.get(2));

                String berechnungsRegel = "function calcuate(results) { \n";
                berechnungsRegel += "    //ziemlich komplizierte Berechnungsregel... \n";
                berechnungsRegel += "    return ergebnis;\n";
                berechnungsRegel += "}";

                moduleRepository.save(new Module("Einführung in den Compilerbau", instituteSet1, userSet1,
                                exerciseInstanceList1, berechnungsRegel));

                moduleRepository.save(new Module("Mathematik I", instituteSet2, userSet2, exerciseInstanceList1,
                                berechnungsRegel));

                moduleRepository.save(new Module("Visuelle Trendanalyse", instituteSet3, userSet3,
                                exerciseInstanceList1, berechnungsRegel));

                LOGGER.info("refilling Database done.");
        }

}