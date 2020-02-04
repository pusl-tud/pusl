package de.bp2019.pusl.ui.components;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import de.bp2019.pusl.model.PerformanceScheme;

public class PerformanceSchemeEditor extends CustomField<PerformanceScheme> {

    private static final long serialVersionUID = 1L;

    private TextField name;
    private TextField calculationRule;

    public PerformanceSchemeEditor() {
        setWidth("100%");
        setHeight("20em");

        name = new TextField("Name");
        name.setWidth("100%");
        add(name);

        calculationRule = new TextField("Berechnungsregel");
        calculationRule.setValueChangeMode(ValueChangeMode.EAGER);
        calculationRule.setPlaceholder("Platzhalter");
        calculationRule.setHeight("15em");
        calculationRule.setWidthFull();
        add(calculationRule);
    }

    @Override
    protected PerformanceScheme generateModelValue() {
        return new PerformanceScheme(name.getValue(), calculationRule.getValue());
    }

    @Override
    protected void setPresentationValue(PerformanceScheme newPresentationValue) {
        name.setValue(newPresentationValue.getName());
        calculationRule.setValue(newPresentationValue.getCalculationRule());
    }

}