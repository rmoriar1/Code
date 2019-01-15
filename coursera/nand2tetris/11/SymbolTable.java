import java.util.*;
import java.io.*;
import java.lang.*;

public class SymbolTable {

    private static final String FIELD = "field";
    private static final String STATIC = "static";
    private static final String ARG = "argument";
    private static final String VAR = "var";

    private Hashtable<String, Node> classST; 
    private Hashtable<String, Node> subroutineST; 
    private int fieldIndex = 0;
    private int staticIndex = 0;
    private int argumentIndex = 0;
    private int localIndex = 0;

    class Node
    {
        public String type;
        public String kind;
        public int index;

        public Node(String type, String kind, int index)
        {
            this.type = type;
            this.kind = kind;
            this.index = index;
        }
    }

    public SymbolTable()
    {
        classST = new Hashtable<String, Node>();
        subroutineST = new Hashtable<String, Node>();
    }

    public void startSubroutine()
    {
        argumentIndex = 0;
        localIndex = 0;
        subroutineST.clear();
    }

    public boolean contains(String name)
    {
        if (classST.containsKey(name) || subroutineST.containsKey(name))
        {
            return true;
        }
        return false;
    }

    public void define(String name, String type, String kind)
    {
        Node newest = new Node(type, kind, varCount(kind));
        if (kind.equals(FIELD) || kind.equals(STATIC))
        {
            classST.put(name, newest);     
        }
        else
            subroutineST.put(name, newest);
        if (kind.equals(FIELD))
            fieldIndex++;
        if (kind.equals(STATIC))
            staticIndex++;
        if (kind.equals(ARG))
            argumentIndex++;
        if (kind.equals(VAR))   
            localIndex++;
    }

    public int varCount(String kind)
    {
        if (kind.equals(FIELD))
            return fieldIndex;
        if (kind.equals(STATIC))
            return staticIndex;
        if (kind.equals(ARG))
            return argumentIndex;
        return localIndex;
    }

    public String typeOf(String name)
    {
        if (subroutineST.get(name) != null)
            return subroutineST.get(name).type;
        return classST.get(name).type;
    }

    public String kindOf(String name)
    {
        String kind = "";
        if (subroutineST.get(name) != null)
            kind = subroutineST.get(name).kind;
        else
            kind = classST.get(name).kind;
        if (kind.equals(FIELD))
            return "this";
        if (kind.equals(VAR))
            return "local";
        else 
            return kind;
    }

    public int indexOf(String name)
    {
        if (subroutineST.get(name) != null)
            return subroutineST.get(name).index;
        return classST.get(name).index;
    }
}