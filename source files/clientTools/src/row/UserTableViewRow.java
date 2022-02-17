package row;

import javafx.beans.property.SimpleStringProperty;

public class UserTableViewRow {
    private SimpleStringProperty userName,role;

    public UserTableViewRow(String userNameIn,String roleIn){
        userName=new SimpleStringProperty(userNameIn);
        role=new SimpleStringProperty(roleIn);
    }

    public String getRole() {return role.get();}
    public SimpleStringProperty roleProperty() {return role;}
    public void setRole(String role) {this.role.set(role);}
    public String getUserName() {return userName.get();}
    public SimpleStringProperty userNameProperty() {return userName;}
    public void setUserName(String userName) {this.userName.set(userName);}
}
