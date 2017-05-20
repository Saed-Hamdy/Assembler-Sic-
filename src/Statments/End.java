package Statments;

import SicController.Controller;
import SicController.Pass2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class End implements IStatement {

    private String adress, addressType, LabelName, query;
    private String label = "^(?i)(([a-z](\\w+)?(\\s+))|(\\s*))$";
    private String operation = "^(?i)(end)\\s{5}$";
    private String hexNum = "(([a-f]|\\d)+)";
    private String addressRex = "([a-z](\\w+)?)";
    private String operand = "^(?i)(" + addressRex + "|" + hexNum + "|(0x" + hexNum + "))\\s*$";

    @Override
    public boolean isValid(String query) {
        Pattern pat;
        Matcher ma;
        this.query = query;
        String la = query.substring(0, 9);
        pat = Pattern.compile(label);
        ma = pat.matcher(la);
        if (!ma.matches())
            return false;
        LabelName = ma.group(1).trim();

        String oper = query.substring(9, 17);
        pat = Pattern.compile(operation);
        ma = pat.matcher(oper);
        if (!ma.find())
            return false;

        String opran = query.substring(17, query.length() > 35 ? 35 : query.length());
        pat = Pattern.compile(operand);
        ma = pat.matcher(opran);
        if (ma.matches()) {
            adress = ma.group(1).toLowerCase();
            pat = Pattern.compile(addressRex);
            ma = pat.matcher(opran.trim().toLowerCase());
            if (ma.matches()) {
                addressType = "label";
            } else {
                if (opran.trim().toLowerCase().startsWith("0x"))
                    adress = adress.substring(2);
                addressType = "hex";
            }

            return true;
        }
        return false;
    }

    @Override
    public Boolean executePass1() {

        switch (addressType) {
        case "label":
            if (Controller.SymbleTabls.containsKey(adress)
                    && Controller.SymbleTabls.get(adress) <= Controller.programCounter) {
                Controller.endLocation = Controller.programCounter;

            } else
                return false;
            break;
        case "hex":
            if (!(Integer.parseInt(adress, 16) <= Controller.programCounter)) {
                return false;
            }else
                Controller.endLocation = Controller.programCounter;
            break;
        }
        if (LabelName.length() > 0){
            Controller.SymbleTabls.put(LabelName, Controller.programCounter);
            if(Controller.unDeclaredSymbols.contains(LabelName))
                Controller.unDeclaredSymbols.remove(LabelName);
        }
        Controller.mediateFile.add("\t" + query);
        return true;
    }

    @Override
    public Boolean executePass2() {
        //System.out.println(adress);
        if (addressType == "label") {
            if (Controller.SymbleTabls.containsKey(adress))
                Pass2.EndInstruction(Integer.toHexString(Controller.SymbleTabls.get(adress)));
            else {
                System.out.println("end error");
                Pass2.isComment = true;
                Pass2.addToListFile("syntax Error in " + query);
                Pass2.isComment = false;
                return false;
            }
        } else {
            Pass2.EndInstruction(adress);
           
        }
        Pass2.addToListFile(query + "    ");
        Pass2.writeFiles();

        return true;
    }

}
