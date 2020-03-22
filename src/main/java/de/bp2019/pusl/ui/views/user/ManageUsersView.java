package de.bp2019.pusl.ui.views.user;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.service.UserService;
import de.bp2019.pusl.ui.dialogs.ConfirmDeletionDialog;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.dialogs.SuccessDialog;
import de.bp2019.pusl.ui.interfaces.AccessibleByAdmin;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.MainAppView;
import de.bp2019.pusl.util.Service;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * View that displays a list of all {@link User}s
 * 
 * @author Leon Chemnitz
 */
@PageTitle(PuslProperties.NAME + " | Benutzer")
@Route(value = ManageUsersView.ROUTE, layout = MainAppView.class)
public class ManageUsersView extends BaseView implements AccessibleByAdmin {

    private static final long serialVersionUID = -5763725756205681478L;

    public static final String ROUTE = "admin/users";

    private UserService userService;

    public ManageUsersView() {
        super("Benutzer");

        this.userService = Service.get(UserService.class);

        /* -- Create Components -- */

        Grid<User> grid = new Grid<>();

        grid.setWidth("100%");
        grid.setDataProvider(userService);

        grid.addComponentColumn(item -> createNameButton(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createDeleteButton(item)).setFlexGrow(0).setWidth("4em");

        add(grid);

        Button newUserButton = new Button("Neuer Nutzer");
        newUserButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(newUserButton);
        setHorizontalComponentAlignment(Alignment.END, newUserButton);

        newUserButton.addClickListener(event -> UI.getCurrent().navigate(EditUserView.ROUTE + "/new"));

    }

    /**
     * Used to create the button for the Grid entries that displays the name and
     * links to the edit page of the individual User.
     * 
     * @param user User to create the Button for
     * @return
     * @author Leon Chemnitz
     */
    private Button createNameButton(User user) {
        var name = UserService.getFullName(user);

        Button button = new Button(name, clickEvent -> {
            UI.getCurrent().navigate(EditUserView.ROUTE + "/" + user.getId());
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        /** makes testing a lot easier */
        button.setId("create-" + user.getId().toString());
        return button;
    }

    /**
     * Used to generate the delete button for each Grid Item
     * 
     * @param user entity to create button for
     * @author Leon Chemnitz
     * @return delete button
     */
    protected Button createDeleteButton(User user) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            ConfirmDeletionDialog.open(user.getEmailAddress(), () -> {
                try {
                    userService.delete(user);
                    userService.refreshAll();
                    SuccessDialog.open(user.getEmailAddress() + " erfolgreich gelöscht");
                } catch (UnauthorizedException e) {
                    UI.getCurrent().navigate(PuslProperties.ROOT_ROUTE);
                    ErrorDialog.open("Nicht authorisiert um Nutzer zu löschen!");
                }
            });
        });
        button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
        /** makes testing a lot easier */
        button.setId("delete-" + user.getId().toString());
        return button;
    }
}