package Statments;

import SicController.Pass2;

public class Comment implements IStatement {
    private String query;

    public boolean isValid(String query) {
        this.query = query;
        if (query.trim().startsWith(".") || query.trim().length() == 0)
            return true;

        return false;
    }

    @Override
    public Boolean executePass1() {
        return true;
    }

    @Override
    public Boolean executePass2() {
        Pass2.isComment=true;
        if (query.trim().length() > 1)
            Pass2.addToListFile(query);
        Pass2.isComment =false ;
        return true;
    }

}
