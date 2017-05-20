package Statments;

import java.util.ArrayList;
import java.util.List;
import SicController.Pass2;

import SicController.Controller;

public class Ltorg implements IStatement {
    private static List<ArrayList<String>> list;
    private static int counter;
    private String query;

    public Ltorg() {
        if (list == null)
            list = new ArrayList<>();
        counter = 0;
    }

    public static String twosCompHexConverter(String hex) {
        int length = hex.length() + 1;
        int intVal = Integer.parseInt(hex, 16);
        String sol = Integer.toHexString((-1 * intVal));
        return sol.substring(sol.length() - length);
    }

    @Override
    public boolean isValid(String query) {
        this.query = query;
        if (!(query.substring(0, 9).trim().length() == 0))
            return false;

        if (!(query.substring(15, 35).trim().length() == 0))
            return false;
        if (!(query.substring(9, 15).trim().equalsIgnoreCase("LTORG")))
            return false;
        return true;
    }

    @Override
    public Boolean executePass1() {
        List<String> arr = Controller.unDeclaredSymbols;
        list.add((ArrayList<String>) arr);
        for (int i = 0; i < arr.size(); i++) {
            if (!Controller.LiteralsTable.containsKey(arr.get(i))) {
                Controller.LiteralsTable.put(arr.get(i), Controller.programCounter);
                if ((arr.get(i).charAt(0) + "").equalsIgnoreCase("C")) {
                    Controller.programCounter += arr.get(i).length() - 3;
                } else {
                    Controller.programCounter += (int) Math.ceil((arr.get(i).length() - 3) / 2);
                }
            }
        }
        Controller.unDeclaredSymbols = new ArrayList<>();
        return true;
    }

    @Override
    public Boolean executePass2() {
        Pass2.isComment = true;
        if (query != null)
            Pass2.addToListFile("\n" + query);
        if (!list.get(counter).isEmpty())
            Pass2.addToListFile(" .\t\t Some Literals added Here ");
        Pass2.isComment = false;
        for (String s : list.get(counter)) {
            String str;
            if ((s.charAt(0) + "").equalsIgnoreCase("C")) {
                Controller.programCounter += s.length() - 3;
                str = Pass2.byteMethod(s.substring(2, s.length() - 1), true);

            } else {
                Controller.programCounter += (int) Math.ceil((s.length() - 3) / 2);
                str = Pass2.byteMethod(s.substring(2, s.length() - 1), false);
            }
            Pass2.addToListFile(" .\t\t" + s + "\t writen in memory\t\t\t\t\t\t\t\t\t" + str.substring(1));
        }
        Pass2.isComment = true;
        Pass2.addToListFile("\n");
        counter++;
        Pass2.isComment = false;

        return true;
    }

}
