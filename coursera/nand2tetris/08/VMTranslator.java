import java.io.*;

public class VMTranslator
{
	protected static final String C_ARITHMETIC = "C_ARITHMETIC";
	protected  static final String C_PUSH = "C_PUSH";
	protected  static final String C_POP = "C_POP";
	protected  static final String C_LABEL = "C_LABEL";
	protected  static final String C_GOTO = "C_GOTO";
	protected  static final String C_IF = "C_IF";
	protected  static final String C_FUNCTION = "C_FUNCTION";
	protected static final String C_RETURN = "C_RETURN";
	protected  static final String C_CALL = "C_CALL"; 

	
	public static void main(String[] args) throws IOException{
		if (args[0].charAt(args[0].length() - 1) == '/')
			args[0] = args[0].substring(0, args[0].length() - 1);
		File filename = new File(args[0]);
		CodeWriter cw = new CodeWriter();
		if (filename.isDirectory())
		{
			cw = new CodeWriter(args[0]);
			cw.writeInit();
			File[] directoryListing = filename.listFiles();
			for (File f : directoryListing)
			{
				if (!f.getName().substring(f.getName().length()-2).equals("vm"))
					continue;
				Parser p = new Parser(args[0] + "/" + f.getName());
				cw.setFileName(f.getName());
				while (p.hasMoreCommands())
				{
					p.advance();
					if (p.commandType() == C_ARITHMETIC)
					{
						cw.writeArithmetic(p.arg1());
					}
					if (p.commandType() == C_POP || p.commandType() == C_PUSH)
					{
						cw.writePushPop(p.commandType(), p.arg1(), p.arg2());
					}
					if (p.commandType() == C_LABEL)
					{
						cw.writeLabel(p.arg1());
					}
					if (p.commandType() == C_GOTO)
					{
						cw.writeGoto(p.arg1());
					}
					if (p.commandType() == C_IF)
					{
						cw.writeIf(p.arg1());
					}
					if (p.commandType() == C_FUNCTION)
					{
						cw.writeFunction(p.arg1(), p.arg2());
					}
					if (p.commandType() == C_CALL)
					{
						cw.writeCall(p.arg1(), p.arg2());
					}
					if (p.commandType() == C_RETURN)
						cw.writeReturn();
				}
			}
		}
		else
		{
			Parser p = new Parser(args[0]);
			cw = new CodeWriter(args[0]);
			cw.writeInit();
			while (p.hasMoreCommands())
			{
				p.advance();
				if (p.commandType() == C_ARITHMETIC)
				{
					cw.writeArithmetic(p.arg1());
				}
				if (p.commandType() == C_POP || p.commandType() == C_PUSH)
				{
					cw.writePushPop(p.commandType(), p.arg1(), p.arg2());
				}
				if (p.commandType() == C_LABEL)
				{
					cw.writeLabel(p.arg1());
				}
				if (p.commandType() == C_GOTO)
				{
					cw.writeGoto(p.arg1());
				}
				if (p.commandType() == C_IF)
				{
					cw.writeIf(p.arg1());
				}
				if (p.commandType() == C_FUNCTION)
				{
					cw.writeFunction(p.arg1(), p.arg2());
				}
				if (p.commandType() == C_CALL)
				{
					cw.writeCall(p.arg1(), p.arg2());
				}
				if (p.commandType() == C_RETURN)
					cw.writeReturn();
			}
		}
		cw.close();
	}
}
