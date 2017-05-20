package Statments;

import SicController.Controller;
import SicController.Pass2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Byte implements IStatement {
    private String query, data;
    private int size;
    private Boolean character;
    public String LabelName, adress;
    private String label = "^(?i)(([a-z](\\w+)?(\\s+))|(\\s*))$";
    private String operation = "^(?i)(byte)\\s{4}$";
    private String hexNum = "x'(([a-f]|\\d)+)'";
    private String characters = "c'(.+)'";
    // for any other instruction
    private String operand = "^(?i)(" + hexNum + "|" + characters + ")\\s*$";

    @Override
    public boolean isValid(String query) {
        this.query = query;
        Pattern pat;
        Matcher ma;

        String la = query.substring(0, 9);
        pat = Pattern.compile(label);
        ma = pat.matcher(la);
        if (!ma.matches())
            return false;
        LabelName = ma.group(1).trim().toLowerCase();

        String oper = query.substring(9, 17);
        pat = Pattern.compile(operation);
        ma = pat.matcher(oper);
        if (!ma.find())
            return false;

        String opran = query.substring(17, query.length() > 35 ? 35 : query.length());
        pat = Pattern.compile(operand);
        ma = pat.matcher(opran);
        if (ma.matches()) {
            adress = ma.group(1).trim();
            if (adress.toLowerCase().startsWith("c'"))
                character = true;
            else
                character = false;
            data = adress.substring(2, opran.trim().length() - 1);
            return true;
        }
        return false;
    }

    @Override
    public Boolean executePass1() {
        size = character ? data.length() : data.length() / 2;
        if (Controller.SymbleTabls.containsKey(LabelName))
            return false;
        if (LabelName.length() > 0) {
            Controller.SymbleTabls.put(LabelName, Controller.programCounter);
            if(Controller.unDeclaredSymbols.contains(LabelName))
                Controller.unDeclaredSymbols.remove(LabelName);
        }
        Controller.mediateFile.add(Integer.toHexString(Controller.programCounter) + "\t" + query);
        Controller.programCounter += size;

        return true;
    }

    @Override
    public Boolean executePass2() {
        size = character ? data.length() : data.length() / 2;
        String s = Pass2.byteMethod(data, character);
        Pass2.addToListFile(query + s.substring(1));
        Controller.programCounter += size;
        return true;
    }
}
