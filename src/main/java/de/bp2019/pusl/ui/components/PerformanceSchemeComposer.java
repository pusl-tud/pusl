package de.bp2019.pusl.ui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import de.bp2019.pusl.ui.components.tabs.HorizontalTabs;
import de.bp2019.pusl.ui.views.lecture.EditLectureView;

/**
 * Field Component used in {@link EditLectureView} to manage PerformanceSchemes
 * 
 * @author Leon Chemnitz
 */
public class PerformanceSchemeComposer extends CustomField<List<PerformanceScheme>> {

    private static final long serialVersionUID = -1741662181318687543L;

    private List<String> performanceNames = new ArrayList<>();
    private HorizontalTabs<TextArea> tabs;
    private Button deleteButton;
    private String defaultCalculationRule;

    public PerformanceSchemeComposer() {
        setWidth("100%");

        defaultCalculationRule = "function calculate(results) { \n";
        defaultCalculationRule += "     \n";
        defaultCalculationRule += "    return 'nicht definiert';\n";
        defaultCalculationRule += "}";

        tabs = new HorizontalTabs<>();
        tabs.setHeight("22.8em");

        add(tabs);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new ResponsiveStep("1em", 1), new ResponsiveStep("1em", 2),
                new ResponsiveStep("1em", 3));

        TextField nameField = new TextField();
        nameField.setValueChangeMode(ValueChangeMode.EAGER);
        nameField.setPlaceholder("Name");
        nameField.setId("performance-name");
        formLayout.add(nameField);

        Button createButton = new Button("Leistung hinzufügen", new Icon(VaadinIcon.PLUS_CIRCLE));
        createButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        createButton.setId("create-performance");

        formLayout.add(createButton);

        deleteButton = new Button("Leistung löschen", new Icon(VaadinIcon.CLOSE));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.setEnabled(false);

        formLayout.add(deleteButton);

        add(formLayout);

        setPresentationValue(Arrays.asList(new PerformanceScheme("Prüfungsleistung", defaultCalculationRule)));

        /* ######### Listeners ######## */

        createButton.addClickListener(event -> {
            String performanceName = nameField.getValue();

            if (!performanceNames.contains(performanceName) && !performanceName.equals("")) {
                performanceNames.add(performanceName);
                tabs.addTab(performanceName, createCalculationRuleField(defaultCalculationRule));
                nameField.setValue("");
                updateValue();
            }
        });

        deleteButton.addClickListener(event -> {
            performanceNames.remove(tabs.getSelectedTabTitle());
            tabs.deleteSelectedTab();

            if (tabs.getNumTabs() <= 1) {
                deleteButton.setEnabled(false);
            }

            setValue(generateModelValue());
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

        newPresentationValue.forEach(performanceScheme -> {
            String name = performanceScheme.getName();

            if (performanceNames.contains(name)) {
                tabs.getComponentFromTitle(name).setValue(performanceScheme.getCalculationRule());
            } else {
                tabs.addTab(name, createCalculationRuleField(performanceScheme.getCalculationRule()));
            }
        });

        performanceNames = newPresentationValue.stream().map(PerformanceScheme::getName).collect(Collectors.toList());

        for (String name : performanceNames) {
            if (newPresentationValue.stream().filter(scheme -> scheme.getName().equals(name)).findFirst().isEmpty()) {
                tabs.removeTab(name);
            }
        }


        if (tabs.getNumTabs() > 1) {
            deleteButton.setEnabled(true);
        } else {
            deleteButton.setEnabled(false);
        }
    }

    private TextArea createCalculationRuleField(String calculationRule) {
        TextArea editor = new TextArea();
        editor.setValueChangeMode(ValueChangeMode.EAGER);
        editor.setWidth("100%");
        editor.getStyle().set("minHeight", "18em");
        editor.setValue(calculationRule);
        editor.setVisible(false);

        editor.addValueChangeListener(event -> {
            setValue(generateModelValue());
        });

        return editor;
    }

}