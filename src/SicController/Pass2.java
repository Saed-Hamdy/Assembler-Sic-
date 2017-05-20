package SicController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Pass2 {
    private static Pass2 instance;
    private static String oneTxtRecord, programName;
    private static Integer startAdress;
    private static Integer txtRecordLength;
    private static List<String> output, listcode;
    private static String separator = "^";
    private static int cnt = 0;
    public static boolean isComment;

    private Pass2() {
        startAdress = 0;
        programName = "";
        output = new ArrayList<String>();
        listcode = new ArrayList<>();
        oneTxtRecord = "";
    }

    public static Pass2 getInstance() {
        if (instance == null)
            instance = new Pass2();
        return instance;
    }

    public static void addToListFile(String line) {
        String pcCnt=StartingAddressFieldformat(Integer.toHexString(Controller.programCounter));
        if (isComment)
            pcCnt="      ";
            
        listcode.add(pcCnt.substring(2)+"    " + line);
    }

    public static void writeFiles() {
        writeToFile(programName + " objectFile.txt", output);
        writeToFile(programName + " ListFile.txt", listcode);
    }

    public static void writeToFile(String fileName, List<String> arr) {

        try {
            File file = new File(fileName);
            if (file.createNewFile()) {
                System.out.println("File is created!");
            } else {
                System.out.println("File already exists will be overwritten");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
            while (arr.size() != 0) {
                bw.write(arr.get(0));
                arr.remove(0);
                bw.newLine();
            }
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();

            }
        }
    }

    private static String ProgramNameFieldformat(String str) {
        if (str.length() <= 6) {
            int spaceNumber = 6 - str.length();
            for (int i = 0; i < spaceNumber; i++) {
                str += " ";
            }
        } else {
            throw new RuntimeException();
        }
        return str;
    }

    private static String StartingAddressFieldformat(String str) {
        String ret = "";
        if (str.length() <= 6) {
            int spaceNumber = 6 - str.length();
            for (int i = 0; i < spaceNumber; i++) {
                ret += "0";
            }
        } else {
            throw new RuntimeException();
        }
        return ret + str;
    }

    public static String startInstruction(String progName, String progLength, String operand) {

        startAdress = Controller.programCounter;
        programName = progName.length() > 0 ? progName : programName;
        progName = ProgramNameFieldformat(progName);
        operand = StartingAddressFieldformat(operand);
        String content = "H" + separator + progName + separator + operand + separator
                + StartingAddressFieldformat(progLength);
        output.add(content);
        return "    ";
    }

    public static String EndInstruction(String operand) {
        output.add("T" + separator + StartingAddressFieldformat(Integer.toHexString(startAdress)) + separator
                + Integer.toHexString(txtRecordLength) + oneTxtRecord);
        operand = StartingAddressFieldformat(operand);
        String content = "E" + separator + operand;
        output.add(content);
        return "    ";
    }

    public static String Instruction(String operation, String operand) {
        if (oneTxtRecord.length() == 0) {
            startAdress = Controller.programCounter;
        }
        String opcode;
        if (Reader.opTab.get(operation) == null) {
            throw new RuntimeException();
        } else {
            opcode = Reader.opTab.get(operation);
            String tobeAdded = separator + opcode + operand;
            cnt++;
            if (60 - (oneTxtRecord.length() - cnt) < (tobeAdded.length() - 1)) {
                txtRecordLength = Controller.programCounter - startAdress;
                output.add("T" + separator + StartingAddressFieldformat(Integer.toHexString(startAdress)) + separator
                        + Integer.toHexString(txtRecordLength) + oneTxtRecord);
                oneTxtRecord = "";
                cnt = 0;
                startAdress = Controller.programCounter;
            }
            oneTxtRecord += tobeAdded;
            return tobeAdded;
        }

    }

    public static String reserve() {
        if (oneTxtRecord.length() > 0) {
            txtRecordLength = Controller.programCounter - startAdress;
            output.add("T" + separator + StartingAddressFieldformat(Integer.toHexString(startAdress)) + separator
                    + Integer.toHexString(txtRecordLength) + oneTxtRecord);
            oneTxtRecord = "";
            cnt = 0;
        }
        return "    ";

    }

    private static String addToRecord(String tobeAdded) {
        tobeAdded = separator + tobeAdded;
        if (oneTxtRecord.length() == 0) {
            startAdress = Controller.programCounter;
        }
        if (60 - (oneTxtRecord.length() - cnt) < (tobeAdded.length() - 1)) {
            txtRecordLength = Controller.programCounter - startAdress;
            output.add("T" + separator + StartingAddressFieldformat(Integer.toHexString(startAdress)) + separator
                    + Integer.toHexString(txtRecordLength) + oneTxtRecord);
            oneTxtRecord = "";
            cnt = 0;
            startAdress = Controller.programCounter;
        }
        oneTxtRecord += tobeAdded;
        return tobeAdded;
    }

    public static String wordMethod(String value) {
        String w = "";
        if (value.length() != 6) {
            for (int i = value.length(); i < 6; i++) {
                w += "0";
            }
            w += value;
            return addToRecord(w);
        }
        return addToRecord(value);

    }

    public static String byteMethod(String value, boolean ischaracter) {
        if (!ischaracter) {
            if (value.length()%2==1)
                return addToRecord("0"+value);     
            return addToRecord(value);
        } else {
            String w = "";
            for (int i = 0; i < value.length(); i++) {
                w += Integer.toHexString(Integer.parseInt((int) value.charAt(i) + ""));
            }

            return addToRecord(w);
        }
    }

}
