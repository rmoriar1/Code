import java.util.*;
import java.io.*;
import java.lang.*;

public class VMWriter {

    public static BufferedWriter bw;

    public VMWriter(String filename) throws IOException
    {
        bw = new BufferedWriter(new FileWriter(filename));
    } 

    public void writePush(String segment, int index) throws IOException
    {
        bw.write("push " + segment + " " + index + "\n");
    }

    public void writePop(String segment, int index) throws IOException
    {
        bw.write("pop " + segment + " " + index + "\n");
    }

    public void writeArithmetic(String command) throws IOException
    {
        if (command.equals("*"))
        {
            writeCall("Math.multiply", 2);
        }
        else if (command.equals("/"))
        {
            writeCall("Math.divide", 2);
        }
        else if (command.equals("+"))
        {
            bw.write("add\n");
        }
        else if (command.equals("-"))
        {
            bw.write("neg\n");
        }
        else if (command.equals("&gt;"))
        {
            bw.write("gt\n");
        }
        else if (command.equals("&lt;"))
        {
            bw.write("lt\n");
        }
        else if (command.equals("&amp;"))
        {
            bw.write("and\n");
        }
        else if (command.equals("="))
        {
            bw.write("eq\n");
        }
        else if (command.equals("|"))
        {
            bw.write("or\n");
        }
        else if (command.equals("true") || command.equals("false"))
        {
            writePush("constant", 0);
            if (command.equals("true"))
                writeArithmetic("not");
        }
        else if (command.equals("~"))
        {
            writeArithmetic("not");
        }
        else 
            bw.write(command + "\n");
    }

    public void writeLabel(String label) throws IOException
    {
        bw.write("label " + label + "\n");
    }

    public void writeGoto(String label) throws IOException
    {
        bw.write("goto " + label + "\n");
    }

    public void writeIf(String label) throws IOException
    {
        bw.write("if-goto " + label + "\n");
    }

    public void writeCall(String name, int nArgs) throws IOException
    {
        bw.write("call " + name + " " + nArgs + "\n");
    }

    public void writeFunction(String name, int nLocals) throws IOException
    {
        bw.write("function " + name + " " + nLocals + "\n");
    }

    public void writeReturn() throws IOException
    {
        bw.write("return\n");
    }

    public void close() throws IOException
    {
        bw.close();
    }
}