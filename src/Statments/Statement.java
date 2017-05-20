package Statments;

import SicController.Controller;
import SicController.Pass2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Statement implements IStatement {

    private String query, addressType;
    private Boolean indexed;
    public String LabelName, address, type;
    private String Label = "^(?i)(([a-z](\\w+)?(\\s+))|(\\s*))$";
    private String operation; 
    private String hexNum = "(([a-f]|\\d)+)";
    private String addressrgx = "([a-z](\\w+(,x)?)?)";
    // for any other instruction
    private String oprand = "^(?i)(" + addressrgx + "|" + hexNum + "|(0x" + hexNum + "))\\s*$";
    private List<String> unaddresse;
    Literal liter ;

    public Statement(String s) {
        unaddresse = new ArrayList<>();
        unaddresse.add("rsub");

        type = s.toLowerCase();
        operation = "^(?i)(" + s + ")\\s{" + (7 - s.length()) + "}$";
        if (unaddresse.contains(s)) {
            oprand = "^(\\s*)";
            
        }
        indexed = false;
        addressType = "";
        liter = new Literal();
    }

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

        String oper = query.substring(9, 16);
        pat = Pattern.compile(operation);
        ma = pat.matcher(oper);
        if (!ma.find())
            return false;

        String opran = query.substring(17, query.length() > 35 ? 35 : query.length());
        if(query.charAt(16)=='='){
            addressType="Litral";
            return liter.isValid(opran);
        }else {
            
            pat = Pattern.compile(oprand);
            ma = pat.matcher(opran);
            if (ma.matches()) {

                address = ma.group(1).toLowerCase().trim();

                if (!unaddresse.contains(type)) {
                    pat = Pattern.compile(addressrgx);
                    ma = pat.matcher(opran.trim().toLowerCase());
                    if (ma.matches()) {
                        addressType = "Label";
                        if (opran.trim().toLowerCase().endsWith(",x")) {
                            indexed = true;
                            address = address.substring(0, address.length() - 2);
                        }
                    } else {
                        if (opran.trim().toLowerCase().startsWith("0x"))
                            address = address.substring(2);
                        addressType = "hex";
                    }
                }
                return true;
            }
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
        Controller.programCounter += 3;
        if(addressType=="Litral")
            return liter.executePass1();
        return true;

    }

    @Override
    public Boolean executePass2() {
        switch (addressType) {
        case "Label":
            Integer x;
            if (Controller.SymbleTabls.containsKey(address)) {
                x = Controller.SymbleTabls.get(address);
                if (indexed)
                	x = x | Integer.parseInt("8000",16);
                    address = Integer.toHexString(x);
            } else
                return false;
            break;
        case "hex":
            if (Integer.parseInt(address, 16) >= Integer.parseInt("ffff", 16))
                return false;
            break;
        case "Litral":
            liter.executePass2();
            address=liter.address;
        }
        if(unaddresse.contains(type))
            address = "0000";;
        String s = Pass2.Instruction(type, address);
        Pass2.addToListFile(query + s.substring(1));
        Controller.programCounter += 3;
        return true;

    }
    public static void main(String[] args) {
        
        Statement s=new Statement("add");
        System.out.println(s.isValid("         add    =c'eof'"));
    }
}
