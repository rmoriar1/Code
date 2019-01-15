import java.io.*;

public class CodeWriter {
    protected static final String C_ARITHMETIC = "C_ARITHMETIC";
    protected  static final String C_PUSH = "C_PUSH";
    protected  static final String C_POP = "C_POP";
    protected  static final String C_LABEL = "C_LABEL";
    protected  static final String C_GOTO = "C_GOTO";
    protected  static final String C_IF = "C_IF";
    protected  static final String C_FUNCTION = "C_FUNCTION";
    protected static final String C_RETURN = "C_RETURN";
    protected  static final String C_CALL = "C_CALL"; 

    private BufferedWriter bw;
    public String filename; 
    private int count = 16;
    private int retNum = 0;
    public CodeWriter() {}
    public CodeWriter(String filename) throws IOException{
      String outputFile = filename;
      for (int i = outputFile.length() - 1; i > 0; i--)
      {
        if (outputFile.charAt(i) == '.')
        {
          outputFile = outputFile.substring(0, i);
          break;
        }
        if (outputFile.charAt(i) == '/')
        {
          outputFile = outputFile + outputFile.substring(i);
          break;
        }
      }
      outputFile = outputFile + ".asm";
      bw = new BufferedWriter(new FileWriter(outputFile));
      this.filename = outputFile;
    }

    public void setFileName(String fileName) {
      this.filename = fileName;
    }

    public void writeInit() throws IOException {
      bw.write("@256\n");
      bw.write("D = A\n");
      bw.write("@0\n");
      bw.write("M=D\n");
      writeCall("Sys.init" , 0);
    }

    public void writeLabel(String command) throws IOException {
      bw.write("(" + command + ")\n");
    }

    public void writeGoto(String command) throws IOException {
      bw.write("@" + command + "\n");
      bw.write("0 ; JMP\n");
    }

    public void writeIf(String command) throws IOException {
      bw.write("@SP\n"); 
      bw.write("A = M - 1\n"); //Get addr top of stack;
      bw.write("D = M\n");
      bw.write("@SP\n"); 
      bw.write("M = M - 1\n"); //SP--;
      bw.write("@" + command + "\n");// Check if it's == 0
      bw.write("D ; JNE\n"); // if top of stack (M[A]) != 0 the test condition was true, so jump
    }

    public void writeFunction(String command, int numVars) throws IOException {
      writeLabel(command);
      for (int i = 0; i < numVars; i++)
      {
        writePushPop(C_PUSH, "constant", 0);
      }
    }

    public void writeSPPush() throws IOException
    {
      bw.write("@SP\n");
      bw.write("A = M\n");
      bw.write("M = D\n");
      bw.write("@SP\n");
      bw.write("M = M + 1\n");
    }

    public void writeCall(String command, int numArgs) throws IOException {
      /* Push retAddr label;
      push LCL
      push ARG
      push THIS
      push THAT
      ARG = SP - 5 - nArgs;
      LCL = SP;
      goto retAddr;
      */
      bw.write("@RETADDR" + retNum + "\n");
      bw.write("D = A\n");
      writeSPPush();

      bw.write("@LCL\n");
      bw.write("D = M\n");
      writeSPPush();

      bw.write("@ARG\n");
      bw.write("D=M\n");
      writeSPPush();

      bw.write("@THIS\n");
      bw.write("D=M\n");
      writeSPPush();

      bw.write("@THAT\n");
      bw.write("D=M\n");
      writeSPPush();

      bw.write("@" + numArgs + "\n");
      bw.write("D=A\n");
      bw.write("@5\n");
      bw.write("D=D+A\n");
      bw.write("@SP\n");
      bw.write("D = M - D\n");
      bw.write("@ARG\n");
      bw.write("M = D\n");

      bw.write("@SP\n");
      bw.write("D = M\n");
      bw.write("@LCL\n");
      bw.write("M = D\n");

      writeGoto(command);

      writeLabel("RETADDR" + retNum);
      retNum++;
    }

