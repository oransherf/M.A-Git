package javafx.primary.left;

import engine.Branch;
import engine.Commit;
import javafx.AppController;
import javafx.fxml.FXML;
import javafx.primary.left.committree.CommitTreeManager;
import javafx.scene.control.ScrollPane;

import java.util.List;

public class LeftController
{
    private AppController m_MainController;
    private CommitTreeManager m_CommitTreeManager = new CommitTreeManager(this);

    @FXML private ScrollPane treeSurfaceScrollPane;

    public void setMainController(AppController i_MainController)
    {
        m_MainController = i_MainController;
    }

    public List<Commit> getOrderedCommitsByDate()
    {
        return m_MainController.getOrderedCommitsByDate();
    }

    public boolean isCommitFather(String i_FatherSHA1, String i_ChildSHA1)
    {
        return m_MainController.isCommitFather(i_FatherSHA1, i_ChildSHA1);
    }

    public List<Commit> getAllCommitsWithTwoParents()
    {
        return m_MainController.getAllCommitsWithTwoParents();
    }

    public Commit getCommit(String i_CommitSHA1)
    {
        return m_MainController.getCommit(i_CommitSHA1);
    }

    public void updateCommitTree()
    {
        m_CommitTreeManager.update(treeSurfaceScrollPane);
    }

    public void commitNodeTreeSelected(String i_CommitSHA1)
    {
        m_MainController.commitNodeTreeSelected(i_CommitSHA1);
    }

    public List<Branch> getContainedBranches(String i_CommitSHA1)
    {
        return m_MainController.getContainedBranches(i_CommitSHA1);
    }

    public Branch getActiveBranch()
    {
        return m_MainController.getActiveBranch();
    }

    public void resetBranchAnimate(String i_CommitSHA1)
    {
        m_CommitTreeManager.resetBranchAnimate(i_CommitSHA1);
    }
}
