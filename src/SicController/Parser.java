package SicController;

import Statments.Byte;
import Statments.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    List<IStatement> stms;
    List<IStatement> storageStms;
    IStatement start, end, comment;

    public Parser() {
        start = new StartStm();
        end = new End();
        comment = new Comment();
        stms = new ArrayList<>();
        stms.add(new Statement("add"));
        stms.add(new Statement("and"));
        stms.add(new Statement("comp"));
        stms.add(new Statement("div"));
        stms.add(new Statement("j"));
        stms.add(new Statement("jeq"));
        stms.add(new Statement("jgt"));
        stms.add(new Statement("jlt"));
        stms.add(new Statement("jsub"));
        stms.add(new Statement("lda"));
        stms.add(new Statement("ldch"));
        stms.add(new Statement("ldl"));
        stms.add(new Statement("ldx"));
        stms.add(new Statement("mul"));
        stms.add(new Statement("or"));
        stms.add(new Statement("rd"));
        stms.add(new Statement("rsub"));    // this a different type of statement
        stms.add(new Statement("sta"));
        stms.add(new Statement("stch"));
        stms.add(new Statement("stl"));
        stms.add(new Statement("stx"));
        stms.add(new Statement("sub"));
        stms.add(new Statement("td"));
        stms.add(new Statement("tix"));
        stms.add(new Statement("wd"));

        storageStms = new ArrayList<>();
        storageStms.add(new Reserve("resw"));
        storageStms.add(new Reserve("resb"));
        storageStms.add(new Word());
        storageStms.add(new Byte());
        storageStms.add(new Org());
        storageStms.add(new Equ());
        storageStms.add(new Ltorg());
        
    }

    public Boolean checkPass1(String query) {
        try{
        if (comment.isValid(query))
            return comment.executePass1();
        if (start.isValid(query))
            return false;
        for (IStatement iStatment : stms) {
            if (iStatment.isValid(query)) {
                return iStatment.executePass1();
            }
        }
        for (IStatement stm : storageStms) {
            if (stm.isValid(query)) {
                return stm.executePass1();

            }
        }

        return false;
        }catch(Exception e){
            return false;
        }
    }

    public Boolean checkPass2(String query) {

        if (comment.isValid(query))
            return comment.executePass2();
        for (IStatement iStatment : stms) {
            if (iStatment.isValid(query)) {
                
                return iStatment.executePass2();
            }
        }
        for (IStatement is : storageStms) {
            if (is.isValid(query)) {
               return is.executePass2();
            }
        }

        return false;
    }

}
