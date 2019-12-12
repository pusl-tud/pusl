package de.bp2019.zentraldatei.view.components;

import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import de.bp2019.zentraldatei.model.PerformanceScheme;

/**
 * @author Leon Chemnitz
 */
public class PerformanceSchemeManager extends CustomField<List<PerformanceScheme>> {

    private static final long serialVersionUID = -1741662181318687543L;

    Tabs tabs;
    Map<Tab, Component> tabsToPages;

    public PerformanceSchemeManager(){

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