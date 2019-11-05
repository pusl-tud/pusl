package de.bp2019.zentraldatei.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class DemoView extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    public DemoView() {
        add(new Text("Wie herrlich, es funktioniert alles, wunderbar."));
    }

}