import java.util.*;
import java.io.*;
import java.lang.*;

public class JackAnalyzer
{
    private static final String FILETYPE = ".xml";
    public static void main(String[] args) throws IOException
    {
        if (args[0] == null)
            throw new IllegalArgumentException();
        File filename = new File(args[0]);
        if (filename.isDirectory())
        {
            String directoryName = args[0];
            if (directoryName.charAt(directoryName.length() - 1) != '/')
                directoryName = directoryName + "/";
            File[] directoryListing = filename.listFiles();
            for (File f : directoryListing)
            {
                if (!f.getName().substring(f.getName().length()-4).equals("jack"))
                    continue;
                writeToFile(directoryName + f.getName());
            }
        }
        else
            writeToFile(args[0]);
    }

    public static void writeToFile(String filename) throws IOException
    {
        JackTokenizer createTokens = new JackTokenizer(filename);
        CompilationEngine createXML = new CompilationEngine(createTokens.getFilename());
    }
}