import java.util.*;
import java.io.*;
import java.lang.*;


public class JackTokenizer 
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
    private static final String [] KEYWORDS = {"class", "method", "function", "constructor",
    "int", "boolean", "char", "void", "var", "static", "field", "let", "do", "if", "else",
    "while", "return", "true", "false", "null", "this"};
    private static final Character [] SYMBOLS = {'{', '}', '(', ')', '[', ']', '.', ',', ';', 
    '+', '-', '*', '/', '&', '|', '<', '>', '=', '~'};
    private static BufferedReader br;
    private static String currentToken;
    private static StringBuilder currentString;
    private static boolean reachedEnd = false;
    private static String fn;
    private static String ta;

    public JackTokenizer(String filename) throws IOException
    {
        try
        {
            br = new BufferedReader(new FileReader(filename));
        }
        catch(FileNotFoundException ex)
        {
            System.out.println("Unable to open file: " + filename);
        }
        currentString = new StringBuilder(br.readLine());
        filename = filename.substring(0, filename.length() - 5);
        fn = filename + "T" + FILETYPE;
        BufferedWriter bw = new BufferedWriter(new FileWriter(fn));
        bw.write("<tokens>\n");
        reachedEnd = false;
        while (hasMoreTokens()) 
        {
            advance();
            if (!reachedEnd)
            {
                String tokenClassification = tokenType();
                String output = "";
                if (tokenClassification == INT_CONST)
                {
                    bw.write("<" + tokenClassification + "> " + 
                    intVal() + " </" + tokenClassification + ">\n");
                }
                else
                {
                    if (tokenClassification == STRING_CONST)
                        output = stringVal();
                    if (tokenClassification == KEYWORD)
                        output = keyWord();
                    if (tokenClassification == IDENTIFIER)
                        output = identifier();
                    if (tokenClassification == SYMBOL)
                        output = symbol();
                    if (!output.equals(""))
                    {
                        bw.write("<" + tokenClassification + "> " + output + 
                        " </" + tokenClassification + ">\n");
                    }
                }
            }
        }
        bw.write("</tokens>\n");
        bw.close();
    }
    public String getFilename()
    {
        return fn;
    }
    // Are there more tokens in the input
    public boolean hasMoreTokens() throws IOException
    {
        return (br.ready()) ;
    }
    /* Gets the next token from the input and makes it the current token.
    This method should be called only if hasMoreTokens is true.
    Initially there is not current token. */
    public void advance() throws IOException
    {
        while (currentString.length() == 0 && hasMoreTokens())
        {
            currentString = new StringBuilder(br.readLine());
            if(currentString.toString().replaceAll("\\s", "").length() == 0)
            {
                currentString.delete(0, currentString.length());
            }

        }
        if(currentString.length() == 0)
        {
            reachedEnd = true;
            return;
        }
        char firstChar = currentString.charAt(0);
        if (firstChar == ' ' )
        {
            for (int i = 0; i < currentString.length(); i++)
            {
                if (currentString.charAt(i) != ' ')
                {
                    firstChar = currentString.charAt(i);
                    currentString = currentString.delete(0, i);
                    break;
                }
                else if (i == currentString.length() - 1 && hasMoreTokens())
                {
                    i = 0;
                    currentString = new StringBuilder(br.readLine());
                }
            }
        }

        if (firstChar == '/' && currentString.charAt(1) == '*')
        {
            for (int i = 2; i < currentString.length(); i++)
            {
                if (currentString.charAt(i) == '/' && 
                    currentString.charAt(i-1) == '*')
                {
                    currentString.delete(0, i + 1);
                    break;
                }
                else if (i == currentString.length() - 1)
                {
                    currentString = new StringBuilder(br.readLine());
                    i = 1;
                }
            }
        }

        for (int i = 0; i < currentString.length() - 1; i++)
        {
            if (currentString.charAt(i) == '/' && currentString.charAt(i+1) == '/')
            {
                currentString.delete(i, currentString.length());
            }
        }

        if (currentString.length() == 0)
        {
            advance();
            return;
        }

        // If char is digit build digit string until no digit
        if (Character.isDigit(firstChar))
        {
            for (int i = 1; i < currentString.length(); i++)
            {
                if (!Character.isDigit(currentString.charAt(i)))
                {
                    currentToken = currentString.substring(0, i);
                    currentString = currentString.delete(0, i);
                    return;
                }
            }
        }
        // Check if is symbol
        if (Arrays.asList(SYMBOLS).contains(firstChar))
        {
            currentToken = "" + currentString.charAt(0);
            currentString.deleteCharAt(0);
            return;
        }
        // Check if string
        if (firstChar == '"')
        {
            for (int i = 1; i < currentString.length(); i++)
            {
                if (currentString.charAt(i) == '"')
                {
                    currentToken = currentString.substring(0, i+1);
                    currentString = currentString.delete(0, i + 1);
                    return;
                }
            }
        }
        // Check if keyword/ identifier
        for (int i = 0; i < currentString.length(); i++)
        {
            if (currentString.charAt(i) == ' ' ||
                Arrays.asList(SYMBOLS).contains(currentString.charAt(i)))
            {
                currentToken = currentString.substring(0, i);
                currentString = currentString.delete(0, i);
                return;
            } 
            if (Arrays.asList(KEYWORDS).contains(currentString.substring(0,i)))
            {
                currentToken = currentString.substring(0, i);
                currentString = currentString.delete(0, i);
                return;
            }
        }
        currentString = new StringBuilder(br.readLine());
    }
    // Returns the type of the curren token, as a constant.
    public String tokenType() throws IOException
    {
        if (Arrays.asList(SYMBOLS).contains(currentToken.charAt(0)))
            return SYMBOL;
        if (Arrays.asList(KEYWORDS).contains(currentToken.replaceAll("\\s", "")))
            return KEYWORD;
        if (currentToken.charAt(0) == '"')
            return STRING_CONST;
        for (int i = 0; i < currentToken.length(); i++)
        {
            if (!Character.isDigit(currentToken.charAt(i)))
                return IDENTIFIER;
        }
        return INT_CONST;
    }
    // Returns the keyword which is the current token, as a constant.
    public static String keyWord() throws IOException
    {
        return currentToken.replaceAll("\\s", "");
    }
    // Returns the character which is the current token.
    public static String symbol() throws IOException
    {
        char c = currentToken.charAt(0);
        if (c == '<')
            return "&lt;";
        if (c == '>')
            return "&gt;";
        if (c == '"')
            return "&quot;";
        if (c == '&')
            return "&amp;";
        return Character.toString(c);
    }
    public static String identifier() throws IOException
    {
        return currentToken.replaceAll("\\s", "");
    }
    public static int intVal() throws IOException
    {
        return Integer.parseInt(currentToken);
    }
    public static String stringVal() throws IOException
    {
        return currentToken.substring(1, currentToken.length() - 1);
    }

    public static void main(String [] args) throws IOException
    {
        JackTokenizer tknzer = new JackTokenizer(args[0]);
    }
}