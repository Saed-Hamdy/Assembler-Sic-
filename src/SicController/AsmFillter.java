package SicController;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * filter for asm files
 *
 * @author said
 */
public class AsmFillter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return false;
        }
        String s = f.getName().toLowerCase();
        return s.endsWith(".asm");

    }

    @Override
    public String getDescription() {
        return ".asm,.ASM";
    }

}
