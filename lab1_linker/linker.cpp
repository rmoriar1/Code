#include <iostream>
#include <fstream>
#include <cstdio>
#include <cstring>
#include <cstdlib>

using namespace std;

int linenum = 1, lineoffset = 1, module = 1, prevoffset = 1;
int instCount = 0, base = 0, symbolCount = 0;
char useList[16][16];
bool used[16];
bool onPass1;
bool addrNext;
char buffer[1024];
FILE * fptr;

struct symbols
{
    char symbol[16];
    bool multDef;
    bool used;
    int val;
    int rel;
    int module;
};

struct symbols symbolTable[256];

bool isNumber(char * str)
{
    for (int i = 0; i < strlen(str); i++)
    {
        if (!isdigit(str[i]))
            return false;
    }
    return true;
}

bool isSymbol(char * str)
{
    if (!isalpha(str[0]))
        return false;
    for (int i = 1; i < strlen(str); i++)
    {
        if (!isalnum(str[i]))
            return false;
    }
    return true;
}

bool isAddress(char * str)
{
    if (strcmp(str, "A") == 0 || strcmp(str, "E") == 0
        || strcmp(str, "I") == 0 || strcmp(str, "R") == 0)
        return true;
    return false;
}

void createSymbol(char* sym, int val, int base)
{
    struct symbols newSym;
    strncpy(newSym.symbol, sym, 16);
    newSym.val = val + base;
    newSym.rel = val;
    newSym.multDef = false;
    newSym.used = false;
    newSym.module = module;
    symbolTable[symbolCount] = newSym;  
    symbolCount++;
}

bool symbolDefd(char * sym)
{
    for (int i = 0; i < symbolCount; i++)
    {
        if (strcmp(symbolTable[i].symbol, sym) == 0)
        {
            symbolTable[i].multDef = true;
            return true;
        }
    }
    return false;
}

void symbolUsed(char * sym)
{
    for (int i = 0; i < symbolCount; i++)
    {
        if (strcmp(symbolTable[i].symbol, sym) == 0)
        {
            symbolTable[i].used = true;
        }
    }
}

void parseError(int errcode) 
{
    static const char* errstr[] = 
    {
        "NUM_EXPECTED", 
        "SYM_EXPECTED",
        "ADDR_EXPECTED",
        "SYM_TOO_LONG",
        "TOO_MANY_DEF_IN_MODULE",
        "TOO_MANY_USE_IN_MODULE",
        "TOO_MANY_INSTR"
    }; 
    printf("Parse Error line %d offset %d: %s\n", linenum, lineoffset - 1, errstr[errcode]);
    exit(1);
}

int readInt()
{
    char * token;
    if (lineoffset == 1)
        token = strtok(buffer, " \t\n");
    else 
        token = strtok(NULL, " \t\n");
    while (token == NULL)
    {
        if (fgets(buffer, 1024, fptr) == NULL)
            return -1;
        linenum++;
        lineoffset = 1;
        token = strtok(buffer, " \t\n");
    }
    lineoffset = token + 1 - buffer;
    lineoffset++;
    prevoffset = lineoffset + strlen(token);
    if (!isNumber(token))
        parseError(0);
    return atoi(token);
}

char * readSym()
{
    char * token;
    if (lineoffset == 1)
        token = strtok(buffer, " \t\n");
    else 
        token = strtok(NULL, " \t\n");
    while (token == NULL)
    {
        if (fgets(buffer, 1024, fptr) == NULL)
        {
            lineoffset = prevoffset;
            if (addrNext)
                parseError(2);
            parseError(1);
        }
        linenum++;
        lineoffset = 1;
        lineoffset++;
        prevoffset = lineoffset;
        token = strtok(buffer, " \t\n");
    }
    lineoffset = token + 1 - buffer;
    lineoffset++;
    prevoffset = lineoffset + strlen(token);
    return token;
}

void checkForUnused()
{
    for (int i = 0; i < symbolCount; i++)
    {
        if (!symbolTable[i].used)
            printf("Warning: Module %d: %s was defined but never used\n", symbolTable[i].module, 
                symbolTable[i].symbol);
    }
}

void printSymbols()
{
    printf("Symbol Table\n");
    for (int i = 0; i < symbolCount; i++)
    {
        printf("%s=%d", symbolTable[i].symbol, symbolTable[i].val);
        if (symbolTable[i].multDef)
            printf(" Error: This variable is multiple times defined; first value used\n");
        else
            printf("\n");
    }
}

