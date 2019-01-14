// Pager.cpp Created by Ryan Moriarty

#include <cstdio>
#include <cstdlib>
#include <queue>
#include "Process.hpp"
#include "Pager.hpp"

using namespace std;

extern bool a_option;
extern int rofs;
extern long num_frames, rand_size;
extern Process* current_process;
extern vector <int> rand_vals;
extern vector <Process*> process_list;
extern vector <frame_t*> frame_table;

int Pager::my_random(int modulo) {return 0;};

frame_t* FIFO::select_victim_frame()
{
    frame_t* victim = frame_table[fifoidx++ % num_frames];
    process_list[victim->pid]->unmap(victim->vpage);
    return victim;
}
frame_t* Second_Chance::select_victim_frame()
{
    frame_t * victim;
    while ((victim = frame_queue.front()))
    {
        frame_queue.pop();
        // if referenced bit is set, unset it and continue searching frame_table
        if (B_IS_SET(process_list[victim->pid]->page_table[victim->vpage].entry, REFERENCED))
        {
            B_UNSET(process_list[victim->pid]->page_table[victim->vpage].entry, REFERENCED);
            frame_queue.push(victim);
        }
        else
        {
            frame_queue.push(victim);
            break;
        }
    }
    process_list[victim->pid]->unmap(victim->vpage);
    fifoidx = victim->frame_number + 1;
    return victim;
}

int Random::my_random(int rand_size)
{
    return rand_vals[rofs++ % rand_size] % num_frames;
}

frame_t* Random::select_victim_frame()
{
    frame_t* victim = frame_table[my_random((int)rand_size)];
    process_list[victim->pid]->unmap(victim->vpage);
    return victim;
}

int NRU::my_random(int pages_in_array)
{
    return rand_vals[rofs++ % rand_size] % pages_in_array;
}

frame_t* NRU::select_victim_frame()
{
    clock_cycle++;
    vector<int> classes[4];
    // Separate frames into classes based on R and M bits
    for (int i = 0; i < num_frames; i++)
    {
        pte_t* pte = &process_list[frame_table[i]->pid]->page_table[frame_table[i]->vpage];
        if (B_IS_SET(pte->entry, REFERENCED))
        {
            if (B_IS_SET(pte->entry, MODIFIED))
                classes[3].push_back(i);
            else
                classes[2].push_back(i);
            // on every 10th clock cycle, reset the reference bits
            if (clock_cycle % 10 == 0)
                B_UNSET(pte->entry, REFERENCED);
        }
        else
        {
            if (B_IS_SET(pte->entry, MODIFIED))
                classes[1].push_back(i);
            else
                classes[0].push_back(i);
        }
    }
    int selidx = 0;
    int lowest_class = 0;
    int frame_num = 0;
    // Select a frame at random from the lowest class
    for (int i = 0; i < 4; i++)
    {
        if (classes[i].size() == 0)
            continue;
        selidx = my_random((int) classes[i].size());
        frame_num = classes[i][selidx];
        lowest_class = i;
        break;
    }
    frame_t* victim = frame_table[frame_num];
    if (a_option)
    {
        printf("ASTATUS: ");
        if (clock_cycle % 10 == 0)
            printf("reset NRU refbits while walking PTE\n");
        printf(" | selected --> lowest_class=%d: selidx=%d from ", lowest_class, selidx);
        for (int i = 0; i < classes[lowest_class].size(); i++)
        {
            printf("%d ", classes[lowest_class][i]);
        }
        printf("\n");
    }
    process_list[victim->pid]->unmap(victim->vpage);
    return victim;
}

frame_t* Clock::select_victim_frame()
{
    frame_t* victim;
    while ((victim = frame_table[index_of_oldest++ % num_frames]))
    {
        // if referenced bit is set, unset it and continue searching frame_table
        if (B_IS_SET(process_list[victim->pid]->page_table[victim->vpage].entry, REFERENCED))
            B_UNSET(process_list[victim->pid]->page_table[victim->vpage].entry, REFERENCED);
        else
            break;
    }
    process_list[victim->pid]->unmap(victim->vpage);
    return victim;
}

frame_t* Aging::select_victim_frame()
{
    for (int i = 0; i < num_frames; i++)
    {
        // bit shift left
        frame_table[i]->age >>= 1;
        // set bit leftmost bit in each entry of the age vector if reference bit is set, then clear it
        if (B_IS_SET(process_list[frame_table[i]->pid]->page_table[frame_table[i]->vpage].entry, REFERENCED))
        {
            B_SET(frame_table[i]->age, 31);
            B_UNSET(process_list[frame_table[i]->pid]->page_table[frame_table[i]->vpage].entry, REFERENCED);
        }
    }
    int min_frame = 0;
    // find min_frame, if ages are equal choose lowest frame #
    for (int i = 1; i < num_frames; i++)
    {
        if (frame_table[i]->age < frame_table[min_frame]->age)
            min_frame = i;
    }
    frame_t* victim = frame_table[min_frame];
    if (a_option)
    {
        printf("ASTATUS:  ");
        for (int i = 0; i < num_frames; i++)
        {
            printf("%d:%x ", i, frame_table[i]->age);
        }
        printf("| selected --> min_frame = %d age=%x\n", min_frame, frame_table[min_frame]->age);
    }
    // reset age of victim
    victim->age = 0;
    process_list[victim->pid]->unmap(victim->vpage);
    return victim;
}
