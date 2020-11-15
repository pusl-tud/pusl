package de.bp2019.pusl.ui.views;

import javax.annotation.PostConstruct;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base View containing some formatting and title. All Views displayed in
 * {@link MainAppView} should extend this view.
 * 
 * @author Leon Chemnitz
 */
public class BaseView extends VerticalLayout {

    private static final long serialVersionUID = 5906463495344039816L;
    
    protected Logger LOGGER;

    public BaseView(String title) {
        LOGGER = LoggerFactory.getLogger(getClass());
        LOGGER.debug("started creation of " + this.getClass().getSimpleName());

        setWidth("90%");
        setMaxWidth("68em");
        getStyle().set("marginLeft", "2em");

        Label titleLabel = new Label(title);
        titleLabel.getStyle().set("font-size", "2em");
        add(titleLabel);
    }

    @PostConstruct
    public void postConstruct(){
        LOGGER.debug("finished creation of " + this.getClass().getSimpleName());
    }
}