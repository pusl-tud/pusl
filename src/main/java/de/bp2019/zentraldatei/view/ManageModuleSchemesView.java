package de.bp2019.zentraldatei.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import de.bp2019.zentraldatei.model.ModuleSchemeTmp;
import de.bp2019.zentraldatei.view.components.CustomListComponent;

/**
 * View that displays a list of all ModuleSchemes
 * 
 * @author Leon Chemnitz
 */
@Route("moduleSchemes")
public class ManageModuleSchemesView extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    
    public ManageModuleSchemesView() {
        /* -- Create Components -- */
        CustomListComponent<ModuleSchemeTmp> excerciseSchemes = new CustomListComponent<ModuleSchemeTmp>(new ModuleSchemeTmp.ModuleSchemeDefaultFactory());
        excerciseSchemes.setLabel("Veranstaltungen");
        excerciseSchemes.setButtonText("Veranstaltung hinzufügen");
        add(excerciseSchemes);
    }

}