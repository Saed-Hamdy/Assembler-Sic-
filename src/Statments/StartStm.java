package Statments;

import SicController.Controller;
import SicController.Pass2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * name start 10000 // bytes 1�8 label // 9 blank // 10�15 operation code //
 * 16�17 blank // 18�35 operand // 36�66 comment
 *
 */
public class StartStm implements IStatement {

    String startLoc, LabelName, query;
    private String label = "^(?i)(([a-z](\\w+)?(\\s+))|(\\s*))$";
    private String operation = "^(?i)(start)\\s{3}$";
    private String hexNum = "(([a-f]|\\d)+)";
    private String operand = "^(?i)(" + hexNum + "|(0X" + hexNum + "))\\s*$";

    public boolean isValid(String query) {
        this.query = query;
        Pattern pat;
        Matcher ma;

        String la = query.substring(0, 9);
        pat = Pattern.compile(label);
        ma = pat.matcher(la);
        if (!ma.matches())
            return false;
        LabelName = ma.group(1).trim();
        if (LabelName.length() == 0)
            LabelName = "      ";
        String oper = query.substring(9, 17);
        pat = Pattern.compile(operation);
        ma = pat.matcher(oper);
        if (!ma.find())
            return false;

        String opran = query.substring(17, query.length() > 35 ? 35 : query.length());
        pat = Pattern.compile(operand);
        ma = pat.matcher(opran);
        if (ma.matches()) {
            startLoc = ma.group(1);
            return true;
        }
        return false;
    }

    @Override
    public Boolean executePass1() {
        if (Controller.SymbleTabls.containsKey(LabelName))
            return false;
        if (LabelName.length() > 0) {
            Controller.SymbleTabls.put(LabelName.toLowerCase(), Controller.programCounter);
        }
        try {
            Controller.startLocation = Controller.programCounter = Integer.parseInt(startLoc, 16);
        } catch (Exception e) {
            Controller.Pass1Error = true;
            Controller.mediateFile.add("error out of pound \t" + query);
            return true;
        }

        Controller.programName = LabelName;
        Controller.SymbleTabls.put(LabelName, Controller.startLocation);
        Controller.mediateFile.add(startLoc + "    " + query);
        return true;
    }

    @Override
    public Boolean executePass2() {
        Controller.programCounter = Controller.startLocation;
        Pass2.startInstruction(Controller.programName,
                Integer.toHexString(Controller.endLocation - Controller.startLocation), startLoc);

        Pass2.addToListFile(query + "    ");
        return true;

    }

}
