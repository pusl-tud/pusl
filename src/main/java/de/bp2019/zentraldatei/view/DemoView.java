package de.bp2019.zentraldatei.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.bp2019.zentraldatei.model.ExerciseScheme;
import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.ModuleScheme;
import de.bp2019.zentraldatei.model.User;
import de.bp2019.zentraldatei.repository.ExerciseSchemeRepository;
import de.bp2019.zentraldatei.repository.InstituteRepository;
import de.bp2019.zentraldatei.repository.ModuleSchemeRepository;
import de.bp2019.zentraldatei.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Demo View currently just empties and refills the database.
 * 
 * @author Leon Chemnitz
 */
@Route(value = "demo", layout = MainAppView.class)
public class DemoView extends VerticalLayout {

        private static final long serialVersionUID = 1L;
        private static final Logger LOGGER = LoggerFactory.getLogger(DemoView.class);

        public DemoView(@Autowired InstituteRepository instituteRepository, @Autowired UserRepository userRepository,
                        @Autowired ExerciseSchemeRepository exerciseSchemeRepository,
                        @Autowired ModuleSchemeRepository moduleSchemeRepository) {

                add(new Text("Befülle die Datenbank mit Testdaten!"));

                LOGGER.info("deleting all Database entries");
                instituteRepository.deleteAll();
                userRepository.deleteAll();
                exerciseSchemeRepository.deleteAll();
                moduleSchemeRepository.deleteAll();

                LOGGER.info("refilling Database...");
                instituteRepository.save(new Institute("Bahntechnik"));
                instituteRepository.save(new Institute("Straßenwesen"));
                instituteRepository.save(new Institute("Computergrafik"));

                List<Institute> institutes = instituteRepository.findAll();

                Set<String> instituteSet1 = new HashSet<String>();
                instituteSet1.add(institutes.get(0).getId());
                instituteSet1.add(institutes.get(2).getId());

                Set<String> instituteSet2 = new HashSet<String>();
                instituteSet2.add(institutes.get(1).getId());

                Set<String> instituteSet3 = new HashSet<String>();
                instituteSet3.add(institutes.get(1).getId());
                instituteSet3.add(institutes.get(0).getId());
                instituteSet3.add(institutes.get(2).getId());

                userRepository.save(new User("Walter", "Frosch", null, null, instituteSet3, null));
                userRepository.save(new User("Peter", "Pan", null, null, instituteSet1, null));
                userRepository.save(new User("Angela", "Merkel", null, null, instituteSet3, null));
                userRepository.save(new User("John", "Lennon", null, null, instituteSet1, null));
                userRepository.save(new User("Helene", "Fischer", null, null, instituteSet1, null));
                userRepository.save(new User("Walter", "Gropius", null, null, instituteSet2, null));

                List<User> users = userRepository.findAll();

                Set<String> userSet1 = new HashSet<String>();
                userSet1.add(users.get(3).getId());
                userSet1.add(users.get(5).getId());
                userSet1.add(users.get(0).getId());
                userSet1.add(users.get(1).getId());

                Set<String> userSet2 = new HashSet<String>();
                userSet2.add(users.get(2).getId());
                userSet2.add(users.get(4).getId());

                Set<String> userSet3 = new HashSet<String>();
                userSet3.add(users.get(1).getId());
                userSet3.add(users.get(5).getId());
                userSet3.add(users.get(0).getId());

                Set<String> tokenSet1 = new HashSet<String>();
                tokenSet1.add("wiedervorlage");
                tokenSet1.add("ausgegeben");
                tokenSet1.add("abgegeben");

                exerciseSchemeRepository.save(new ExerciseScheme("Testat", false, instituteSet3, tokenSet1, userSet2));
                exerciseSchemeRepository.save(new ExerciseScheme("Übung", true, instituteSet1, tokenSet1, userSet1));
                exerciseSchemeRepository.save(new ExerciseScheme("Klausur", true,instituteSet2 , tokenSet1, userSet3));

                List<ExerciseScheme> exerciseSchemes = exerciseSchemeRepository.findAll();

                List<String> exerciseSchemeList1 = new ArrayList<String>();
                exerciseSchemeList1.add(exerciseSchemes.get(0).getId());
                exerciseSchemeList1.add(exerciseSchemes.get(0).getId());
                exerciseSchemeList1.add(exerciseSchemes.get(0).getId());
                exerciseSchemeList1.add(exerciseSchemes.get(0).getId());
                exerciseSchemeList1.add(exerciseSchemes.get(2).getId());

                List<String> exerciseSchemeList2 = new ArrayList<String>();
                exerciseSchemeList2.add(exerciseSchemes.get(1).getId());
                exerciseSchemeList2.add(exerciseSchemes.get(0).getId());
                exerciseSchemeList2.add(exerciseSchemes.get(1).getId());
                exerciseSchemeList2.add(exerciseSchemes.get(2).getId());

                List<String> exerciseSchemeList3 = new ArrayList<String>();
                exerciseSchemeList3.add(exerciseSchemes.get(1).getId());
                exerciseSchemeList3.add(exerciseSchemes.get(2).getId());
                exerciseSchemeList3.add(exerciseSchemes.get(0).getId());
                exerciseSchemeList3.add(exerciseSchemes.get(0).getId());
                exerciseSchemeList3.add(exerciseSchemes.get(1).getId());
                exerciseSchemeList3.add(exerciseSchemes.get(2).getId());

                moduleSchemeRepository.save(new ModuleScheme("Einführung in den Compilerbau", instituteSet1, userSet1,
                                exerciseSchemeList1, "Sehr simple Berechnungsregel"));

                moduleSchemeRepository.save(new ModuleScheme("Mathematik I", instituteSet2, userSet2,
                                exerciseSchemeList2, "ziemlich komplizierte Berechnungsregel"));

                moduleSchemeRepository.save(new ModuleScheme("Visuelle Trendanalyse", instituteSet3, userSet3,
                                exerciseSchemeList3, "wirklich sehr komplizierte Berechnungsregel"));

                LOGGER.info("refilling Database done.");
        }

}