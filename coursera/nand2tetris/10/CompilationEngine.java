import java.util.*;
import java.io.*;
import java.lang.*;

public class CompilationEngine
{
    private static final String FILETYPE = ".xml";
    private static final String KEYWORD = "keyword";
    private static final String SYMBOL = "symbol";
    private static final String IDENTIFIER = "identifier";
    private static final String INT_CONST = "integerConstant";
    private static final String STRING_CONST = "stringConstant";
    private static final String CLASS = "class";
    private static final String METHOD = "method";
    private static final String FUNCTION = "function";
    private static final String CONSTRUCTOR = "constructor";
    private static final String INT = "int";
    private static final String BOOLEAN = "boolean";
    private static final String CHAR = "char";
    private static final String VOID = "void";
    private static final String VAR = "var";
    private static final String STATIC = "static";
    private static final String FIELD = "field";
    private static final String LET = "let";
    private static final String DO = "do";
    private static final String IF = "if";
    private static final String ELSE = "else";
    private static final String WHILE = "while";
    private static final String RETURN = "return";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String NULL = "null";
    private static final String THIS = "this";
    private static final String [] OPLIST = {"+", "-", "*", "/", "|",  
    "=", "~", "&lt;", "&gt;", "&quot;", "&amp;"};

    private static BufferedReader br;
    private static BufferedWriter bw;
    private static String currentString;
    private static String currentClassification;
    private static String currentToken;
    private static String nextToken;
    private static String className;
    private static int indentLevel = 0;

    public CompilationEngine(String filename) throws IOException
    {
        try
        {
            br = new BufferedReader(new FileReader(filename));
        }
        catch(FileNotFoundException ex)
        {
            System.out.println("Unable to open file: " + filename);
        }
        filename = filename.substring(0, filename.length() - 5);
        filename = filename + FILETYPE;
        bw = new BufferedWriter(new FileWriter(filename));
        advance();
        compileClass();
        bw.close();
    }

    public String isolateClassification() throws IOException
    {
        String s = "";
        int j = 0;
        for (int i = 0; i < currentString.length(); i++)
        {
            if (currentString.charAt(i) == '<')
            {
                j = i;
            }
            if (currentString.charAt(i) == '>')
            {
                s = currentString.substring(j+1, i);
                break;
            }
        }
        return s;
    }

    public String isolateToken() throws IOException
    {
        String s = "";
        int j = 0;
        for (int i = 1; i < currentString.length(); i++)
        {
            if (currentString.charAt(i) == '>')
                j = i;
            if (currentString.charAt(i) == '<')
            {
                s = currentString.substring(j+1, i).replaceAll("\\s", "");
                break;
            }
        }
        return s;
    }

    public void writeIndent(int i) throws IOException
    {
        for (int j = 0; j < i; j++)
            bw.write("  ");
    }

    public void indentWriteAdvance() throws IOException
    {
        writeIndent(indentLevel);
        bw.write(currentString + "\n");
        advance();
    }

    public void advance() throws IOException
    {
        if (br.ready())
        {
            currentString = br.readLine();
            currentClassification = isolateClassification();
            currentToken = isolateToken();
        }
    }

    public void compileClass() throws IOException
    {
        bw.write("<class>\n");
        indentLevel++;
        // Skip <tokens>
        advance();
        // Write 'class'
        indentWriteAdvance();
        // Write className and save
        className = currentToken;
        indentWriteAdvance();
        // Write '{'
        indentWriteAdvance();
        // Write 0 or more classVarDec
        while (currentToken.equals(STATIC) || currentToken.equals(FIELD))
        {
            compileClassVarDec();
        }
        // Write 0 or more subroutineDex
        while (currentToken.equals(CONSTRUCTOR) || currentToken.equals(FUNCTION)
            || currentToken.equals(METHOD))
        {
            compileSubroutineDec();
        }
        // Write '}'
        indentWriteAdvance();
        // Skip </tokens>
        advance();
        indentLevel--;
        bw.write("</class>\n");
    }

    public void compileClassVarDec() throws IOException
    {
        writeIndent(indentLevel);
        bw.write("<classVarDec>\n");
        indentLevel++;
        // Write ('static' | 'field')
        indentWriteAdvance();
        // Write type
        indentWriteAdvance();
        // Write varName
        indentWriteAdvance();
        // Write 0 or more (',' varName)
        while (currentToken.equals(","))
        {
            // Write (',')
            indentWriteAdvance();
            // Write varName
            indentWriteAdvance();
        }
        // Write (';')
        indentWriteAdvance();
        indentLevel--;
        writeIndent(indentLevel);
        bw.write("</classVarDec>\n");
    }

