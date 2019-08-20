package javafx.primary.top.popup.createnewrepository;

import javafx.event.ActionEvent;
import javafx.factories.AlertFactory;
import javafx.fxml.FXML;
import javafx.primary.top.TopController;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class CreateNewRepositoryController
{
    @FXML private TopController m_TopController;
    @FXML private VBox createNewRepositoryComponent;
    @FXML private TextField directoryTextField;
    @FXML private Button browseButton;
    @FXML private TextField repositoryNameTextField;
    @FXML private Button createButton;

    public void setTopController(TopController i_TopController){ m_TopController = i_TopController; }

    @FXML
    void browseButtonAction(ActionEvent event)
    {
        Node source = (Node)event.getSource();
        Window theStage = source.getScene().getWindow();

        // get directory from user
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(theStage);

        // if user choosed a directory set it to directoryTextField
        if (selectedDirectory != null)
        {
            directoryTextField.setText(selectedDirectory.toString());
        }
    }

    @FXML
    void createButtonAction(ActionEvent event) throws IOException
    {
        boolean isRepoNameIsntEmpty, isUserChoosedPath;
        String userInputRepoName = repositoryNameTextField.getText();
        Path userInputPath;

        // check if the user chose path
        isUserChoosedPath = directoryTextField.getText().length() != 0;
        // check if the user entered a repository name
        isRepoNameIsntEmpty = repositoryNameTextField.getText().length() != 0;

        if (isUserChoosedPath && isRepoNameIsntEmpty)
        {
            userInputPath = Paths.get(directoryTextField.getText());
            // check if the path is already a repository
            if (m_TopController.isRepository(userInputPath))
            {
                // stash and initialize new repository if user wants to
                handleStashAndCreateNewRepository(userInputPath, userInputRepoName);
            }
            else
            {
                // the path isn't repository, init this path and inform user
                createNewRepository(userInputPath, userInputRepoName);
            }
        }
        else
        {
            // user didn't enter path or name
            errorAlertUserInputIsMissing();
        }
    }


    private void errorAlertUserInputIsMissing()
    {
        AlertFactory alertFactory = new AlertFactory();

        alertFactory.createErrorAlert("Create new repository" , "Directory path or repository name are missing")
                .showAndWait();
    }

    private void handleStashAndCreateNewRepository(Path i_UserInputPath, String i_UserInputRepoName) throws IOException
    {
        AlertFactory alertFactory = new AlertFactory();


        // ask user if he want to stash the existing repository
        Alert alert = alertFactory.createYesNoAlert("Create new repository", "Would you like to stash the existing repository?");
        boolean isUserWantToStash = alert.showAndWait().get() == ButtonType.YES;

        if (isUserWantToStash)
        {
            m_TopController.stashRepository(i_UserInputPath);
            createNewRepository(i_UserInputPath, i_UserInputRepoName);
        }
    }

    private void createNewRepository(Path i_UserInputPath, String i_UserInputRepoName) throws IOException
    {
        AlertFactory alertFactory = new AlertFactory();

        // ask user if he want to stash the existing repository
        Alert alert = alertFactory.createInformationAlert("Create new repository", "A new repository has been initialized!");
        alert.showAndWait();
        m_TopController.createNewRepository(i_UserInputPath, i_UserInputRepoName);
    }

}
