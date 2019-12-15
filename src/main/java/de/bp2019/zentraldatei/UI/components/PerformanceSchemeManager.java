package de.bp2019.zentraldatei.UI.components;

import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;


/**
 * Under development...
 * @author Leon Chemnitz
 */
public class PerformanceSchemeManager extends CustomField<List<String>> {

    private static final long serialVersionUID = -1741662181318687543L;

    Tabs tabs;
    Map<Tab, Component> tabsToPages;

    public PerformanceSchemeManager(){

    }

    @Override
    protected List<String> generateModelValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setPresentationValue(List<String> newPresentationValue) {
        // TODO Auto-generated method stub

    }

}