    public void compileSubroutineDec() throws IOException
    {
        writeIndent(indentLevel);
        bw.write("<subroutineDec>\n");
        indentLevel++;
        // Write ('constructor' | 'function' | 'method')
        indentWriteAdvance();
        // Write ('void | type)
        indentWriteAdvance();
        // Write subroutineName
        indentWriteAdvance();
        // Write '('
        indentWriteAdvance();
        // Write parameters
        compileParamenterList();
        // Write ')'
        indentWriteAdvance();
        // Write subroutineBody
        compileSubroutineBody();
        indentLevel--;
        writeIndent(indentLevel);
        bw.write("</subroutineDec>\n");
    }

    public void compileSubroutineBody() throws IOException
    {
        writeIndent(indentLevel);
        bw.write("<subroutineBody>\n");
        indentLevel++;
        // Write '{'
        indentWriteAdvance();
        // Write 0 or more varDec
        while (currentToken.equals("var"))
        {  
            // Write varDec
            compileVarDec();
        }
        // Write statements
        compileStatements();
        // Write '}'
        indentWriteAdvance();
        indentLevel--;
        writeIndent(indentLevel);
        bw.write("</subroutineBody>\n");
    }

    public void compileParamenterList() throws IOException
    {
        writeIndent(indentLevel);
        bw.write("<parameterList>\n");
        indentLevel++;
        if (!currentToken.equals(")"))
        {
            // Write type 
            indentWriteAdvance();
            // Write varName
            indentWriteAdvance();
            // More params?
            while (currentToken.equals(","))
            {
                // Write (',')
                indentWriteAdvance();
                // Write type 
                indentWriteAdvance();
                // Write varName
                indentWriteAdvance();
            }
        }
        indentLevel--;
        writeIndent(indentLevel);
        bw.write("</parameterList>\n");
    }

    public void compileVarDec() throws IOException
    {
        writeIndent(indentLevel);
        bw.write("<varDec>\n");
        indentLevel++;
        // Write 'var'
        indentWriteAdvance();
        // Write type
        indentWriteAdvance();
        // Write varName
        indentWriteAdvance();
        // Write 0 or more (',' varName)
        while (!currentToken.equals(";"))
        {
            // Write (',')
            indentWriteAdvance();
            // Write varName
            indentWriteAdvance();
        }
        // Write ';'
        indentWriteAdvance();
        indentLevel--;
        writeIndent(indentLevel);
        bw.write("</varDec>\n");
    }

    public void compileStatements() throws IOException
    {
        writeIndent(indentLevel);
        bw.write("<statements>\n");
        indentLevel++;
        while (currentToken.equals("let") || currentToken.equals("while")
            || currentToken.equals("do") || currentToken.equals("if")
            || currentToken.equals("return"))
        {
            if (currentToken.equals("let"))
                compileLet();
            else if (currentToken.equals("while"))
                compileWhile();
            else if (currentToken.equals("do"))
                compileDo();
            else if (currentToken.equals("if"))
                compileIf();
            else
                compileReturn();
        }
        indentLevel--;
        writeIndent(indentLevel);
        bw.write("</statements>\n");
    }

    public void compileLet() throws IOException
    {
        writeIndent(indentLevel);
        bw.write("<letStatement>\n");
        indentLevel++;
        // Write let
        indentWriteAdvance();
        // Write varName
        indentWriteAdvance();
        // Write 0 or 1 expression
        if (currentToken.equals("["))
        {
            //Write '['
            indentWriteAdvance();
            // Write expression
            compileExpression();
            //Write ']'
            indentWriteAdvance();
        }
        // Write '='
        indentWriteAdvance();
        // Write expression
        compileExpression();
        // Write ';'
        indentWriteAdvance();
        indentLevel--;
        writeIndent(indentLevel);
        bw.write("</letStatement>\n");
    }

    public void compileIf() throws IOException
    {
        writeIndent(indentLevel);
        bw.write("<ifStatement>\n");
        indentLevel++;
        // Write 'if'
        indentWriteAdvance();
        // Write '('
        indentWriteAdvance();
        // Write expression
        compileExpression();
        // Write ')'
        indentWriteAdvance();
        // Write '{'
        indentWriteAdvance();
        // Write statements
        compileStatements();
        // Write '}'
        indentWriteAdvance();
        // Write 0 or 1 else
        if (currentToken.equals("else"))
        {
            // Write else
            indentWriteAdvance();
            // Write '{'
            indentWriteAdvance();
            // Write statements
            compileStatements();
            // Write '}'
            indentWriteAdvance();
        }
        indentLevel--;
        writeIndent(indentLevel);
        bw.write("</ifStatement>\n");
    }

