package de.bp2019.zentraldatei.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import de.bp2019.zentraldatei.service.InstituteService;
import de.bp2019.zentraldatei.service.ModuleSchemeService;
import de.bp2019.zentraldatei.service.UserService;
import de.bp2019.zentraldatei.enums.UserType;
import de.bp2019.zentraldatei.model.User;

/**
 * View that displays a list of all Users
 * 
 * @author Fabio Pereira da Costa
 */

@Route("Userview/new")
public class Userview extends Div implements HasUrlParameter<String> {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(NewUser.class);

	/*
	 * no @Autowire because service is injected by constructor. Vaadin likes it
	 * better this way...
	 */
	private UserService userservice;
	
	List<User> userlist = new ArrayList<>();
	
	public Userview(@Autowired InstituteService instituteService, @Autowired UserService userService,
            @Autowired ModuleSchemeService moduleSchemeService,
            @Autowired ExerciseSchemeService exerciseSchemeService) {

		LOGGER.debug("Started creation of ModuleSchemeView");
		
		// Beispieldaten zum Füllen und Testen
		Set<String> institutes = new HashSet<String>();
		institutes.add("Bahntechnik");	
		userlist.add(new User("Fabio", "Costa", "abc@de.com", "password", institutes, UserType.SUPERADMIN));
		institutes.add("Verkehrstechnik");
		userlist.add(new User("Tiago", "Costa", "abcd@de.com", "password2", institutes, UserType.ADMIN));
		
		// create Textfield to search 
		TextField searchtext = new TextField();
		searchtext.setPlaceholder("Suche");
		searchtext.setValueChangeMode(ValueChangeMode.EAGER); 
		
		// create Button to search 
		Button search = new Button("Suche");
		
		
		HorizontalLayout actions = new HorizontalLayout();
		actions.add(searchtext, search);
		searchtext.getStyle().set("marginRight", "10em");
		
			
		//create table
		//The Grid<>(User.class) sorts the properties
		Grid<User> grid = new Grid<>(User.class);
		
		grid.setItems(userlist);
		//grid.setItems(userService.getAllUser());
		
		grid.removeColumnByKey("id");
		
		// setting the columns which should be shown
		grid.setColumns("Vorname", "Nachname", "Email", "Institute", "Usertyp");
		
	}
	
	

	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		// TODO Auto-generated method stub

	}
}
