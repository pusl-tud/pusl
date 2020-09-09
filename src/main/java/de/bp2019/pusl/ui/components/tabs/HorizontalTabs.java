package de.bp2019.pusl.ui.components.tabs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.tabs.Tabs.Orientation;

/**
 * A Horizontal Tab component, to ease use with Vaadin tabs
 * 
 * @author Leon Chemnitz
 */
@Tag("pusl-horizontal-tabs")
public class HorizontalTabs<T extends Component> extends BaseTabs<T> {
    public HorizontalTabs() {
        super(Orientation.HORIZONTAL);
    }

    private static final long serialVersionUID = -6745194487461158076L;

}
