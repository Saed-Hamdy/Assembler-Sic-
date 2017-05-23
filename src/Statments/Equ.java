package Statments;

import SicController.Controller;
import SicController.Pass2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by said on 10/05/2017.
 */
public class Equ implements IStatement {
    public String LabelName, sympol;
    private List<String> address, addressType;
    private boolean pass2;
    private String query;
    private String Label = "^(?i)(([a-z](\\w+)?(\\s+)))$";
    private String operation = "^(?i)(equ)\\s{5}$";
    private String Num = "((\\d)+)";
    private String addressRgx = "([a-z](\\w+)?)";
    private String oneOprand = "(" + addressRgx + "|" + Num + ")";
    private String operand = "^(?i)(" + oneOprand + "(\\s*[+-]\\s*" + oneOprand + ")?)\\s*$";
    private Star star;

    public Equ() {
        address = new ArrayList<>();
        addressType = new ArrayList<>();
        sympol = "";
        pass2 = false;
        star = new Star();
    }

    @Override
    public boolean isValid(String query) {
        this.query = query;
        Pattern pat;
        Matcher ma;
        String la = query.substring(0, 9);
        pat = Pattern.compile(Label);
        ma = pat.matcher(la);
        if (!ma.matches())
            return false;

        LabelName = ma.group(1).trim().toLowerCase();
        String oper = query.substring(9, 17);
        pat = Pattern.compile(operation);
        ma = pat.matcher(oper);
        if (!ma.find())
            return false;

        String opran = query.substring(17, query.length() > 35 ? 35 : query.length()).trim().toLowerCase();
        if (star.isValid(opran)) {
            address.add(star.address);
            addressType.add("num");
            return true;
        } else {
            pat = Pattern.compile(operand);
            ma = pat.matcher(opran);
            if (ma.matches()) {
                if (!setOperation("+", opran))
                    if (!setOperation("-", opran)) {
                        address.add(opran.trim());
                        if (Character.isAlphabetic(oper.charAt(0)))
                            addressType.add("Label");
                        else
                            addressType.add("num");
                    }

                return true;
            }
        }
        return false;
    }

    private Boolean setOperation(String operation, String operand) {
        if (operand.contains(operation)) {
            String[] ss = operand.replaceAll("\\s*", "").split("\\" + operation);
            address = Arrays.asList(ss);
            sympol = operation;
            for (String s : address) {
                Pattern pat = Pattern.compile(addressRgx);
                Matcher ma = pat.matcher(s.trim().toLowerCase());
                if (ma.matches()) {
                    addressType.add("Label");
                } else {
                    addressType.add("num");
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public Boolean executePass1() {
        if (Controller.SymbleTabls.containsKey(LabelName) && !pass2)
            return false;
        if (!pass2)
            Controller.mediateFile.add("\t\t" + query);
        if (LabelName.length() == 0)
            return true;

        boolean hasunDeclairedSymboles = false;
        for (int i = 0; i < address.size(); i++) {
            if (addressType.get(i).equals("Label"))
                if (!Controller.SymbleTabls.containsKey(address.get(i))) {
                    Controller.unDeclaredSymbols.add(address.get(i));
                    hasunDeclairedSymboles = true;
                } else
                    address.set(i, Controller.SymbleTabls.get(address.get(i)) + "");

        }
        if (hasunDeclairedSymboles) {
            Controller.unFinishedStatements.add(query);
        } else {
            int x = Integer.parseInt(address.get(0));

            switch (sympol) {
            case "+":
                x += Integer.parseInt(address.get(1));
                break;
            case "-":
                x -= Integer.parseInt(address.get(1));
            }
            Controller.SymbleTabls.put(LabelName, x);
            if (Controller.unFinishedStatements.contains(query))
                Controller.unFinishedStatements.remove(query);
            if (Controller.unDeclaredSymbols.contains(LabelName))
                Controller.unDeclaredSymbols.remove(LabelName);
        }
        return true;
    }

    @Override
    public Boolean executePass2() {
        Pass2.isComment = true;
        Pass2.addToListFile(query);
        Pass2.isComment = false;
        pass2 = true;
        return executePass1();
    }
}
