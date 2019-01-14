// IO_scheduler.cpp Created by Ryan Moriarty

#include <cstdio>
#include <cstdlib>
#include <queue>
#include "IO_scheduler.hpp"

using namespace std;

extern bool q_option;

void FIFO::add_io(io_request_t* req)
{
    io_queue.push(req);
}

io_request_t* FIFO::issue_io()
{
    if (io_queue.empty())
        return NULL;
    io_request_t* req = io_queue.front();
    io_queue.pop();
    return req;
}

void SSTF::add_io(io_request_t* req)
{
    io_queue.push_back(req);
}

io_request_t* SSTF::issue_io()
{
    if (io_queue.empty())
        return NULL;
    if (q_option)
    {
        printf("\t");
        for (int i = 0; i < io_queue.size(); i++)
        {
            printf("%d:%d ", io_queue[i]->id, abs(current_track - io_queue[i]->track));
        }
        printf("\n");
    }
    // find which index is closest to current track
    int min_index = 0;
    io_request_t* min_seek = io_queue[0];
    for (int i = 1; i < io_queue.size(); i++)
    {
        // find closest track to current, break ties by lowest io_id
        if (abs(current_track - io_queue[i]->track) < abs(current_track - min_seek->track))
        {
            min_seek = io_queue[i];
            min_index = i;
        }
    }
    io_queue.erase(io_queue.begin() + min_index);
    return min_seek;
}

void LOOK::add_io(io_request_t* req)
{
    io_queue.push_back(req);
}

io_request_t* LOOK::issue_io()
{
    if (io_queue.empty())
        return NULL;
    if (q_option)
        printf("\tGet: (");
    int next_index = 0;
    io_request_t* next = NULL;
    for (int i = 0; i < io_queue.size(); i++)
    {
        // if direction is up find min track thats > cur track
        if (up)
        {
            if (io_queue[i]->track >= current_track)
            {
                if (q_option)
                    printf("%d:%d ", io_queue[i]->id, abs(current_track - io_queue[i]->track));
                if (next == NULL || io_queue[i]->track < next->track)
                {
                    next_index = i;
                    next = io_queue[i];
                }
            }
        }
        // if direction is down find max track thats < cur track
        else
        {
            if (io_queue[i]->track <= current_track)
            {
                if (q_option)
                    printf("%d:%d ", io_queue[i]->id, abs(current_track - io_queue[i]->track));
                if (next == NULL || io_queue[i]->track > next->track)
                {
                    next_index = i;
                    next = io_queue[i];
                }
            }
        }
    }
    if (next == NULL)
    {
        // if none found switch directions and search again
        up = !up;
        if (q_option)
            printf(") --> change direction to %d\n", 2 * up - 1);
        return issue_io();
    }
    io_queue.erase(io_queue.begin() + next_index);
    if (q_option)
        printf(") --> %d dir=%d\n", next->id, 2 * up - 1);
    return next;
}

void CLOOK::add_io(io_request_t* req)
{
    io_queue.push_back(req);
}

io_request_t* CLOOK::issue_io()
{
    if (io_queue.empty())
        return NULL;
    if (q_option)
        printf("\tGet: (");
    int next_index = 0;
    io_request_t* next = NULL;
    for (int i = 0; i < io_queue.size(); i++)
    {
        // find min track thats > cur track
        if (io_queue[i]->track >= current_track)
        {
            if (q_option)
                printf("%d:%d ", io_queue[i]->id, abs(current_track - io_queue[i]->track));
            if (next == NULL || io_queue[i]->track < next->track)
            {
                next_index = i;
                next = io_queue[i];
            }
        }
    }
    // if none found, "wrap around" and choose io with lowest track #
    if (next == NULL)
    {
        next = io_queue[0];
        for (int i = 1; i < io_queue.size(); i++)
        {
            if (io_queue[i]->track < next->track)
            {
                next_index = i;
                next = io_queue[i];
            }
        }
        if (q_option)
            printf(") --> go to bottom and pick %d\n", next->id);
    }
    else
        if (q_option)
            printf(") --> %d\n", next->id);
    io_queue.erase(io_queue.begin() + next_index);
    return next;
}

void FLOOK::add_io(io_request_t* req)
{
    // add to non-active queue
    io_queues[1 - active_queue].push_back(req);
    if (q_option)
    {
        printf("   Q=%d ( ", 1 - active_queue);
        for (int i = 0; i < io_queues[1 - active_queue].size(); i++)
        {
            printf("%d:%d ", io_queues[1 - active_queue][i]->id, io_queues[1 - active_queue][i]->track);
        }
        printf(")\n");
    }
}

io_request_t* FLOOK::issue_io()
{
    static bool changed_dir_last = false;
    if (io_queues[active_queue].empty())
    {
        // if active queue is empty, switch queues
        active_queue = 1 - active_queue;
        // if both empty return null
        if (io_queues[active_queue].empty())
            return NULL;
    }
    if (q_option && !changed_dir_last)
    {
        printf("AQ=%d dir=%d curtrack=%d:  Q[0] = ( ", active_queue, 2 * up - 1, current_track);
        for (int i = 0; i < io_queues[0].size(); i++)
        {
            printf("%d:%d:%d ", io_queues[0][i]->id, io_queues[0][i]->track, io_queues[0][i]->track - current_track);
        }
        printf(")  Q[1] = ( ");
        for (int i = 0; i < io_queues[1].size(); i++)
        {
            printf("%d:%d:%d ", io_queues[1][i]->id, io_queues[1][i]->track, io_queues[1][i]->track - current_track);
        }
        printf(")\n");
    }
    if (q_option)
        printf("\tGet: (");
    int next_index = 0;
    io_request_t* next = NULL;
    // perform same logic as look
    for (int i = 0; i < io_queues[active_queue].size(); i++)
    {
        if (up)
        {
            if (io_queues[active_queue][i]->track >= current_track)
            {
                if (q_option)
                    printf("%d:%d:%d ", io_queues[active_queue][i]->id, io_queues[active_queue][i]->track, abs(current_track - io_queues[active_queue][i]->track));
                if (next == NULL || io_queues[active_queue][i]->track < next->track)
                {
                    next_index = i;
                    next = io_queues[active_queue][i];
                }
            }
        }
        else
        {
            if (io_queues[active_queue][i]->track <= current_track)
            {
                if (q_option)
                    printf("%d:%d:%d ", io_queues[active_queue][i]->id, io_queues[active_queue][i]->track, abs(current_track - io_queues[active_queue][i]->track));
                if (next == NULL || io_queues[active_queue][i]->track > next->track)
                {
                    next_index = i;
                    next = io_queues[active_queue][i];
                }
            }
        }
    }
    if (next == NULL)
    {
        up = !up;
        if (q_option)
            printf(") --> change direction to %d\n", 2 * up - 1);
        changed_dir_last = true;
        return issue_io();
    }
    changed_dir_last = false;
    io_queues[active_queue].erase(io_queues[active_queue].begin() + next_index);
    if (q_option)
        printf(") --> %d dir=%d\n", next->id, 2 * up - 1);
    return next;
}
