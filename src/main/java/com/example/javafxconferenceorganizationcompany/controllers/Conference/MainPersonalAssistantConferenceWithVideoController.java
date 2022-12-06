package com.example.javafxconferenceorganizationcompany.controllers.Conference;

import com.example.javafxconferenceorganizationcompany.ConferenceOrganizationCompanyApplication;
import com.example.javafxconferenceorganizationcompany.controllers.MainPersonalAssistantAndVideographer.PersonalAssistantAndVideographerMainController;
import com.example.javafxconferenceorganizationcompany.models.*;
import com.example.javafxconferenceorganizationcompany.repository.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class MainPersonalAssistantConferenceWithVideoController {
    @FXML
    private Label bujet;
    @FXML
    private Label description;

    @FXML
    private TableView<VideoAndPhotoShooting> videographer;
    @FXML
    private TableColumn<Object, Object> fioColumn;
    @FXML
    private TableColumn<Object, Object> contactPhoneColumn;
    @FXML
    private TableColumn<Object, Object> isVideoRequiredColumn;
    @FXML
    private TableColumn<Object, Object> isPhotoRequiredColumn;
    @FXML
    private TableColumn<Object, Object> deleteColumn;
    @FXML
    private Label FIO;
    @FXML
    private Label amount;
    @FXML
    private Label locationAddress;
    @FXML
    private Label phoneNumber;
    @FXML
    private CheckBox photoIsRequired;
    @FXML
    private CheckBox videoIsRequired;

    @FXML
    private Label companyName;
    @FXML
    private Label start;
    @FXML
    private Label finish;

    private Integer conferenceId;

    private Integer personalAssistantId;

    private Stage stage;
    private CompanyRepository companyRepository;
    private ConferenceRepository conferenceRepository;

    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setConferenceRepository(ConferenceRepository conferenceRepository) {
        this.conferenceRepository = conferenceRepository;
    }

    public void setCompanyRepository(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public void setConferenceId(Integer conferenceId) {
        this.conferenceId = conferenceId;
    }

    public void setPersonalAssistantId(Integer id) {
        this.personalAssistantId=id;
    }

    public void setInfo() {
        Conference conference = conferenceRepository.getConferenceById(conferenceId);

        description.setText(conference.getConferenceDescription());
        bujet.setText(String.valueOf(conference.getBudjet()));
        start.setText(conference.getStartTime());
        finish.setText(conference.getFinishTime());
        amount.setText(String.valueOf(conference.getParticipantsAmount()));
        System.out.println(conference.getParticipantsAmount());

        int locationId=conference.getConferenceLocationId();
        int companyId = conference.getCompanyId();

        Company company = companyRepository.getCompanyByItsId(companyId);
        companyName.setText(company.getCompanyName());
        FIO.setText(company.getMainParticipantFIO());
        phoneNumber.setText(company.getMainParticipantContactTelephoneNumber());

        UserRepository userRepository = new UserRepository(connection);
        User user = userRepository.getPersonalAssistantByConferenceId(conferenceId);

        LocationRepository locationRepository=new LocationRepository(connection);
        Location location=locationRepository.getLocationById(locationId);

        locationAddress.setText(location.getLocationAddress());

        VideoAndPhotoShootingRepository videoAndPhotoShootingRepository= new VideoAndPhotoShootingRepository(connection);
        ObservableList<VideoAndPhotoShooting> shoots= videoAndPhotoShootingRepository.getAllVideoAndPhotoShootingsByConferenceId(conferenceId);


        for (VideoAndPhotoShooting shoot:shoots){
            EventHandler<ActionEvent> handler = event -> {
                System.out.println(shoot.getVideoAndPhotoId());
                videoAndPhotoShootingRepository.deleteVideoAndPhotoShootingByShootingId(shoot.getVideoAndPhotoId());
                FXMLLoader fxmlLoader = new FXMLLoader(ConferenceOrganizationCompanyApplication.class.getResource("personal-assistant-conference-view-with-video-or-photo.fxml"));
                Scene newScene = null;
                try {
                    newScene = new Scene(fxmlLoader.load(), 700, 700);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                MainPersonalAssistantConferenceWithVideoController controller = fxmlLoader.getController();
                controller.setConnection(connection);
                controller.setConferenceRepository(conferenceRepository);
                controller.setCompanyRepository(new CompanyRepository(connection));
                controller.setPersonalAssistantId(personalAssistantId);
                controller.setStage(stage);
                controller.setConferenceId(conferenceId);
                controller.setInfo();
                stage.setScene(newScene);
            };

            Button button = shoot.getDelete();////устанавливаем кнопкам обработчики
            button.setOnAction(handler);
            shoot.setDelete(button);
        }
        fioColumn.setCellValueFactory(new PropertyValueFactory<>("videographerFIO"));
        contactPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("videographerPhoneNumber"));
        isVideoRequiredColumn.setCellValueFactory(new PropertyValueFactory<>("videoIsRequired"));
         isPhotoRequiredColumn.setCellValueFactory(new PropertyValueFactory<>("photoIsRequired"));
         deleteColumn.setCellValueFactory(new PropertyValueFactory<>("delete"));


         videographer.setItems(shoots);
    }

    public void backToMain() {
        FXMLLoader fxmlLoader = new FXMLLoader(ConferenceOrganizationCompanyApplication.class.getResource("personal-assistant-videographer-main-view.fxml"));
        Scene newScene = null;
        try {
            newScene = new Scene(fxmlLoader.load(), 700, 700);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PersonalAssistantAndVideographerMainController controller = fxmlLoader.getController();
        controller.setConferenceRepository(conferenceRepository);
        controller.setConnection(connection);
        controller.setUserRepository(new UserRepository(connection));
        controller.setStage(stage);
        controller.setRoleId(2);
        controller.setId(personalAssistantId);
        controller.setInfo();
        stage.setScene(newScene);

    }
}
