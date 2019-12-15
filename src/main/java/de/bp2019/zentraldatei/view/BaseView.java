package de.bp2019.zentraldatei.view;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class BaseView extends VerticalLayout{

    private static final long serialVersionUID = 5906463495344039816L;

    public BaseView(String title) {
        setWidth("90%");
        setMaxWidth("50em");
        getStyle().set("marginLeft", "2em");

        Label titleLabel = new Label(title);
        titleLabel.getStyle().set("font-size", "2em");
        add(titleLabel);
    }
}