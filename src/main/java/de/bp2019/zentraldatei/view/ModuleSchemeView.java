package de.bp2019.zentraldatei.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.gatanaso.MultiselectComboBox;

import de.bp2019.zentraldatei.model.ExerciseScheme;
import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.ModuleScheme;
import de.bp2019.zentraldatei.model.User;
import de.bp2019.zentraldatei.service.InstituteService;
import de.bp2019.zentraldatei.service.ModuleSchemeService;
import de.bp2019.zentraldatei.service.UserService;

/**
 * View containing a form to edit a ModuleScheme
 * 
 * @author Leon Chemnitz
 */
@Route("moduleScheme")
public class ModuleSchemeView extends Div implements HasUrlParameter<String> {

        private static final long serialVersionUID = 1L;
        private static final Logger LOGGER = LoggerFactory.getLogger(ModuleSchemeView.class);

        private ModuleSchemeService moduleSchemeService;

        private Grid<ExerciseScheme> exerciseSchemes;
        private ExerciseScheme draggedItem;
        private List<ExerciseScheme> gridItems;

        /** Binder to bind the form Data to an Object */
        private Binder<ModuleScheme> binder;

        private boolean isNewEntity;

        public ModuleSchemeView(@Autowired InstituteService instituteService, @Autowired UserService userService,
                        @Autowired ModuleSchemeService moduleSchemeService) {

                LOGGER.debug("Started creation of ModuleSchemeView");

                this.moduleSchemeService = moduleSchemeService;

                FormLayout layoutWithBinder = new FormLayout();
                layoutWithBinder.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("10em", 2));
                layoutWithBinder.setWidth("35em");
                layoutWithBinder.getStyle().set("marginLeft", "1em");

                binder = new Binder<>();

                /* -- Create the fields -- */
                TextField name = new TextField();
                name.setLabel("Name");
                name.setPlaceholder("Name Der Veranstaltung");
                name.setValueChangeMode(ValueChangeMode.EAGER);
                layoutWithBinder.add(name);

                MultiselectComboBox<Institute> institutes = new MultiselectComboBox<Institute>();
                institutes.setLabel("Institute");
                institutes.setItems(instituteService.getAllInstitutes());
                institutes.setItemLabelGenerator(Institute::getName);
                layoutWithBinder.add(institutes, 2);

                MultiselectComboBox<User> hasAccess = new MultiselectComboBox<User>();
                hasAccess.setLabel("Hat Zugriff");
                hasAccess.setItems(userService.getAllUsers());
                hasAccess.setItemLabelGenerator(user -> userService.getFullName(user));
                layoutWithBinder.add(hasAccess, 2);

                exerciseSchemes = new Grid<>();
                gridItems = new ArrayList<ExerciseScheme>();
                exerciseSchemes.setItems(gridItems);
                exerciseSchemes.addColumn(ExerciseScheme::getName).setHeader("Prüfungsschemas");
                exerciseSchemes.setSelectionMode(SelectionMode.NONE);
                exerciseSchemes.setRowsDraggable(true);
                exerciseSchemes.setHeight("15em");
                layoutWithBinder.add(exerciseSchemes, 2);

                TextArea calculationRule = new TextArea();
                calculationRule.setValueChangeMode(ValueChangeMode.EAGER);
                calculationRule.setLabel("Berechnungsregel");
                calculationRule.setPlaceholder("Platzhalter");
                layoutWithBinder.add(calculationRule, 2);

                Button save = new Button("Speichern");
                save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                VerticalLayout actions = new VerticalLayout();
                actions.add(save);
                actions.setHorizontalComponentAlignment(Alignment.END, save);
                layoutWithBinder.add(actions, 2);

                /* -- Data Binding and validation -- */
                binder.forField(name).withValidator(
                                new StringLengthValidator("Bitte Name der Veranstaltung angeben", 1, null))
                                .bind(ModuleScheme::getName, ModuleScheme::setName);

                binder.forField(institutes)
                                .withValidator(selectedInstitutes -> !selectedInstitutes.isEmpty(),
                                                "Bitte mind. ein Institut angeben")
                                .bind(ModuleScheme::getInstitutes, ModuleScheme::setInstitutes);

                binder.bind(hasAccess, ModuleScheme::getHasAccess, ModuleScheme::setHasAccess);

                binder.bind(calculationRule, ModuleScheme::getCalculationRule, ModuleScheme::setCalculationRule);
                TextArea id = new TextArea();
                binder.bind(id, ModuleScheme::getId, ModuleScheme::setId);

                /* -- Click Listeners for the Buttons -- */
                save.addClickListener(event -> {
                        ModuleScheme formData = new ModuleScheme();
                        if (binder.writeBeanIfValid(formData)) {
                                if (isNewEntity) {
                                        moduleSchemeService.addModuleScheme(formData);
                                        Dialog dialog = new Dialog();
                                        dialog.add(new Text("Veranstaltungsschema erfolgreich erstellt oder so..."));
                                        dialog.open();
                                        UI.getCurrent().navigate("moduleSchemes");
                                } else {
                                        moduleSchemeService.updateModuleScheme(formData);
                                        Dialog dialog = new Dialog();
                                        dialog.add(new Text("Veranstaltungsschema erfolgreich verändert oder so..."));
                                        dialog.open();
                                        UI.getCurrent().navigate("moduleSchemes");
                                }
                        } else {
                                BinderValidationStatus<ModuleScheme> validate = binder.validate();
                                String errorText = validate.getFieldValidationStatuses().stream()
                                                .filter(BindingValidationStatus::isError)
                                                .map(BindingValidationStatus::getMessage).map(Optional::get).distinct()
                                                .collect(Collectors.joining(", "));
                                LOGGER.info("There are errors: " + errorText);
                        }
                });

                exerciseSchemes.addDragStartListener(event -> {
                        draggedItem = event.getDraggedItems().get(0);
                        exerciseSchemes.setDropMode(GridDropMode.BETWEEN);
                });

                exerciseSchemes.addDragEndListener(event -> {
                        draggedItem = null;
                        exerciseSchemes.setDropMode(null);
                });

                exerciseSchemes.addDropListener(event -> {
                        ExerciseScheme dropOverItem = event.getDropTargetItem().get();
                        if (!dropOverItem.equals(draggedItem)) {
                                gridItems.remove(draggedItem);
                                int dropIndex = gridItems.indexOf(dropOverItem)
                                                + (event.getDropLocation() == GridDropLocation.BELOW ? 1 : 0);
                                gridItems.add(dropIndex, draggedItem);
                                exerciseSchemes.getDataProvider().refreshAll();
                        }
                });
                /* -- Add Layout to Component -- */
                add(layoutWithBinder);
                LOGGER.debug("Finished creation of ManageModuleSchemesView");
        }

        @Override
        public void setParameter(BeforeEvent event, String moduleSchemeId) {
                if (moduleSchemeId.equals("new")) {
                        isNewEntity = true;
                        /* clear fields by setting null */
                        binder.readBean(null);
                } else {

                        ModuleScheme fetchedModuleScheme = moduleSchemeService.getModuleSchemeById(moduleSchemeId);
                        /* getModuleSchemeById returns null if no matching ModuleScheme is found */
                        if (fetchedModuleScheme == null) {
                                throw new NotFoundException();
                        } else {
                                isNewEntity = false;
                                binder.readBean(fetchedModuleScheme);
                                gridItems.clear();
                                gridItems.addAll(fetchedModuleScheme.getExerciseSchemes());
                                exerciseSchemes.getDataProvider().refreshAll();
                        }
                }

        }

}