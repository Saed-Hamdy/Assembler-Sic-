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
public class Org implements IStatement {

    public String LabelName, sympol;
    private List<String> address, addressType;
    boolean reset, pass2;
    private String query;
    private final String Label = "^(\\s*)$";
    private final String operation = "^(?i)(org)\\s{5}$";
    private final String Num = "((\\d)+)";
    private final String addressRgx = "([a-z](\\w+)?)";
    private final String oneOprand = "(" + addressRgx + "|" + Num + ")";
    private final String operand = "^(?i)(" + oneOprand + "(\\s*[+-]\\s*" + oneOprand + ")?)\\s*$";

    public static void main(String[] args) {
        // System.out.println("saiiiid");
        // String s = "sssd + ffff";
        // s = s.replaceAll("\\s*", "");
        // String[] ss = s.split("\\+");
        // address.addAll(Arrays.asList(ss));
        System.out.println(new Org().isValid("         org     aaa -100 "));
    }

    public Org() {
        address = new ArrayList<>();
        addressType = new ArrayList<>();
        sympol = "";
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
        String opran = query.substring(17, query.length() > 35 ? 35 : query.length());
        pat = Pattern.compile(operand);
        ma = pat.matcher(opran);
        if (ma.matches()) {
            if (!setOperation("+", opran))
                if (!setOperation("-", opran)) {
                    address.add(opran.trim());
                    if (Character.isAlphabetic(oper.charAt(0)))
                        addressType.add("Label");
                    else
                        addressType.add("hex");
                }
            reset=false;
            return true;
        } else if (opran.trim().length() == 0) {
            // we should reset PC .
            return reset = true;
        }
        return false;
    }

    private Boolean setOperation(String operation, String operand) {
        if (operand.contains(operation)) {
            String[] ss = operand.replaceAll("\\s*", "").split("\\" + operation);
            address = (Arrays.asList(ss));
            sympol = operation;
            for (String s : address) {
                Pattern pat = Pattern.compile(addressRgx);
                Matcher ma = pat.matcher(s.trim().toLowerCase());
                if (ma.matches()) {
                    addressType.add("Label");
                } else {
                    addressType.add("hex");
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
        if (reset) {
            Controller.programCounter = Controller.lastOrgLocation;
            Controller.orgChanged = false;
        } else {
            Controller.lastOrgLocation = Controller.programCounter;
            int x;
            if (addressType.get(0).equals("Label")) {
                if (!Controller.SymbleTabls.containsKey(address.get(0)))
                    return false;
                x = Controller.SymbleTabls.get(address.get(0));
            } else
                x = Integer.parseInt(address.get(0));
            int sign = 1;
            if (address.size() > 1) {
                if (sympol == "-")
                    sign = -1;
                if (addressType.get(1).equals("Label")) {
                    if (!Controller.SymbleTabls.containsKey(address.get(1)))
                        return false;
                    x += (sign) * Controller.SymbleTabls.get(address.get(1));
                } else
                    x += (sign) * Integer.parseInt(address.get(1));
            }
            if (x < Controller.startLocation | x > Controller.programCounter)
                return false;
            Controller.programCounter = x;
            Controller.orgChanged = true;
        }

        return true;

    }

    @Override
    public Boolean executePass2() {
        Pass2.addToListFile(query);
        pass2 = true;
        return executePass1();
    }
}
