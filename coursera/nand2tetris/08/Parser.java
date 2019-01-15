import java.io.*;

public class Parser {

    protected static final String C_ARITHMETIC = "C_ARITHMETIC";
    protected  static final String C_PUSH = "C_PUSH";
    protected  static final String C_POP = "C_POP";
    protected  static final String C_LABEL = "C_LABEL";
    protected  static final String C_GOTO = "C_GOTO";
    protected  static final String C_IF = "C_IF";
    protected  static final String C_FUNCTION = "C_FUNCTION";
    protected static final String C_RETURN = "C_RETURN";
    protected  static final String C_CALL = "C_CALL"; 

    private BufferedReader br;
    private String currentCommand;
    private String[] tokens = new String[3];
    public Parser (String filename) throws IOException {
      try
      {
        br = new BufferedReader(new FileReader(filename));
      }
      catch(FileNotFoundException ex) 
      {
              System.out.println(
                  "Unable to open file '" + 
                  filename + "'");                
      }
    }

    public boolean hasMoreCommands() throws IOException{
      return ((currentCommand = br.readLine()) != null);
    }

    public void advance() throws IOException{
      for (int i = 0; i < currentCommand.length() - 1; i++) {
        if (currentCommand.charAt(i) == '/')
          if (currentCommand.charAt(i+1) == '/')
            currentCommand = currentCommand.substring(0,i);
      }
      tokens = currentCommand.split("\\s");
      currentCommand = currentCommand.replaceAll("\\s", "");
      if (currentCommand.length() == 0)
        if (this.hasMoreCommands())
          this.advance();
    }

    public String commandType() throws IOException {
      if (currentCommand.equals("add") || currentCommand.equals("sub") ||
        currentCommand.equals("neg") || currentCommand.equals("eq") ||
        currentCommand.equals("gt") || currentCommand.equals("lt") ||
        currentCommand.equals("and") || currentCommand.equals("or") ||
        currentCommand.equals("not"))
        return C_ARITHMETIC;
      if  (currentCommand.contains("push"))
        return C_PUSH;
      if (currentCommand.contains("pop"))
        return C_POP;
      if (currentCommand.contains("label"))
        return C_LABEL;
      if (currentCommand.contains("if-goto"))
        return C_IF;
      if (currentCommand.contains("goto"))
        return C_GOTO;
      if (currentCommand.contains("function"))
        return C_FUNCTION;
      if (currentCommand.contains("call"))
        return C_CALL;
      return C_RETURN;
    }

    public String arg1() throws IOException {
      if (this.commandType() == C_ARITHMETIC)
        return currentCommand;
      if (this.commandType() == C_PUSH)
      {
        for (int i = 0; i < currentCommand.length(); i++)
        {
          if (Character.isDigit(currentCommand.charAt(i)))
            return currentCommand.substring(4, i);
        }
        return currentCommand.substring(4);
      }
      if (this.commandType() == C_POP)
      {
        for (int i = 0; i < currentCommand.length(); i++)
        {
          if (Character.isDigit(currentCommand.charAt(i)))
            return currentCommand.substring(3, i);
        }
        return currentCommand.substring(3);
      }
      if (this.commandType() == C_LABEL)
      {
        return currentCommand.replaceAll("label", "");
      }
      if (this.commandType() == C_GOTO)
      {
        return currentCommand.replaceAll("goto", "");
      }
      if (this.commandType() == C_IF)
      {
        return currentCommand.replaceAll("if-goto", "");
      }
      if (this.commandType() == C_FUNCTION)
      {
        return tokens[1];
      }
      if (this.commandType() == C_CALL)
      {
        return tokens[1];
      }
      return "invalid command";
    } 

    public int arg2() throws IOException{
      return Integer.parseInt(tokens[2]);
    }
  }