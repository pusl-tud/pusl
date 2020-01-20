package de.bp2019.zentraldatei.UI.views.User;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bp2019.zentraldatei.UI.views.BaseView;
import de.bp2019.zentraldatei.UI.views.MainAppView;
import de.bp2019.zentraldatei.UI.views.ExerciseScheme.ManageExerciseSchemesView;
import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.User;
import de.bp2019.zentraldatei.service.UserService;

/**
 * View containing a form to edit a Userview
 *
 * @author Fabio Costa
 *
 **/
@PageTitle("Zentraldatei | UserView")
@Route(value = "userviews", layout = MainAppView.class)
public class UserManagementView extends BaseView {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ManageExerciseSchemesView.class);

	private UserService userService;

	Grid<User> grid = new Grid<>();

	@Autowired
	public UserManagementView(UserService userService) {
		super("Benutzer");

		LOGGER.debug("started creation of ManageInstitutesView");

		this.userService = userService;

		/* -- Create Components -- */

		grid.setWidth("100%");
		grid.setItems(userService.getAllUsers());

		grid.addComponentColumn(item -> createUserButton(item)).setAutoWidth(true);
		grid.addComponentColumn(item -> createDeleteButton(item)).setFlexGrow(0).setWidth("4em");

		add(grid);

		Button newUserButton = new Button("Neuer Benutzer");
		newUserButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		add(newUserButton);
		setHorizontalComponentAlignment(Alignment.END, newUserButton);

		newUserButton.addClickListener(event -> UI.getCurrent().navigate("UserView/new"));

		LOGGER.debug("finished creation of ManageInstitutesView");
	}

	 /**
     * Used to generate the User field for each Grid item
     *
     * @param item
     * @return
     * @author Fabio Costa
     */
	private Button createUserButton(User item) {
		Button button = new Button(item.getLastName(), clickEvent -> {
			UI.getCurrent().navigate("User/" + item.getId());
		});
		button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		return button;
	}

	/**
     * Used to generate the delete button for each Grid Item
     *
     * @param item
     * @return
     * @author Fabio Costa
     */
	protected Button createDeleteButton(User item) {
		Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
			Dialog dialog = new Dialog();
			dialog.add(new Text("Wirklich Löschen?"));
			dialog.setCloseOnEsc(false);
			dialog.setCloseOnOutsideClick(false);

			Button confirmButton = new Button("Löschen", event -> {
				userService.deleteUser(item);
				ListDataProvider<User> dataProvider = (ListDataProvider<User>) grid.getDataProvider();
				dataProvider.getItems().remove(item);
				dataProvider.refreshAll();

				dialog.close();
				Dialog answerDialog = new Dialog();
				answerDialog.add(new Text("Benutzer " + item.getFirstName() + " " + item.getLastName() + " gelöscht"));
				answerDialog.open();
			});

			Button cancelButton = new Button("Abbruch", event -> {
				dialog.close();
			});

			dialog.add(confirmButton, cancelButton);
			dialog.open();
		});
		button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
		return button;
	}

}
