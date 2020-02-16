package de.bp2019.pusl.ui.views.user;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.config.AppConfig;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.service.UserService;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.MainAppView;

/**
 * View that displays a list of all {@link User}s
 * 
 * @author Leon Chemnitz
 */
@PageTitle(AppConfig.NAME + " | Benutzer")
@Route(value = ManageUsersView.ROUTE, layout = MainAppView.class)
public class ManageUsersView extends BaseView {

    private static final long serialVersionUID = -5763725756205681478L;

    public static final String ROUTE = "admin/users";

    private UserService userService;
    
    private ListDataProvider<User> userDataProvider;

    @Autowired
    public ManageUsersView(UserService userService) {
        super("Benutzer");

        this.userService = userService;

        userDataProvider = new ListDataProvider<>(userService.getAll());

        /* -- Create Components -- */

        Grid<User> grid = new Grid<>();

        grid.setWidth("100%");
        grid.setDataProvider(userDataProvider);

        grid.addComponentColumn(item -> createNameButton(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createDeleteButton(item)).setFlexGrow(0).setWidth("4em");

        add(grid);

        Button newUserButton = new Button("Neuer Nutzer");
        newUserButton.setId("new-user");
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

        /* initial admin has no name */
        if(user.getFirstName() == null){
            name = user.getEmailAddress();
        }

        Button button = new Button(name, clickEvent -> {
            UI.getCurrent().navigate(EditUserView.ROUTE + "/" + user.getId());
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
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
            Dialog dialog = new Dialog();
            dialog.add(new Text("Wirklich Löschen?"));
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            Button confirmButton = new Button("Löschen", event -> {
                try {
                    userService.delete(user);
                    userDataProvider.getItems().remove(user);
                    userDataProvider.refreshAll();
    
                    dialog.close();
                    Dialog answerDialog = new Dialog();
                    answerDialog.add(new Text("Nutzer '" + UserService.getFullName(user) + "' gelöscht"));
                    answerDialog.open();
                } catch(Exception e){                    
                    Dialog answerDialog = new Dialog();
                    answerDialog.add(new Text("Fehler beim Löschen des Nutzers!"));
                    answerDialog.open();
                    LOGGER.error("Could not delete User! User ID was: " + user.getId());
                }
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