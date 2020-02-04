package de.bp2019.pusl.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import de.bp2019.pusl.model.PerformanceScheme;

/**
 * Under development...
 * 
 * @author Leon Chemnitz
 */
public class PerformanceSchemeComposer extends CustomField<List<PerformanceScheme>> {

    private static final long serialVersionUID = -1741662181318687543L;

    private List<PerformanceSchemeEditor> performanceSchemeEditors;

    public PerformanceSchemeComposer() {
        setLabel("Leistungen");
        setWidth("100%");

        HorizontalTabs tabs = new HorizontalTabs();
        tabs.setWidth("100%");
        tabs.setHeight("20em");

        performanceSchemeEditors = new ArrayList<PerformanceSchemeEditor>();

        PerformanceSchemeEditor editor = new PerformanceSchemeEditor();

        performanceSchemeEditors.add(editor);
        tabs.addTab("Prüfungsleistung", editor);

        add(tabs);


        Button createButton = new Button(new Icon(VaadinIcon.PLUS_CIRCLE));
        add(createButton);
    }

    @Override
    protected List<PerformanceScheme> generateModelValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setPresentationValue(List<PerformanceScheme> newPresentationValue) {
        // TODO Auto-generated method stub

    }

}