    public void compileWhile() throws IOException
    {
        writeIndent(indentLevel);
        bw.write("<whileStatement>\n");
        indentLevel++;
        // Write while
        indentWriteAdvance();
        // Write '('
        indentWriteAdvance();
        // Write expression
        compileExpression();
        // Write ')'
        indentWriteAdvance();
        // Write '{'
        indentWriteAdvance();
        // Write statements
        compileStatements();
        // Write '}'
        indentWriteAdvance();
        indentLevel--;
        writeIndent(indentLevel);
        bw.write("</whileStatement>\n");
    }

    public void compileDo() throws IOException
    {
        writeIndent(indentLevel);
        bw.write("<doStatement>\n");
        indentLevel++;
        // Write do
        indentWriteAdvance();
        // Write subroutineCall
        // Write subroutineName | className | varName
        indentWriteAdvance();
        // Is expreesionList or '.'
        if (!currentToken.equals("."))
        {
            // Write '('
            indentWriteAdvance();
            // Write expressionList()
            compileExpressionList();
            // Write ')'
            indentWriteAdvance();
        }
        else
        {
            // Write '.'
            indentWriteAdvance();
            // Write subroutineName
            indentWriteAdvance();
            // Write '('
            indentWriteAdvance();
            // Write expressionList
            compileExpressionList();
            // Write ')'
            indentWriteAdvance();
        }
        // Write ';'
        indentWriteAdvance();
        indentLevel--;
        writeIndent(indentLevel);
        bw.write("</doStatement>\n");
    }

    public void compileReturn() throws IOException
    {
        writeIndent(indentLevel);
        bw.write("<returnStatement>\n");
        indentLevel++;
        // Write return
        indentWriteAdvance();
        // Write 0 or 1 expression
        if (!currentToken.equals(";"))
        {
            // Write expression
            compileExpression();
        }
        // Write ';'
        indentWriteAdvance();
        indentLevel--;
        writeIndent(indentLevel);
        bw.write("</returnStatement>\n");
    }
    
    public void compileExpression() throws IOException
    {
        writeIndent(indentLevel);
        bw.write("<expression>\n");
        indentLevel++;
        // Write term
        compileTerm();
        // Write 0 or more op terms
        while (Arrays.asList(OPLIST).contains(currentToken))
        {
            // Write op
            indentWriteAdvance();
            // Write term
            compileTerm();
        }
        indentLevel--;
        writeIndent(indentLevel);
        bw.write("</expression>\n");
    }

    public void compileTerm() throws IOException
    {   
        writeIndent(indentLevel);
        bw.write("<term>\n");
        indentLevel++;
        // Check if intConst, stringConst, or keyConst
        if (currentClassification.equals(INT_CONST) || currentClassification.equals(STRING_CONST)
            || currentClassification.equals(KEYWORD))
        {
            // Write const
            indentWriteAdvance();
        }
        else if (currentToken.equals("-") || currentToken.equals("~"))
        {
            // Write unaryOp
            indentWriteAdvance();
            // Write term
            compileTerm();
        }
        else if (currentToken.equals("("))
        {
            // Write '('
            indentWriteAdvance();
            // Write expression
            compileExpression();
            // Write ')'
            indentWriteAdvance();
        }
        else
        {
            // Write varName
            indentWriteAdvance();
            // Check if '[' or '.'' or '(''
            if (currentToken.equals("["))
            {
                // Write "["
                indentWriteAdvance();
                // Write expression
                compileExpression();
                // Write "]"
                indentWriteAdvance();
            }
            if (currentToken.equals("."))
            {
                // Write '.'
                indentWriteAdvance();
                // Write subroutineName
                indentWriteAdvance();
                // Write '('
                indentWriteAdvance();
                // Write expressionList
                compileExpressionList();
                // Write ')'
                indentWriteAdvance();
            }
        }
        indentLevel--;
        writeIndent(indentLevel);
        bw.write("</term>\n");
    }

    public void compileExpressionList() throws IOException
    {
        writeIndent(indentLevel);
        bw.write("<expressionList>\n");
        indentLevel++;
        // Write 0 or 1 expression(',' expression)*
        if (!currentToken.equals(")"))
        {
            // Write expression
            compileExpression();
            // Write 0 or more (',' expression)
            while (currentToken.equals(","))
            {
                // Write '.'
                indentWriteAdvance();
                // Write expression
                compileExpression();
            }
        }
        
        indentLevel--;
        writeIndent(indentLevel);
        bw.write("</expressionList>\n");
    }

    public void eat(String s) throws IOException
    {
        if (currentString != s)
            throw new IllegalArgumentException();
        else
            advance();
    }

    public static void main(String [] args) throws IOException
    {
        CompilationEngine cengine = new CompilationEngine(args[0]);
    }
}