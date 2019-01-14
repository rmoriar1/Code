// Main.cpp Created by Ryan Moriarty

#include <cstdio>
#include <cstdlib>
#include <fstream>
#include <queue>
#include <unistd.h>
#include "Pager.hpp"
#include "Process.hpp"

using namespace std;

bool O_option = 0, P_option = 0, F_option = 0, S_option = 0, x_option = 0, y_option = 0, f_option= 0, a_option = 0;
char current_operation, pager_algo;
int current_vpage = 0, num_processes = 0, rofs = 0;
long ctx_switches = 0, inst_count = 0, num_frames = 0, rand_size = 0;
long long cost = 0;
Process* current_process;
Pager* the_pager;
vector <int> rand_vals;
vector <Process*> process_list;
vector <frame_t*> frame_table;
queue <int> free_list;

typedef struct inst_t
{
    char operation;
    int pid_or_vpage;
    long number;
} inst_t;

queue <inst_t*> inst_list;

frame_t* allocate_frame_from_free_list()
{
    if (free_list.empty())
        return NULL;
    frame_t* frame = frame_table[free_list.front()];
    free_list.pop();
    return frame;
}

void update_pte(pte_t* pte, char operation)
{
    switch (operation)
    {
        case 'r':
            B_SET(pte->entry, REFERENCED);
            break;
        case 'w':
            B_SET(pte->entry, REFERENCED);
            if (B_IS_SET(pte->entry, WRITE_PROTECT))
            {
                current_process->stats->segprot++;
                printf("  SEGPROT\n");
            }
            else
                B_SET(pte->entry, MODIFIED);
            break;
    }
}

int get_next_instruction()
{
    if (inst_list.empty())
        return 0;
    inst_t* inst = inst_list.front();
    current_operation = inst->operation;
    current_vpage = inst->pid_or_vpage;
    inst_list.pop();
    if (O_option)
        printf("%lu: ==> %c %d\n", inst->number, inst->operation, inst->pid_or_vpage);
    if (current_operation == 'c')
    {
        ctx_switches++;
        current_process = process_list[current_vpage];
        return get_next_instruction();
    }
    return 1;
}

frame_t* get_frame()
{
    frame_t* frame = allocate_frame_from_free_list();
    if(frame == NULL)
        frame = the_pager->select_victim_frame();
    return frame;
}

void instruction_simulation()
{
    while (get_next_instruction())
    {
        pte_t* pte = &current_process->page_table[current_vpage]; // done by hardware in reality
        if (!B_IS_SET(pte->entry, PRESENT)) // is pte present bit set?
        {
            // check if pte has corresponding vaddr
            for (int i = 0; i < current_process->num_vmas; i++)
            {
                vma_t* v = current_process->vma_list[i];
                if (v->start_vpage <= current_vpage && v->end_vpage >= current_vpage)
                {
                    // set the bits in the pte
                    if (v->write_protected)
                        B_SET(pte->entry, WRITE_PROTECT);
                    if (v->file_mapped)
                        B_SET(pte->entry, FILEMAPPED);
                    // entry is part of vma
                    B_SET(pte->entry, ACCESSIBLE);
                }
            }
            if (!B_IS_SET(pte->entry, ACCESSIBLE))
            {
                current_process->stats->segv++;
                printf("  SEGV\n");
                continue;
            }
            frame_t* newframe = get_frame();
            newframe->pid = current_process->pid;
            newframe->vpage = current_vpage;
            // set page frame number
            pte->entry += newframe->frame_number;
            // if this is the vpage's first access and page is not file mapped, the frame is zeroed
            if (B_IS_SET(pte->entry, FILEMAPPED))
            {
                current_process->stats->fins++;
                if (O_option)
                    printf(" FIN\n");
            }
            else if (B_IS_SET(pte->entry, PAGEDOUT))
            {
                current_process->stats->ins++;
                if (O_option)
                    printf(" IN\n");
            }
            else
            {
                current_process->stats->zeros++;
                if (O_option)
                    printf(" ZERO\n");
            }
            current_process->stats->maps++;
            if (O_option)
                printf(" MAP %d\n", newframe->frame_number);
            B_SET(pte->entry, PRESENT);
        }
        // simulate instruction execution by hardware by updating the PTE bits
        update_pte(pte, current_operation);
        if (y_option || x_option)
        {
            for (int i = 0; i < num_processes; i++)
            {
                if (y_option || i == current_process->pid)
                {
                    printf("PT[%d]: ", i);
                    for (int j = 0; j < 64; j++)
                    {
                        pte_t* pte = &process_list[i]->page_table[j];
                        if (!B_IS_SET(pte->entry, PRESENT))
                            if (B_IS_SET(pte->entry, PAGEDOUT))
                                printf("# ");
                            else
                                printf("* ");
                            else
                            {
                                printf("%d:", j);
                                if (B_IS_SET(pte->entry, REFERENCED))
                                    printf("R");
                                else
                                    printf("-");
                                if (B_IS_SET(pte->entry, MODIFIED))
                                    printf("M");
                                else
                                    printf("-");
                                if (B_IS_SET(pte->entry, PAGEDOUT))
                                    printf("S ");
                                else
                                    printf("- ");
                            }
                    }
                    printf("\n");
                }
            }
        }
        if (f_option)
        {
            printf("FT: ");
            for (int i = 0; i < num_frames; i++)
            {
                if (frame_table[i]->pid == -1)
                    printf("* ");
                else
                    printf("%d:%d ", frame_table[i]->pid, frame_table[i]->vpage);
            }
            if (pager_algo == 'f')
                printf("ALGO: fifoidx = %ld", ((FIFO *)the_pager)->fifoidx % num_frames);
            if (pager_algo == 's')
            {
                printf("ALGO: fifoidx = %ld", ((Second_Chance *)the_pager)->fifoidx % num_frames);
            }
            if (pager_algo == 'c')
                printf("ALGO: hand = %ld", ((Clock *)the_pager)->index_of_oldest % num_frames);
            if (pager_algo == 'a')
            {
                printf("ALGO: ");
                for (int i = 0; i < num_frames; i++)
                {
                    if (frame_table[i]->pid != -1)
                    {
                        printf("%d:%x ", i, frame_table[i]->age);
                    }
                }
            }
            printf("\n");
        }
    }
}

