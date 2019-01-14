// Process.cpp Created by Ryan Moriarty

#include <cstdio>
#include <cstdlib>
#include <queue>
#include "Process.hpp"

using namespace std;

extern bool O_option;

void Process::unmap(int vpage)
{
    stats->unmaps++;
    if (O_option)
        printf(" UNMAP %d:%d\n", pid, vpage);
    if (B_IS_SET(page_table[vpage].entry, MODIFIED))
    {
        if (B_IS_SET(page_table[vpage].entry, FILEMAPPED))
        {
            stats->fouts++;
            if (O_option)
                printf(" FOUT\n");
        }
        else
        {
            stats->outs++;
            B_SET(page_table[vpage].entry, PAGEDOUT);
            if (O_option)
                printf(" OUT\n");
        }
    }
    // maintain accessible, filemapped, pagededout and write_protect bits
    page_table[vpage].entry &= (8192 + 4096 + 2048 + 256);
}

