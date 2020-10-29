package de.bp2019.pusl.service.dataproviders;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import com.vaadin.flow.data.provider.AbstractDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.UIScope;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.service.UserService;
import de.bp2019.pusl.ui.views.lecture.EditLectureView;

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
        implements ConfigurableFilterDataProvider<User, String, Set<ObjectId>> {

    private static final long serialVersionUID = 2275857103540285108L;

    @Autowired
    UserService userService;

    private Set<ObjectId> filter;

    @PostConstruct
    public void init() {
        filter = new HashSet<>();
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public int size(Query<User, String> query) {
        return userService.sizeHiwis(query, filter);
    }

    @Override
    public Stream<User> fetch(Query<User, String> query) {
        return userService.fetchHiwis(query, filter);
    }

    @Override
    public void setFilter(Set<ObjectId> filter) {
        this.filter = filter;
    }

}