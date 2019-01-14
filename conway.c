# import <stdio.h>
# import <stdlib.h>
# import <string.h>

char * remFileExtension(char * filename);

int main(int argc, char * argv[])
{
    int numGens, curGen, c, dimension;
    char * filename = argv[2];
    numGens = atoi(argv[1]);
    dimension = 0;
    FILE * fin;
    FILE * fout;
    fin = fopen(filename, "r");
    if (fin == NULL)
    {
        printf("Can't open input file\n");
        exit(1);
    }
    char * fileNoExt = remFileExtension(filename);
    fout = fopen(strcat(fileNoExt, "out"), "w");
    // Find dimension of matrix
    while ((c = fgetc(fin)) != '\n')
    {
        if (c == '1' || c == '0')
            dimension++;
    }
    rewind(fin);
    char current[dimension][dimension];
    for (int i = 0; i < dimension; i++)
    {
        for (int j = 0; j < dimension; j++)
        {
            if(!fscanf(fin, "%c ", &current[i][j]))
                break;
        }
    }
    curGen = 1;
    char next[dimension][dimension];
    while (curGen <= numGens)
    {
        // Iterate over all cells in current matrix
        for (int i = 0; i < dimension; i++)
        {
            for (int j = 0; j < dimension; j++)
            {
                int neighbors = 0;
                // Iterate over neighbors
                for (int k = i - 1; k <= i + 1; k++)
                {
                    for (int l = j - 1; l <= j + 1; l++)
                    {
                        if (k >= 0 && k < dimension && l >= 0
                            && l < dimension && (k != i || l != j))
                        {
                            if (current[k][l] == '1')
                            {
                                neighbors++;
                            }
                        }
                    }
                }
                if (current[i][j] == '1')
                {
                    if (neighbors == 2 || neighbors == 3)
                    {
                        next[i][j] = '1';
                    }
                    else
                    {
                        next[i][j] = '0';
                    }
                }
                else
                {
                    if (neighbors == 3)
                        next[i][j] = '1';
                    else
                        next[i][j] = '0';
                }
            }
        }
        // Copy next array values into current
        for (int i = 0; i < dimension; i++)
        {
            for (int j = 0; j < dimension; j++)
            {
                current[i][j] = next[i][j];
            }
        }
        curGen++;
    }
    // Write final gen into file and terminal
    printf("\n");
    for (int i = 0; i < dimension; i++)
    {
        for (int j = 0; j < dimension; j++)
        {
            fprintf(fout, "%c ", current[i][j]);
            printf("%c ", current[i][j]);
        }
        fprintf(fout, "\n");
        printf("\n");
    }
    printf("\n");
    fclose(fin);
    fclose(fout);
}

char * remFileExtension(char * filename)
{
    size_t len = strlen(filename);
    char * fileNoExt = malloc(len - 2);
    memcpy(fileNoExt, filename, len - 3);
    fileNoExt[len - 3] = 0;
    return fileNoExt;
}
