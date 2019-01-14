// Main.cpp Created by Ryan Moriarty

#include <cstdio>
#include <cstdlib>
#include <fstream>
#include <queue>
#include <unistd.h>
#include "IO_scheduler.hpp"

using namespace std;

bool f_option = 0, q_option = 0, v_option = 0, io_active = 0;
char sched_algo;
int total_time = 0, tot_movement = 0, max_waittime = 0, current_track = 0, current_time = 1, next_io = 0, io_request_count = 0;
double tot_turnaround = 0, tot_waittime = 0;
IO_scheduler* scheduler;
io_request_t* current_io;
vector <io_request_t*> io_request_list;

void io_simulation()
{
    if (v_option)
        printf("TRACE\n");
    while (1)
    {
        /* add io req when arrival time == current time, since io requests are
           in increasing order list[next] will always contain next to be added */
        if (next_io < io_request_count && io_request_list[next_io]->arrival_time == current_time)
        {
            if (v_option)
                printf("%d:\t%d add %d\n", current_time, next_io, io_request_list[next_io]->track );
            // add io to queue
            scheduler->add_io(io_request_list[next_io]);
            io_request_list[next_io]->waittime = current_time;
            next_io++;
        }
        // current_io track is reached finish io, set active to false
        if (io_active && current_io->track == current_track)
        {
            io_request_list[current_io->id]->end_time = current_time;
            if (v_option)
                printf("%d:\t%d finish %d\n", current_time, current_io->id, current_time - current_io->arrival_time);
            io_active = 0;
        }
        if (!io_active)
        {
            current_io = scheduler->issue_io();
            if (current_io != NULL)
            {
                current_io->start_time = current_time;
                current_io->waittime = current_time - current_io->waittime;
                io_active = 1;
                if (sched_algo == 'f' && f_option)
                    printf("%d:\t%d get Q=%d\n", current_time, current_io->id, ((FLOOK*)scheduler)->active_queue);
                if (v_option)
                    printf("%d:\t%d issue %d %d\n", current_time, current_io->id, current_io->track, current_track);
            }
            else
            {
                // if no io is active, nothing is in queue and all ios have been added end sim
                if (next_io == io_request_count)
                    break;
                current_time++;
            }
        }
        else
        {
            // advance track toward, current_io's track
            if (io_request_list[current_io->id]->track < current_track)
                current_track--;
            else
                current_track++;
            tot_movement++;
            current_time++;
        }
    }
}

int main(int argc, char** argv)
{
    int options;
    ifstream infile;
    string input_line;
    while ((options = getopt (argc, argv, "fqvs:")) != -1)
    {
        switch (options)
        {
            case 'f':
                f_option = 1;
                break;
            case 'q':
                q_option = 1;
                break;
            case 'v':
                v_option = 1;
                break;
            case 's':
                sched_algo = *optarg;
                break;
            default:
                printf("invalid option -- '%c'\n", options);
                exit(1);
        }
    }
    // initialize scheduler based on optarg of s
    switch (sched_algo)
    {
        case 'i':
            scheduler = new FIFO();
            break;
        case 'j':
            scheduler = new SSTF();
            break;
        case 's':
            scheduler = new LOOK();
            ((LOOK*)scheduler)->up = true;
            break;
        case 'c':
            scheduler = new CLOOK();
            break;
        case 'f':
            scheduler = new FLOOK();
            ((FLOOK*)scheduler)->up = true;
            ((FLOOK*)scheduler)->active_queue = 1;
            break;
        default:
            printf("Unknown Replacement pager_algorithm <%c>\n", sched_algo);
            exit(1);
    }
    char* inputfile = argv[argc - 1];
    infile.open(inputfile);
    if (!infile)
    {
        printf("Unable to open file: %s\n", inputfile);
        exit(1);
    }
    // get io operations
    while(!infile.eof())
    {
        while (infile.peek() == '#' || infile.peek() == '\n')
            getline(infile, input_line);
        io_request_t* req = new io_request_t;
        infile >> req->arrival_time >> req->track;
        getline(infile, input_line);
        if (infile.peek() != '?')
        {
            req->id = io_request_count;
            io_request_list.push_back(req);
            io_request_count++;
        }
    }
    io_simulation();
    // print out io stats and summary stats
    for (int i = 0; i < io_request_count; i++)
    {
        io_request_t* req = io_request_list[i];
        printf("%5d: %5d %5d %5d\n", i, req->arrival_time, req->start_time, req->end_time);
        tot_turnaround += req->end_time - req->start_time + req->waittime;
        if (req->waittime > max_waittime)
            max_waittime = req->waittime;
        tot_waittime += req->waittime;
    }
    printf("SUM: %d %d %.2lf %.2lf %d\n",
           current_time, tot_movement, tot_turnaround / io_request_count, tot_waittime / io_request_count, max_waittime);
}
