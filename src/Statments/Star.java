package Statments;

import SicController.Controller;

public class Star {
    public String address ;
    
    public boolean isValid(String query) {
        address=Controller.programCounter+"";
        return query.trim().equals("*");
    }
}
