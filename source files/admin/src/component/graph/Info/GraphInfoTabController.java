package component.graph.Info;

import component.graph.Info.table.view.TableViewTabController;
import component.graph.Info.tree.view.TreeViewTabController;
import component.supercontroller.AdminSuperController;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import output.GraphInfoDto;
import output.TargetInfoDto;
import output.TargetInfoForTableViewDto;

import java.io.IOException;
import java.util.Set;

public class GraphInfoTabController {
    private AdminSuperController sController;

    @FXML private GridPane tableViewTabContent;
    @FXML private TableViewTabController tableViewTabContentController;

    @FXML private GridPane treeViewTabContent;
    @FXML private TreeViewTabController treeViewTabContentController;

    @FXML public void initialize() {
      if(tableViewTabContentController!=null&&treeViewTabContentController!=null) {
          tableViewTabContentController.setGraphInfoTabController(this);
          treeViewTabContentController.setGraphInfoTabController(this);
      }
    }

    public void setAdminSuperController(AdminSuperController superCont){
        sController=superCont;
    }

    public TargetInfoForTableViewDto[] getAllInformationForTableViewFromSuperController(String selectedGraphName){
        try {
            return sController.getAllInformationForTableViewFromServer(selectedGraphName);
        }
        catch (IOException ignore){}
        return null;
    }

    public GraphInfoDto getGraphInfoDtoFromSuperController(String selectedGraphName) {
        GraphInfoDto res=null;
        try {
            res= sController.getGraphInfoDtoFromServer(selectedGraphName);
        }
        catch (IOException ignore){}
        return res;
    }
//    public Set<SerialSetInfoTableViewDto> getAllSerialSetsInfoForTableViewFromSuperController(){
//        return sController.getAllSerialSetsInfoForTableViewFromMed();
//    }

    public TargetInfoDto getTargetInfoDtoFromSuper(String targetName,String selectedGraphName) {
        try {
            return sController.getTargetInfoDtoFromServer(targetName, selectedGraphName);
        }
        catch (IOException ignore){}
        return null;
    }

    public void initSubTabs(String selectedGraphNameIn){
      tableViewTabContentController.initAllTablesViews(selectedGraphNameIn);
      treeViewTabContentController.initTargetsInfoTreeView(selectedGraphNameIn);
    }
}


