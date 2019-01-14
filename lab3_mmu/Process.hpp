//  Process.hpp Created by Ryan Moriarty

using namespace std;

#ifndef Process_h
#define Process_h

/* test if n-th bit in x is set */
#define B_IS_SET(x, n)   (((x) & (1<<(n)))?1:0)

/* set n-th bit in x */
#define B_SET(x, n)      ((x) |= (1<<(n)))

/* unset n-th bit in x */
#define B_UNSET(x, n)    ((x) &= ~(1<<(n)))

// bits corresponding to pte entry
#define PRESENT 7
#define WRITE_PROTECT 8
#define MODIFIED 9
#define REFERENCED 10
#define PAGEDOUT 11
#define FILEMAPPED 12
#define ACCESSIBLE 13

typedef struct vma_t
{
    unsigned int pte:32;
    int start_vpage;
    int end_vpage;
    int write_protected;
    int file_mapped;
} vma_t;

typedef struct pte_t // can only be total of 32-bit size !!!!
{
    unsigned int entry:32;
} pte_t;

typedef struct pstats_t
{
    long unmaps;
    long maps;
    long ins;
    long outs;
    long fins;
    long fouts;
    long zeros;
    long segv;
    long segprot;
} pstats_t;

class Process
{
public:
    int pid;
    int num_vmas;
    pstats_t* stats;
    vector <vma_t*> vma_list;
    pte_t page_table[64];
    void unmap(int vpage);
};

#endif /* Process_h */
