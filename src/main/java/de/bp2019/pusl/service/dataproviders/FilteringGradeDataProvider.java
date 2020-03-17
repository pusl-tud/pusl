package de.bp2019.pusl.service.dataproviders;

import java.util.stream.Stream;

import com.vaadin.flow.data.provider.AbstractDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.UIScope;

import org.springframework.stereotype.Service;

import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.service.GradeService;

@Service
@UIScope
public class FilteringGradeDataProvider extends AbstractDataProvider<Grade, String>
        implements ConfigurableFilterDataProvider<Grade, String, Grade> {

    private static final long serialVersionUID = -5360906790532916459L;

    private Grade filter;
    private GradeService gradeService;

    public FilteringGradeDataProvider(){

    }

    @Override
    public boolean isInMemory() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int size(Query<Grade, String> query) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Stream<Grade> fetch(Query<Grade, String> query) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setFilter(Grade filter) {
        // TODO Auto-generated method stub

    }

}