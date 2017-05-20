package Statments;

public interface IStatement {

    public boolean isValid(String query);
    public Boolean executePass1();
    public Boolean executePass2();
    
}
