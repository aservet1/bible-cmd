import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import java.util.Date;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.MalformedInputException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

class TestLaTeX {

    static void log(String msg) {
    	System.out.printf(" >> %s\n",msg);
    }

    public static void main(String[] args) {

	File temporaryDir = null;
	try {
	    temporaryDir = createTempDirectory("TestLaTeX-working-directory");
	} catch(IOException ex) {
	    ex.printStackTrace();
	    System.exit(1);
	}
	
	File tsvFile = new File(temporaryDir, "text-version.tsv" );
	File texFile = new File(temporaryDir, "latex-version.tex");
	File pdfFile = new File(temporaryDir, "latex-version.pdf");

	try {
		tsvFile.createNewFile();
		texFile.createNewFile();
		pdfFile.createNewFile();
	} catch (IOException ex) {
		ex.printStackTrace();
		System.exit(1);
	}

	temporaryDir.deleteOnExit();
	tsvFile.deleteOnExit();
	texFile.deleteOnExit();
	pdfFile.deleteOnExit();

	String cmd = String.join(" ", args);
	log("will run: " + cmd);
	String tsv = runShellCmd(cmd);
        writeStringToFile(
	    tsv,
	    tsvFile
	);
	log("output: " + tsv);
	log("tsvFile: " + tsvFile.getAbsolutePath());
	String texText = runShellCmd(
            // String.format(
	    //     "cat %s | ./aux-scripts/text-to-latex.py",
	    //     tsvFile.getAbsolutePath()
	    // )
	    String.format(
	        "./aux-scripts/text-to-latex.py %s",
	        tsvFile.getAbsolutePath()
	    )
	);
	log(texText);
	writeStringToFile(
            texText,
	    texFile
	);
	log("wrote to tex file");
	String pdflatexOutput = runShellCmd(
	    String.format(
	        "pdflatex --output-directory %s %s",
	        temporaryDir.getAbsolutePath(),
	        texFile.getAbsolutePath()
	    )
	);
	runShellCmd(String.format("mv %s /home/ale -v", pdfFile.getAbsolutePath()));
    }

    private static File createTempDirectory(String prefix) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
	System.out.println("Temp dir: " + tempDir);
        File generatedDir = new File(tempDir, prefix + System.nanoTime());
        if (!generatedDir.mkdir())
            throw new IOException("Failed to create temp directory " + generatedDir.getName());
        return generatedDir;
    }

    private static void writeStringToFile(String content, File file) {             
        try {
	    FileOutputStream     fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(content.getBytes());
            bos.close(); fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readStringFromProcessStdout(Process p) {
        try {
            BufferedReader stdoutReader = new BufferedReader(
                new InputStreamReader(
                    p.getInputStream()
                )
            );
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = stdoutReader.readLine()) != null) {
                output.append(line);
                output.append("\n");
            }
            int exitVal = p.waitFor();
            if (exitVal == 0) {
                String outputString = output.toString();
                if (outputString.length() > 0) {
                    return outputString.strip();
                } else {
                    return outputString;
                }
            } else {
                // return String.format("command '%s' failed with exit code %d", cmd, exitVal);
                return String.format("command failed with exit code %d", exitVal);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return "IOException with message: " + e.getMessage();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            return "InterruptedException with message: " + e.getMessage();
        }
    }

    public static String runShellCmd(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            return readStringFromProcessStdout(process);
        }
        catch (IOException e) {
            e.printStackTrace();
            return " -- error in runShellCmd("+cmd+"): IOException with message: " + e.getMessage();
        }
    }

}