void pass1()
{
    onPass1 = true;
    char * sym;
    int val;
    fgets(buffer, 1024, fptr);
    while (!feof(fptr))
    {
        addrNext = false;
        int defcount = readInt();
        if (defcount > 16)
            parseError(4);
        if (feof(fptr))
        {
            defcount = -1;
        }
        for (int i = 0; i < defcount; i++)
        {
            sym = strdup(readSym());
            if (!isSymbol(sym))
                parseError(1);
            if (strlen(sym) > 16)
                parseError(3);
            val = readInt();
            if (!symbolDefd(sym))
                createSymbol(sym, val, base);
        }
        int usecount = readInt();
        if (usecount > 16)
            parseError(5);
        for (int i = 0; i < usecount; i++)
        {
            sym = strdup(readSym());
            if (strlen(sym) > 16)
                parseError(3);
            if (!isSymbol(sym))
                parseError(1);
        }
        int codecount = readInt();
        for (int i = 0; i < symbolCount; i++)
        {
            if (symbolTable[i].module == module && symbolTable[i].rel >= codecount)
            {
                printf("Warning: Module %d: %s too big %d (max=%d) assume zero relative\n", 
                    module, symbolTable[i].symbol, symbolTable[i].val - base, codecount - 1);
                symbolTable[i].val -= symbolTable[i].rel;
                symbolTable[i].rel = 0;
            }
        }
        instCount += codecount;
        if (instCount > 512)
            parseError(6);
        base += codecount;
        addrNext = true;
        for (int i = 0; i < codecount; i++)
        {
            sym = strdup(readSym());
            if (!isAddress(sym))
                parseError(2);
            val = readInt();
        }
        module++;
    }
    printSymbols();
}

void pass2()
{
    onPass1 = false;
    char addrSym[1];
    char * sym;
    int val;
    fgets(buffer, 1024, fptr);
    base = 0;
    lineoffset = 1;
    module = 1;
    while (!feof(fptr))
    {
        int defcount = readInt();
        for (int i = 0; i < defcount; i++)
        {
            sym = readSym();
            val = readInt();
        }
        int usecount = readInt();
        for (int i = 0; i < usecount; i++)
        {
            sym = readSym();
            strcpy(useList[i], sym);
            used[i] = false;
        }
        int codecount = readInt();
        for (int i = 0; i < codecount; i++)
        {
            printf("%03d: ", base+i);
            sym = readSym();
            strcpy(addrSym, sym);
            val = readInt();
            int opcode = val / 1000;
            int operand = val % 1000;
            if (val >= 10000)
            {
                if (strcmp(addrSym, "I") == 0)
                {
                    printf("%d ", 9999);
                    printf("Error: Illegal immediate value; treated as 9999\n");
                }
                else
                {
                    printf("%d ", 9999);
                    printf("Error: Illegal opcode; treated as 9999\n");
                }
            }
            else if (strcmp(addrSym, "R") == 0)
                if (operand >= codecount)
                {
                    printf("%04d ", opcode * 1000 + base);
                    printf("Error: Relative address exceeds module size; zero used\n");
                }
                else
                    printf("%04d\n", val + base);
            else if (strcmp(addrSym, "E") == 0)
            {
                if (operand >= usecount)
                {
                    printf("%04d ", val);
                    printf("Error: External address exceeds length of uselist; treated as immediate\n");
                }
                else
                {
                    for (int i = 0; i < symbolCount; i++)
                    {
                        if (strcmp(symbolTable[i].symbol, useList[operand]) == 0)
                        {
                            symbolUsed(symbolTable[i].symbol);
                            used[operand] = true;
                            operand = symbolTable[i].val;
                            printf("%04d\n", opcode * 1000 + operand);
                            break;
                        }
                        if (i == symbolCount - 1)
                        {
                            printf("%04d ", opcode * 1000);
                            printf("Error: %s is not defined; zero used\n", useList[operand]);
                            used[operand] = true;
                        }
                    }
                }
            }
            else if (strcmp(addrSym, "A") == 0 && operand > 512)
            {
                    val = 0;
                    printf("%04d ", opcode * 1000);
                    printf("Error: Absolute address exceeds machine size; zero used\n");
            }
            else
                printf("%04d\n", val);
        }
        for (int i = 0; i < usecount; i++)
            {
                if (!used[i])
                    printf("Warning: Module %d: %s appeared in the uselist but was not actually used\n",
                        module, useList[i]);
            }
        base += codecount;
        module++;
    }
    printf("\n");
    checkForUnused();
}

int main(int argc, char * argv[])
{
    char * filename = argv[1];
    fptr = fopen(filename, "r");
    if (fptr == NULL)
    {
        printf("Cannot open input file %s\n", filename);
        exit(1);
    }
    pass1();
    rewind(fptr);
    printf("\nMemory Map\n");
    pass2();
}
