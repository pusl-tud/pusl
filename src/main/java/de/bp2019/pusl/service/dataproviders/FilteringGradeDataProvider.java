package de.bp2019.pusl.service.dataproviders;

import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import com.vaadin.flow.data.provider.AbstractDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.UIScope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.GradeFilter;
import de.bp2019.pusl.service.GradeService;

@Service
@UIScope
public class FilteringGradeDataProvider extends AbstractDataProvider<Grade, String>
        implements ConfigurableFilterDataProvider<Grade, String, GradeFilter> {

    private static final long serialVersionUID = -5360906790532916459L;

    private GradeFilter filter;

    @Autowired
    private GradeService gradeService;

    @PostConstruct
    public void init(){
        filter = new GradeFilter();
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public int size(Query<Grade, String> query) {
        return gradeService.size(query, filter);
    }

    @Override
    public Stream<Grade> fetch(Query<Grade, String> query) {
        return gradeService.fetch(query, filter);
    }

    @Override
    public void setFilter(GradeFilter filter) {
        this.filter = filter;
    }

}