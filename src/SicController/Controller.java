package SicController;

import Statments.Comment;
import Statments.End;
import Statments.Equ;
import Statments.IStatement;
import Statments.Ltorg;
import Statments.StartStm;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Controller {
    public static HashMap<String, Integer> SymbleTabls, LiteralsTable;
    public static Reader rd;
    public static Integer programCounter, startLocation, endLocation, lastOrgLocation;
    public static String programName;
    public static List<String> mediateFile, unFinishedStatements, unDeclaredSymbols;
    public static boolean Pass1Error, orgChanged;
    private static Controller instance;
    private Parser pars;

    private Controller() {
        Pass1Error = false;
        Pass2.getInstance();
        programCounter = 0;
        pars = new Parser();
        SymbleTabls = new HashMap<>();
        LiteralsTable = new HashMap<>();
        rd = new Reader();
        mediateFile = new ArrayList<>();
        unFinishedStatements = new ArrayList<>();
        unDeclaredSymbols = new ArrayList<>();
        rd.generateOpTab("OpTab.txt");
    }

    public static Controller getInstance() {
        if (instance == null)
            instance = new Controller();
        return instance;
    }

    public static void main(String[] args) {

        Controller asm = Controller.getInstance();
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new AsmFillter());
        int sf = fc.showSaveDialog(fc);
        if (sf == JFileChooser.APPROVE_OPTION)
            if (fc.getSelectedFile().getName().toLowerCase().endsWith(".asm"))
                asm.convert(fc.getSelectedFile());

    }

    public void convert(File asmFile) {
        ArrayList<String> code = rd.loadCode(asmFile); // load the code to be
        // converted
        IStatement st = new StartStm();
        IStatement end = new End();
        IStatement comment = new Comment();
        IStatement ltorg = new Ltorg();
        /**
         * pass1
         */
        int i = 0;
        while (comment.isValid(code.get(i))) {
            i++;
        }
        if (st.isValid(code.get(i))) { // check for start Statement
            st.executePass1();
            i++;
        } else {
            startLocation = 0; // set start location to be 0;
        }
        while (!end.isValid(code.get(i)) && i < code.size()) {
            if (!pars.checkPass1(code.get(i))) {
                // throw new RuntimeException("syntax Error" + code.get(i));
                System.out.println("syntax Error" + code.get(i));
                mediateFile.add("syntax Error" + code.get(i));
                Pass1Error = true;
            }
            i++;
        }
        if (end.isValid(code.get(i))) {
            ltorg.executePass1();
            end.executePass1();
        }
        int x = unFinishedStatements.size();
        while (unFinishedStatements.size() > 0 && Pass1Error == false) {
            Equ equ = new Equ();
            for (int index = 0; index < unFinishedStatements.size(); index++) {
                equ.isValid(unFinishedStatements.get(index));
                if (equ.executePass1()) {
                    mediateFile.remove(mediateFile.size() - 1);
                    index--;
                } else
                    Pass1Error = true;
            }
            if (x == unFinishedStatements.size())
                Pass1Error = true;
            x = unFinishedStatements.size();
        }
        if (Pass1Error | !unDeclaredSymbols.isEmpty()) {
            Pass2.writeToFile("Pass1 errors.txt", mediateFile);
            return;
        }
        /**
         * pass2
         */
        endLocation = programCounter;
        programCounter = startLocation; // reset Program counter to execute
        // pass2
        i = 0;
        while (comment.isValid(code.get(i))) {
            comment.executePass2();
            i++;
        }
        if (st.isValid(code.get(i))) {
            st.executePass2();
            i++;
        }

        while (!end.isValid(code.get(i)) && i < code.size()) {

            if (!pars.checkPass2(code.get(i))) {
                Pass2.isComment = true;
                Pass2.addToListFile("Syntax error  in " + code.get(i));
                Pass2.isComment = false;
                // throw new RuntimeException("Error");
            }
            i++;
        }
        if (end.isValid(code.get(i))) {
            ltorg.executePass2();
            end.executePass2();
        }
    }
}
