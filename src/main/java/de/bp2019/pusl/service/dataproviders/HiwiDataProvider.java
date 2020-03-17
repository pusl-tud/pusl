package de.bp2019.pusl.service.dataproviders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import com.vaadin.flow.data.provider.AbstractDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.UIScope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.service.UserService;
import de.bp2019.pusl.ui.views.lecture.EditLectureView;
import de.bp2019.pusl.util.Utils;

/**
 * Dataprovider for Hiwis, filtered on a Set of {@link Institute}s. Used in
 * {@link EditLectureView}. Because this service has a state (the filter), it is
 * UI scoped and not a singleton. This Dataprovider keeps a list of all HIWIs in
 * memory to reduce complexity and the number of Database Queries.
 * 
 * @author Leon Chemnitz
 */
@Service
@UIScope
public class HiwiDataProvider extends AbstractDataProvider<User, String>
        implements ConfigurableFilterDataProvider<User, String, Set<Institute>> {

    private static final long serialVersionUID = 2275857103540285108L;

    @Autowired
    UserService userService;

    private Set<Institute> filter;

    private List<User> allHiwis;

    @PostConstruct
    public void init() {
        //allHiwis = userService.findAllHiwis();
        filter = new HashSet<Institute>();
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public int size(Query<User, String> query) {
        return (int) allHiwis.stream().filter(hiwi -> Utils.containsAny(hiwi.getInstitutes(), filter)).count();
    }

    @Override
    public Stream<User> fetch(Query<User, String> query) {
        List<User> hiwis = allHiwis.stream().filter(hiwi -> Utils.containsAny(hiwi.getInstitutes(), filter))
                .collect(Collectors.toList());

        int toIndex = query.getLimit() + query.getOffset();

        if (toIndex > hiwis.size()) {
            toIndex = hiwis.size();
        }

        return hiwis.subList(query.getOffset(), toIndex).stream();
    }

    @Override
    public void setFilter(Set<Institute> filter) {
        this.filter = filter;
    }

}