    public void writeReturn() throws IOException {
      /* endFrame = LCL;
      retAddr = *(endFrame - 5);
      SP = ARG + 1;
      THAT = *(endFrame - 1);
      THIS = *(endFrame - 2);
      ARG = *(endFrame - 3);
      LCL = *(endFrame - 4);
      goto retAddr;
      */
      bw.write("@LCL\n");
      bw.write("D = M\n");
      bw.write("@FRAME\n");
      bw.write("MD = D\n");

      bw.write("@5\n");
      bw.write("AD = D - A\n");
      bw.write("D = M\n");
      bw.write("@RETADDR\n");
      bw.write("M = D\n");

      bw.write("@SP\n");
      bw.write("A = M - 1\n");
      bw.write("D = M\n");
      bw.write("@ARG\n");
      bw.write("A = M\n");
      bw.write("M = D\n");

      bw.write("@ARG\n");
      bw.write("D = M\n");
      bw.write("@SP\n");
      bw.write("M = D + 1\n");

      bw.write("@FRAME\n");
      bw.write("AM = M-1\n");
      bw.write("D = M\n");
      bw.write("@THAT\n");
      bw.write("M=D\n");

      bw.write("@FRAME\n");
      bw.write("AM = M-1\n");
      bw.write("D = M\n");
      bw.write("@THIS\n");
      bw.write("M=D\n");

      bw.write("@FRAME\n");
      bw.write("AM = M-1\n");
      bw.write("D = M\n");
      bw.write("@ARG\n");
      bw.write("M=D\n");

      bw.write("@FRAME\n");
      bw.write("AM = M-1\n");
      bw.write("D = M\n");
      bw.write("@LCL\n");
      bw.write("M=D\n");

      bw.write("@RETADDR\n");
      bw.write("AM = M\n");
      bw.write("0 ; JMP\n");
    }

    public void writeArithmetic(String command) throws IOException {
      if (command.equals("add"))
      {
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = Stack pointer - 1
        bw.write("D = M\n"); //D = Value of A (2nd operand)
        bw.write("@SP\n"); 
        bw.write("M = M - 1\n"); //SP--;
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = SP-1 (Address of 1st operand)
        bw.write("M = M + D\n"); //Value of A += D (1st + 2nd);
      }
      if (command.equals("sub"))
      {
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = Stack pointer - 1
        bw.write("D = M\n"); //D = Value of A (2nd operand)
        bw.write("@SP\n"); 
        bw.write("M = M - 1\n"); //SP--;
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = SP-1 (Address of 1st operand)
        bw.write("M = M - D\n"); //Value of A += D (1st + 2nd);
      }
      if (command.equals("neg"))
      {
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = Stack pointer - 1
        bw.write("M = -M\n"); //D = 0 - M[A](negate)
      }
      if (command.equals("eq"))
      {
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = Stack pointer - 1
        bw.write("D = M\n"); //D = Value of A (2nd operand)
        bw.write("@SP\n"); 
        bw.write("M = M - 1\n"); //SP--;
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = SP-2 (Address of 1st operand)
        bw.write("MD = M - D\n"); //Value of M[A] -= D (1st + 2nd);
        bw.write("@Equal" + count + "\n");
        bw.write("D ; JEQ\n"); //IF M== 0 jump
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n");
        bw.write("M = -1\n"); //IF M != 0 M[A] = 111111
        bw.write("(Equal" + count + ")\n");
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n");
        bw.write("M = !M\n");
        count++;
      }
      if (command.equals("gt"))
      {
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = Stack pointer - 1
        bw.write("D = M\n"); //D = Value of A (2nd operand)
        bw.write("@SP\n"); 
        bw.write("M = M - 1\n"); //SP--;
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = SP-2 (Address of 1st operand)
        bw.write("MD = M - D\n"); //Value of M[A] -= D (1st + 2nd);
        bw.write("@Greater" + count + "\n");
        bw.write("D ; JGT\n"); //IF M > 0 jump
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n");
        bw.write("M = 0\n");
        bw.write("@End" + count + "\n");
        bw.write("0 ; JEQ\n");
        bw.write("(Greater" + count + ")\n");
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n");
        bw.write("M = -1\n");
        bw.write("(End" + count + ")\n");
        count++;
      }
      if (command.equals("lt"))
      {
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = Stack pointer - 1
        bw.write("D = M\n"); //D = Value of A (2nd operand)
        bw.write("@SP\n"); 
        bw.write("M = M - 1\n"); //SP--;
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = SP-2 (Address of 1st operand)
        bw.write("MD = M - D\n"); //Value of M[A] -= D (1st + 2nd);
        bw.write("@Less" + count + "\n");
        bw.write("D ; JLT\n"); //IF M > 0 jump
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n");
        bw.write("M = 0\n");
        bw.write("@End" + count + "\n");
        bw.write("0 ; JEQ\n");
        bw.write("(Less" + count + ")\n");
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n");
        bw.write("M = -1\n");
        bw.write("(End" + count + ")\n");
        count++;
      }
      if (command.equals("and"))
      {
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = Stack pointer - 1
        bw.write("D = M\n"); //D = Value of A (2nd operand)
        bw.write("@SP\n"); 
        bw.write("M = M - 1\n"); //SP--;
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = SP-2 (Address of 1st operand)
        bw.write("M = M & D\n"); //Value of M[A] -= D (1st + 2nd);
      }
      if (command.equals("or"))
      {
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = Stack pointer - 1
        bw.write("D = M\n"); //D = Value of A (2nd operand)
        bw.write("@SP\n"); 
        bw.write("M = M - 1\n"); //SP--;
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = SP-2 (Address of 1st operand)
        bw.write("M = M | D\n"); //Value of M[A] -= D (1st + 2nd);
      }
      if (command.equals("not"))
      {
        bw.write("@SP\n"); 
        bw.write("A = M - 1\n"); //A = Stack pointer - 1
        bw.write("M = !M\n"); //D = 0 - M[A](negate)
      }
    }

