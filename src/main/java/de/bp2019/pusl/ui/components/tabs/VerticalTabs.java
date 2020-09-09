package de.bp2019.pusl.ui.components.tabs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.tabs.Tabs.Orientation;

/**
 * A Vertical Tab component, to ease use with Vaadin tabs
 * 
 * @author Leon Chemnitz
 */
@Tag("pusl-vertical-tabs")
public class VerticalTabs<T extends Component> extends BaseTabs<T> {
    private static final long serialVersionUID = -8588329982545823315L;

    public VerticalTabs() {
        super(Orientation.VERTICAL);
    }
}