package Statments;

import SicController.Controller;
import SicController.Pass2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reserve implements IStatement {
    private String query;
    public String LabelName, adress, type;
    private String label = "^(?i)(([a-z](\\w+)?(\\s+))|(\\s*))$";
    private String operation;
    private String operand = "^(\\d+)\\s*$";
    int gain, size;

    public Reserve(String s) {
        type = s;
        switch (s) {
        case "resw":
            gain = 3;
            break;
        case "resb":
            gain = 1;
            break;
        }
        operation = "^(?i)(" + s + ")\\s{" + (8 - s.length()) + "}$";
    }

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
            size = gain * Integer.parseInt(ma.group(1));
            return true;
        }
        return false;
    }

    @Override
    public Boolean executePass1() {
        if (Controller.SymbleTabls.containsKey(LabelName))
            return false;
        if (LabelName.length() > 0) {
            Controller.SymbleTabls.put(LabelName, Controller.programCounter);
            if(Controller.unDeclaredSymbols.contains(LabelName))
                Controller.unDeclaredSymbols.remove(LabelName);
        }
        Controller.mediateFile.add(Integer.toHexString(Controller.programCounter)+"\t"+query);

        Controller.programCounter += size;
        // Controller.programCounter +=size;

        return true;
    }

    @Override
    public Boolean executePass2() {
        Pass2.reserve();
        Pass2.addToListFile(query + "      ");
        Controller.programCounter += size;
        return true;
    }

}