int main(int argc, char** argv)
{
    int c;
    char* cptr;
    string options;
    ifstream infile;
    string input_line;
    while ((c = getopt (argc, argv, "a:o:f:")) != -1)
    {
        switch (c)
        {
            case 'a':
                pager_algo = *optarg;
                break;
            case 'o':
                cptr = optarg;
                while (*cptr)
                {
                    switch(*cptr)
                    {
                        case 'O':
                            O_option = 1;
                            break;
                        case 'P':
                            P_option = 1;
                            break;
                        case 'F':
                            F_option = 1;
                            break;
                        case 'S':
                            S_option = 1;
                            break;
                        case 'x':
                            x_option = 1;
                            break;
                        case 'y':
                            y_option = 1;
                            break;
                        case 'f':
                            f_option = 1;
                            break;
                        case 'a':
                            a_option = 1;
                            break;
                        default:
                            fprintf(stderr, "Unknown output option: <%c>\n",*cptr);
                            exit(1);
                    }
                    cptr++;
                }
                break;
            case 'f':
                num_frames = atoi(optarg);
                for (int i = 0; i < num_frames; i++)
                {
                    // initiallize free_list and frame_table
                    free_list.push(i);
                    frame_t* frame = new frame_t;
                    frame->frame_number = i;
                    frame->pid = -1;
                    frame->age = 0;
                    frame_table.push_back(frame);
                }
                break;
            default:
                printf("invalid option -- '%c'\n", c);
                exit(1);
        }
    }
    // initialize pager based on optarg of a
    switch (pager_algo)
    {
        case 'f':
            the_pager = new FIFO();
            ((FIFO*)the_pager)->fifoidx = 0;
            break;
        case 's':
            the_pager = new Second_Chance();
            // initialize queue
            for (int i = 0; i < num_frames; i++)
            {
                ((Second_Chance*)the_pager)->frame_queue.push(frame_table[i]);
            }
            break;
        case 'r':
             the_pager = new Random();
            break;
        case 'n':
            the_pager = new NRU();
            ((NRU*)the_pager)->clock_cycle = 0;
            break;
        case 'c':
            the_pager = new Clock();
            ((Clock*)the_pager)->index_of_oldest = 0;
            break;
        case 'a':
            the_pager = new Aging();
            break;
        default:
            printf("Unknown Replacement pager_algorithm <%c>\n", pager_algo);
            exit(1);
    }
    // randfile is last arg, input file is 2nd to last arg
    char* randfile = argv[argc-1];
    infile.open(randfile);
    if (!infile)
    {
        printf("Unable to open file: %s\n", randfile);
        exit(1);
    }
    infile >> rand_size;
    for (int i = 0; i < rand_size; i++)
    {
        int j;
        infile >> j;
        rand_vals.push_back(j);
    }
    char* inputfile = argv[argc-2];
    infile.close();
    infile.clear();
    infile.open(inputfile);
    if (!infile)
    {
        printf("Unable to open file: %s\n", inputfile);
        exit(1);
    }
    // skip comments
    while (infile.peek() == '#' || infile.peek() == '\n')
        getline(infile, input_line);
    // input line now holds number of processes
    infile >> num_processes;
    getline(infile, input_line);
    // get each process's info
    for (int i = 0; i < num_processes; i++)
    {
        Process* P = new Process();
        P->pid = i;
        while (infile.peek() == '#' || infile.peek() == '\n')
            getline(infile, input_line);
        // input line now holds number of vmas
        infile >> P->num_vmas;
        // get each vmas info
        for (int j = 0; j < P->num_vmas; j++)
        {
            vma_t* v = new vma_t;
            while (infile.peek() == '#' || infile.peek() == '\n')
                getline(infile, input_line);
            infile >> v->start_vpage >> v->end_vpage >> v->write_protected >> v->file_mapped;
            P->vma_list.push_back(v);
        }
        P->stats = new pstats_t;
        P->stats->unmaps = 0;
        P->stats->maps = 0;
        P->stats->ins = 0;
        P->stats->outs = 0;
        P->stats->fins = 0;
        P->stats->fouts = 0;
        P->stats->zeros = 0;
        P->stats->segv = 0;
        P->stats->segprot = 0;
        process_list.push_back(P);
    }
    // get instructions
    while(!infile.eof())
    {
        inst_t* inst = new inst_t;
        while (infile.peek() == '#' || infile.peek() == '\n')
            getline(infile, input_line);
        infile >> inst->operation >> inst->pid_or_vpage;
        if (inst->operation != 'c' && inst->operation != 'r' && inst->operation != 'w')
            break;
        inst->number = inst_count;
        inst_list.push(inst);
        inst_count++;
    }
    // run instruction_simulation
    instruction_simulation();
    if (P_option)
    {
        for (int i = 0; i < num_processes; i++)
        {
            printf("PT[%d]: ", i);
            for (int j = 0; j < 64; j++)
            {
                pte_t* pte = &process_list[i]->page_table[j];
                if (!B_IS_SET(pte->entry, PRESENT))
                    if (B_IS_SET(pte->entry, PAGEDOUT))
                        printf("# ");
                    else
                        printf("* ");
                else
                {
                    printf("%d:", j);
                    if (B_IS_SET(pte->entry, REFERENCED))
                        printf("R");
                    else
                        printf("-");
                    if (B_IS_SET(pte->entry, MODIFIED))
                        printf("M");
                    else
                        printf("-");
                    if (B_IS_SET(pte->entry, PAGEDOUT))
                        printf("S ");
                    else
                        printf("- ");
                }
            }
            printf("\n");
        }
    }
    if (F_option)
    {
        printf("FT: ");
        for (int i = 0; i < num_frames; i++)
        {
            if (frame_table[i]->pid == -1)
                printf("* ");
            else
                printf("%d:%d ", frame_table[i]->pid, frame_table[i]->vpage);
        }
        printf("\n");
    }
    if (S_option)
    {
        for (int i = 0; i < num_processes; i++)
        {
            pstats_t* pstats = process_list[i]->stats;
            printf("PROC[%d]: U=%lu M=%lu I=%lu O=%lu FI=%lu FO=%lu Z=%lu SV=%lu SP=%lu\n",
                   i, pstats->unmaps, pstats->maps, pstats->ins, pstats->outs, pstats->fins, pstats->fouts, pstats->zeros, pstats->segv, pstats->segprot);
            // accumulate cost for each process
            cost += 400 * (pstats->unmaps + pstats->maps);
            cost += 3000 * (pstats->ins + pstats->outs);
            cost += 2500 * (pstats->fins + pstats->fouts);
            cost += 150 * pstats->zeros;
            cost += 240 * pstats->segv;
            cost += 300 * pstats->segprot;
        }
        cost += (inst_count-ctx_switches) + (121 * ctx_switches);
        printf("TOTALCOST %lu %lu %llu\n", ctx_switches, inst_count, cost);
    }
}
