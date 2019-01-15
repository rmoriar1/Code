import java.util.*;
import java.io.*;
import java.lang.*;

public class CompilationEngine
{
    private static final String FILETYPE = ".vm";
    private static final String KEYWORD = "keyword";
    private static final String SYMBOL = "symbol";
    private static final String IDENTIFIER = "identifier";
    private static final String INT_CONST = "integerConstant";
    private static final String STRING_CONST = "stringConstant";
    private static final String CONST = "constant";
    private static final String CLASS = "class";
    private static final String METHOD = "method";
    private static final String FUNCTION = "function";
    private static final String CONSTRUCTOR = "constructor";
    private static final String INT = "int";
    private static final String BOOLEAN = "boolean";
    private static final String CHAR = "char";
    private static final String VOID = "void";
    private static final String ARG = "argument";
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
    private static final String [] KEYWORDS = {"class", "method", "function", "constructor",
    "int", "boolean", "char", "void", "var", "static", "field", "let", "do", "if", "else",
    "while", "return", "true", "false", "null", "this"};
    private static final String [] OPLIST = {"+", "-", "*", "/", "|",  
    "=", "~", "&lt;", "&gt;", "&quot;", "&amp;"};
    private static BufferedReader br;
    private static String currentString;
    private static String currentClassification;
    private static String currentToken;
    private static String nextToken;
    private static String className;
    private static int indentLevel = 0;
    private static SymbolTable st;
    private static VMWriter vm;
    private static String subroutineType;
    private static String subroutineKind;
    private static String subroutineName;
    private static String kind ="";
    private static String type = "";
    private static boolean handleFirstClass = true;
    private static boolean dontAddNextIdentifierToTheSymbolTable = true;
    private static int argCount = 0;
    private static int whileCount = 0;
    private static int ifCount = 0;
    private static boolean betweenTerms = false;

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
        st = new SymbolTable();
        vm = new VMWriter(filename);
        advance();
        compileClass();
        vm.close();
    }

    public void handleIndentifier() throws IOException
    {
        // Either class, subroutine, var, arg, static, field
        // Is class?
        if (currentToken.equals(className) || handleFirstClass)
        {
            currentString = "<class> " + currentToken + " </class>";
            handleFirstClass = false;
            currentClassification = isolateClassification();
            return;
        }
        if (st.contains(currentToken))
        {
            currentString = "<" + st.kindOf(currentToken) + " " + st.indexOf(currentToken) + " used> " + 
            currentToken + " </" + st.kindOf(currentToken) + " " + st.indexOf(currentToken) + " used>";
            currentClassification = isolateClassification();
            return;
        }
        // Is subroutine
        if (kind.equals(CONSTRUCTOR) || kind.equals(FUNCTION) || kind.equals(METHOD) || 
            type.equals(VOID) || type.equals(currentToken) || currentToken.equals("Array")
            || dontAddNextIdentifierToTheSymbolTable)
        {
            currentString = "<subroutine> " + currentToken + " </subroutine>";
            currentClassification = isolateClassification();
            return;
        }
        // Must be defined, add to ST
        st.define(currentToken, type, kind);
        currentString = "<" + st.kindOf(currentToken) + " " + st.indexOf(currentToken) + " def> " + 
        currentToken + " </" + st.kindOf(currentToken) + " " + st.indexOf(currentToken) + " def>";
        currentClassification = isolateClassification();
        return;
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
        if (s.equals(IDENTIFIER))
            handleIndentifier();
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
                if (currentString.substring(1,j).equals(STRING_CONST))
                    s = currentString.substring(j+2, i-1);
                else
                    s = currentString.substring(j+1, i).replaceAll("\\s", "");
                break;
            }
        }
        return s;
    }

    public void advance() throws IOException
    {
        if (br.ready())
        {
            currentString = br.readLine();
            currentToken = isolateToken();
            currentClassification = isolateClassification();
        }
    }

    public void compileClass() throws IOException
    {
        whileCount = 0;
        ifCount = 0;
        // Skip <tokens>
        advance();
        // Write 'class'
        advance();
        // Write className and save
        className = currentToken;
        advance();
        // Write '{'
        advance();
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
        advance();
        // Skip </tokens>
        advance();
    }

    public void compileClassVarDec() throws IOException
    {
        // Save kind
        kind = currentToken;
        dontAddNextIdentifierToTheSymbolTable = true;
        // Write ('static' | 'field')
        advance();
        dontAddNextIdentifierToTheSymbolTable = false;
        // Save type
        type = currentToken;
        // Write type
        advance();
        // Write varName
        advance();
        // Write 0 or more (',' varName)
        while (currentToken.equals(","))
        {
            // Write (',')
            advance();
            // Write varName
            advance();
        }
        // Write (';')
        advance();
    }

    public void compileSubroutineDec() throws IOException
    {
        ifCount = 0;
        whileCount = 0;
        st.startSubroutine();
        kind = currentToken;
        subroutineKind = currentToken;
        if (currentToken.equals(METHOD))
            st.define("this", className, "argument");
        // Write ('constructor' | 'function' | 'method')
        advance();
        // Write ('void | type)
        type = currentToken;
        subroutineType = currentToken;
        advance();
        // Save subroutine name until we know home many args
        subroutineName = currentToken;
        advance();
        // Skip '('
        advance();
        // Write parameters
        compileParamenterList();
        // Skip ')'
        advance();
        // Write subroutineBody
        compileSubroutineBody();
    }

    public void compileSubroutineBody() throws IOException
    {
        // Write '{'
        advance();
        // Write 0 or more varDec
        while (currentToken.equals("var"))
        {  
            // Write varDec
            compileVarDec();
        }
        // Write call
        vm.writeFunction(className + "." + subroutineName, st.varCount(VAR));
        if (subroutineKind.equals(METHOD))
        {
            vm.writePush(ARG, 0);
            vm.writePop("pointer", 0);
        }
        // Write statements
        if (subroutineName.equals("new"))
        {
            vm.writePush("constant", st.varCount(FIELD));
            vm.writeCall("Memory.alloc", 1);
            vm.writePop("pointer", 0);
        }
        compileStatements();
        // Skip '}'
        advance();
    }

    public void compileParamenterList() throws IOException
    {
        if (!currentToken.equals(")"))
        {
            kind = "argument";
            // Save type
            type = currentToken;
            // Write type 
            advance();
            // Write varName
            advance();
            // More params?
            while (currentToken.equals(","))
            {
                // Write (',')
                advance();
                // Save type
                type = currentToken;
                // Write type 
                advance();
                // Write varName
                advance();
            }
        }
    }

    public void compileVarDec() throws IOException
    {
        // Write 'var'
        kind = currentToken;
        dontAddNextIdentifierToTheSymbolTable = true;
        advance();
        // Write type
        type = currentToken;
        dontAddNextIdentifierToTheSymbolTable = false;
        advance();
        // Write varName
        advance();
        // Write 0 or more (',' varName)
        while (!currentToken.equals(";"))
        {
            // Write (',')
            advance();
            // Write varName
            advance();
        }
        kind = "function";
        // Write ';'
        advance();
    }

    public void compileStatements() throws IOException
    {
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
    }

    public void compileLet() throws IOException
    {
        // Write let
        advance();
        // Write varName
        String varName = currentToken;
        boolean arrAccess = false;
        advance();
        // Write 0 or 1 expression
        if (currentToken.equals("["))
        {
            arrAccess = true;
            //Write '['
            advance();
            // Write expression
            compileExpression();
            //Write ']'
            vm.writePush(st.kindOf(varName), st.indexOf(varName));
            vm.writeArithmetic("+");
            advance();
        }
        // Write '='
        advance();
        // Write expression
        compileExpression();
        if (st.kindOf(varName).equals(ARG) && subroutineType.equals(METHOD))
            vm.writePop(st.kindOf(varName), st.indexOf(varName) - 1);
        else if (arrAccess)
        {
            vm.writePop("temp", 0);
            vm.writePop("pointer", 1);
            vm.writePush("temp", 0);
            vm.writePop("that", 0);
        }
        else
            vm.writePop(st.kindOf(varName), st.indexOf(varName));
        // Write ';'
        advance();
    }

    public void compileIf() throws IOException
    {
        int labelNum = ifCount;
        ifCount++;
        // Write 'if'
        advance();
        // Write '('
        advance();
        // Write expression
        compileExpression();
        // Write ')'
        advance();
        String ifLabel = "IF_TRUE" + labelNum;
        String elseLabel = "IF_FALSE" + labelNum;
        String endLabel = "IF_END" + labelNum;
        vm.writeIf(ifLabel);
        vm.writeGoto(elseLabel);
        vm.writeLabel(ifLabel);
        // Write '{'

        advance();
        // Write statements
        compileStatements();
        // Write '}'
        advance();
        // Write 0 or 1 else
        if (currentToken.equals("else"))
        {
            vm.writeGoto(endLabel);
            vm.writeLabel(elseLabel);
            // Write else
            advance();
            // Write '{'
            advance();
            // Write statements
            compileStatements();
            // Write '}'
            advance();
            vm.writeLabel(endLabel);
        }
        else
        {
            vm.writeLabel(elseLabel);
        }
    }

    public void compileWhile() throws IOException
    {
        int labelNum = whileCount;
        String whileLabel = "WHILE_EXP" + labelNum;
        vm.writeLabel(whileLabel);
        whileCount++;
        // Write while
        advance();
        // Write '('
        advance();
        // Write expression
        compileExpression();
        // Write ')'
        vm.writeArithmetic("not");
        String endLabel = "WHILE_END" + labelNum;
        vm.writeIf(endLabel);
        advance();
        // Write '{'
        advance();
        // Write statements
        compileStatements();
        vm.writeGoto(whileLabel);
        // Write '}'
        advance();
        vm.writeLabel(endLabel);
    }

    public void compileDo() throws IOException
    {
        kind = METHOD;
        argCount = 0;
        // Write do
        advance();
        // Write subroutineCall
        // Write subroutineName | className | varName
        String doName = currentToken;
        String subroutineName = "";
        if (st.contains(doName))
        {
            if (st.kindOf(currentToken).equals(ARG))
                vm.writePush(st.kindOf(currentToken), st.indexOf(currentToken) - 1);
            else
                vm.writePush(st.kindOf(currentToken), st.indexOf(currentToken));
        }
        advance();
        // Is expreesionList or '.'
        if (!currentToken.equals("."))
        {
            // Write '('
            advance();
            vm.writePush("pointer", 0);
            // Write expressionList()
            compileExpressionList();
            // Write ')'
            advance();
        }
        else
        {
            // Write '.'
            advance();
            subroutineName = currentToken;
            // Write subroutineName
            advance();
            // Write '('
            advance();
            // Write expressionList
            compileExpressionList();
            // Write ')'
            advance();
        }
        // ARBITRARY ARG COUNT??
        if (st.contains(doName))
        {
            vm.writeCall(st.typeOf(doName) + "." + subroutineName, argCount + 1);
        }
        else 
        {
            if (subroutineName.equals(""))
            {
                vm.writeCall(className + "." + doName, argCount + 1);
            }
            else
                vm.writeCall(doName + "." + subroutineName, argCount);
        }
        vm.writePop("temp", 0);
        // Write ';'
        advance();
    }

    public void compileReturn() throws IOException
    {
        // Write return
        advance();
        // Write 0 or 1 expression
        if (!currentToken.equals(";"))
        {
            // Write expression
            compileExpression();
        }
        if (subroutineType.equals(VOID))
            vm.writePush(CONST, 0);
        vm.writeReturn();
        // Write ';'
        advance();
    }
    
    public void compileExpression() throws IOException
    {
        // Write term
        compileTerm();
        // Write 0 or more op terms
        while (Arrays.asList(OPLIST).contains(currentToken))
        {
            // Save op
            betweenTerms = true;
            String op = currentToken;
            advance();
            // Write term
            compileTerm();
            if (op.equals("-"))
                vm.writeArithmetic("sub");
            else
                vm.writeArithmetic(op);
        }
        betweenTerms = false;
    }

    public void compileTerm() throws IOException
    {   
        // Check if intConst, stringConst, or keyConst
        if (currentClassification.equals(INT_CONST) || currentClassification.equals(STRING_CONST)
            || currentClassification.equals(KEYWORD))
        {
            if (currentClassification.equals(INT_CONST))
            {
                // Write const
                vm.writePush(CONST, Integer.parseInt(currentToken));
            }
            if (currentClassification.equals(KEYWORD))
            {
                if (currentToken.equals(TRUE))
                {
                    vm.writeArithmetic(TRUE);
                }
                if (currentToken.equals(FALSE))
                {
                    vm.writeArithmetic(FALSE);
                }
                if (currentToken.equals(NULL))
                {
                    vm.writePush(CONST, 0);
                }
                if (currentToken.equals(THIS))
                    vm.writePush("pointer", 0);
                if (currentToken.equals("that"))
                    vm.writePush("pointer", 1);
            }
            if (currentClassification.equals(STRING_CONST))
            {
                vm.writePush(CONST, currentToken.length());
                vm.writeCall("String.new", 1);
                for (int i = 0; i < currentToken.length(); i++)
                {
                    vm.writePush(CONST, (int) currentToken.charAt(i));
                    vm.writeCall("String.appendChar", 2);
                }
            }
            advance();
        }
        else if (currentToken.equals("-") || currentToken.equals("~"))
        {
            String op = currentToken;
            // Write unaryOp
            advance();
            // Write term
            compileTerm();
            vm.writeArithmetic(op);
        }
        else if (currentToken.equals("("))
        {
            // Skip '('
            advance();
            // Write expression
            compileExpression();
            // Write ')'
            advance();
        }
        else
        {
            String varName = currentToken;
            advance();
            if (currentToken.equals("["))
            {
                // Skip "["
                advance();
                // Write expression
                compileExpression();
                vm.writePush(st.kindOf(varName), st.indexOf(varName));
                vm.writeArithmetic("+");
                vm.writePop("pointer", 1);
                vm.writePush("that", 0);
                // Skip "]"
                advance();
            }
            // Save varName
            else if (st.contains(varName))
            {
                if (st.kindOf(varName).equals(ARG))
                    vm.writePush(st.kindOf(varName), st.indexOf(varName));
                else
                    vm.writePush(st.kindOf(varName), st.indexOf(varName));
            }
            if (currentToken.equals("."))
            {
                kind = FUNCTION;
                // Write '.'
                advance();
                // Write subroutineName
                String subroutineName = currentToken;
                advance();
                argCount = 0;
                // Write '('
                advance();
                // Write expressionList
                compileExpressionList();
                if (st.contains(varName))
                {
                    vm.writeCall(st.typeOf(varName) + "." + subroutineName, 1);
                }
                else
                    vm.writeCall(varName + "." + subroutineName, argCount);
                // Write ')'
                advance();
            }
            if (currentToken.equals("("))
            {
                //Skip "("
                advance();
                // Write expression
                compileExpression();
                // Skip "]"
                advance();
                // Write call varName
                vm.writeCall(currentToken, 0);
            }
        }
    }

    public void compileExpressionList() throws IOException
    {
        // Write 0 or 1 expression(',' expression)*
        if (!currentToken.equals(")"))
        {
            argCount++;
            // Write expression
            compileExpression();
            // Write 0 or more (',' expression)
            while (currentToken.equals(","))
            {
                argCount++;
                // Write '.'
                advance();
                // Write expression
                compileExpression();
            }
        }        
    }

    public static void main(String [] args) throws IOException
    {
        CompilationEngine cengine = new CompilationEngine(args[0]);
    }
}