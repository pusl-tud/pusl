package de.bp2019.pusl.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import de.bp2019.pusl.model.PerformanceScheme;

/**
 * Field Component used in {@link EditLectureView} to manage PerformanceSchemes
 * 
 * @author Leon Chemnitz
 */
public class PerformanceSchemeComposer extends CustomField<List<PerformanceScheme>> {

    private static final long serialVersionUID = -1741662181318687543L;

    private List<String> performanceNames;
    HorizontalTabs<TextArea> tabs;
    Button deleteButton;

    public PerformanceSchemeComposer() {
        setWidth("100%");

        tabs = new HorizontalTabs<TextArea>();
        tabs.setHeight("22.8em");

        performanceNames = new ArrayList<String>();

        var firstPerformanceName = "Prüfungsleistung";
        tabs.addTab(firstPerformanceName, createCalculationRuleField());

        add(tabs);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new ResponsiveStep("1em", 1), new ResponsiveStep("1em", 2),
                new ResponsiveStep("1em", 3));

        TextField nameField = new TextField();
        nameField.setValueChangeMode(ValueChangeMode.EAGER);
        nameField.setPlaceholder("Name");
        formLayout.add(nameField);

        Button createButton = new Button("Leistung hinzufügen", new Icon(VaadinIcon.PLUS_CIRCLE));
        createButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        formLayout.add(createButton);

        deleteButton = new Button("Leistung löschen", new Icon(VaadinIcon.CLOSE));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.setEnabled(false);

        formLayout.add(deleteButton);

        add(formLayout);

        /* ######### Listeners ######## */

        createButton.addClickListener(event -> {
            var performanceName = nameField.getValue();

            if (!performanceNames.contains(performanceName)) {
                performanceNames.add(performanceName);
                tabs.addTab(performanceName, createCalculationRuleField());
                setPresentationValue(generateModelValue());
            }
        });

        deleteButton.addClickListener(event -> {
            performanceNames.remove(tabs.getSelectedTabTitle());
            tabs.deleteSelectedTab();
            if (tabs.getNumTabs() <= 1) {
                deleteButton.setEnabled(false);
            }
        });
    }

    @Override
    protected List<PerformanceScheme> generateModelValue() {
        List<PerformanceScheme> performanceSchemes = new ArrayList<PerformanceScheme>();
        performanceNames.forEach(name -> performanceSchemes
                .add(new PerformanceScheme(name, tabs.getComponentFromTitle(name).getValue())));
        return performanceSchemes;
    }

    @Override
    protected void setPresentationValue(List<PerformanceScheme> newPresentationValue) {
        tabs.deleteAllTabs();
        performanceNames.clear();

        newPresentationValue.forEach(performanceScheme -> {
            String name = performanceScheme.getName();
            performanceNames.add(name);
            TextArea calculationRule = createCalculationRuleField();
            calculationRule.setValue(performanceScheme.getCalculationRule());
            tabs.addTab(name, calculationRule);
        });

        if (tabs.getNumTabs() > 1) {
            deleteButton.setEnabled(true);
        } else {
            deleteButton.setEnabled(true);
        }
    }

    private TextArea createCalculationRuleField() {

        TextArea editor = new TextArea();
        editor.setValueChangeMode(ValueChangeMode.EAGER);
        editor.setWidth("100%");
        editor.getStyle().set("minHeight", "18em");

        String defaultValue = "function calcuate(results) { \n";
        defaultValue += "     \n";
        defaultValue += "    return ergebnis;\n";
        defaultValue += "}";

        editor.setValue(defaultValue);
        return editor;
    }

}