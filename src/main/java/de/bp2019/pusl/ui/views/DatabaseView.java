package de.bp2019.pusl.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import de.bp2019.pusl.service.SerializationService;
import de.bp2019.pusl.ui.interfaces.AccessibleBySuperadmin;
import de.bp2019.pusl.util.Service;

/**
 * Demo View currently just empties and refills the database.
 * 
 * @author Leon Chemnitz
 */
@Route(value = DatabaseView.ROUTE, layout = MainAppView.class)
public class DatabaseView extends BaseView implements AccessibleBySuperadmin {

        private static final long serialVersionUID = 1240260329860093364L;

        public static final String ROUTE = "admin/database";

        private SerializationService serializationService;

        public DatabaseView() {
                super("Datenbank");

                serializationService = Service.get(SerializationService.class);

                FormLayout layout = new FormLayout();
                layout.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));
                layout.setWidth("100%");

                Anchor backupDownload = new Anchor(
                                new StreamResource("BACKUP.pusl", this.serializationService::serialize), "");
                backupDownload.getElement().setAttribute("download", true);
                Button backupDownloadButton = new Button("Backup herunterladen");
                backupDownloadButton.setWidthFull();
                backupDownloadButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
                backupDownload.add(backupDownloadButton);
                backupDownload.setWidthFull();
                layout.add(backupDownload, 2);

                MemoryBuffer loadStateUploadBuffer;
                loadStateUploadBuffer = new MemoryBuffer();
                Upload loadStateUpload = new Upload(loadStateUploadBuffer);
                // upload.setDropLabel(new Span("Excelliste hier abelegen"));
                loadStateUpload.setUploadButton(new Button("Backup hochladen und laden"));
                loadStateUpload.setAcceptedFileTypes(".pusl");
                loadStateUpload.setWidth("96%");
                add(loadStateUpload);

                MemoryBuffer insertStateUploadBuffer;
                insertStateUploadBuffer = new MemoryBuffer();
                Upload insertStateUpload = new Upload(insertStateUploadBuffer);
                // upload.setDropLabel(new Span("Excelliste hier abelegen"));
                insertStateUpload.setUploadButton(new Button("Backup hochladen und einfügen"));
                insertStateUpload.setAcceptedFileTypes(".pusl");
                insertStateUpload.setWidth("96%");
                add(insertStateUpload);

                add(layout);

                /* ############ Listeners ########## */

                loadStateUpload.addSucceededListener(event -> {
                        serializationService.loadState(loadStateUploadBuffer.getInputStream());
                });
                
                insertStateUpload.addSucceededListener(event -> {
                        serializationService.insertState(insertStateUploadBuffer.getInputStream());
                });
        }

}