    public void writePushPop(String command, String segment, int index) throws IOException{
      if (segment.equals("local"))
        segment = "LCL";
      if (segment.equals("argument"))
        segment = "ARG";
      if (segment.equals("this"))
        segment = "THIS";
      if (segment.equals("that"))
        segment = "THAT";
      if (segment.equals("temp"))
        index += 5;
      if (segment.equals("pointer"))
      {
        if (index == 0)
          segment = "THIS";
        else
          segment = "THAT";
        if (command.equals("C_PUSH"))
        {
          bw.write("@" + segment + "\n");
          bw.write("D = M\n");
          bw.write("@SP\n"); 
          bw.write("A = M\n");
          bw.write("M = D\n");
          bw.write("@SP\n");
          bw.write("M = M + 1\n"); 
        }
        else
        {
          bw.write("@SP\n");
          bw.write("M = M - 1\n");
          bw.write("A = M\n");
          bw.write("D = M\n");
          bw.write("@" + segment + "\n");
          bw.write("M = D\n");
        }
        return;
      }
      if (segment.equals("static"))
      {
        String var = this.filename;
        var = var.substring(0, var.length() - 3);
        var += "." + index;
        if (command.equals("C_PUSH"))
        {
          bw.write("@" + var +"\n");
          bw.write("D = M\n");
          bw.write("@SP\n"); 
          bw.write("A = M\n");
          bw.write("M = D\n");
          bw.write("@SP\n");
          bw.write("M = M + 1\n"); 
        }
        else
        {
          bw.write("@SP\n");
          bw.write("M = M - 1\n");
          bw.write("A = M\n");
          bw.write("D = M\n");
          bw.write("@" + var + "\n");
          bw.write("M = D\n");
        } 
        return;
      }
      if (command.equals("C_PUSH"))
      {
        //M[SP] = M[M[segment] + index]
        //SP++
        bw.write("@" + index + "\n");
        if (segment.equals("temp"))
        {
          bw.write("D = M\n");
        }
        else
        {
          bw.write("D = A\n");
          if (!segment.equals("constant"))
          {
            bw.write("@" + segment + "\n");
            bw.write("A = M + D\n");
            bw.write("D = M\n");
          }
        }
        bw.write("@SP\n"); 
        bw.write("A = M\n");
        bw.write("M = D\n");
        bw.write("@SP\n");
        bw.write("M = M + 1\n"); 
      }
      if (command.equals("C_POP"))
      {
        if (segment.equals("temp"))
        {
          bw.write("@SP\n");
          bw.write("M = M - 1\n");
          bw.write("A = M\n");
          bw.write("D = M\n");
          bw.write("@" + index + "\n");
          bw.write("M = D\n");
        }
        else
        {
          bw.write("@" + index + "\n");
          bw.write("D = A\n");
          bw.write("@" + segment + "\n");
          bw.write("D = M + D\n");
          bw.write("@addr" + count + "\n");
          bw.write("M = D\n");
          bw.write("@SP\n");
          bw.write("M = M - 1\n");
          bw.write("A = M\n");
          bw.write("D = M\n");
          bw.write("@addr" + count + "\n");
          bw.write("A = M\n");
          bw.write("M = D\n");
          count++;
        }
      }
    }

    public void close() throws IOException{
      bw.close();
    }
  }
