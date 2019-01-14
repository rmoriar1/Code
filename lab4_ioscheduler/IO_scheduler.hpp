//  IO_scheduler.hpp Created by Ryan Moriarty

using namespace std;

#ifndef IO_scheduler_h
#define IO_scheduler_h

extern int current_track;

typedef struct io_request_t
{
    int id;
    int arrival_time;
    int track;
    int turnaround;
    int waittime;
    int start_time;
    int end_time;
} io_request_t;

class IO_scheduler
{
public:
    virtual
    void add_io(io_request_t* req) = 0;
    virtual
    io_request_t* issue_io() = 0;
};

class FIFO: public IO_scheduler
{
public:
    queue <io_request_t *> io_queue;
    void add_io(io_request_t* req);
    io_request_t* issue_io();
};

class SSTF: public IO_scheduler
{
public:
    vector <io_request_t *> io_queue;
    void add_io(io_request_t* req);
    io_request_t* issue_io();
};

class LOOK: public IO_scheduler
{
public:
    bool up;
    vector <io_request_t *> io_queue;
    void add_io(io_request_t* req);
    io_request_t* issue_io();
};

class CLOOK: public IO_scheduler
{
public:
    vector <io_request_t *> io_queue;
    void add_io(io_request_t* req);
    io_request_t* issue_io();
};

class FLOOK: public IO_scheduler
{
public:
    bool up;
    int active_queue;
    vector <io_request_t *> io_queues[2];
    void add_io(io_request_t* req);
    io_request_t* issue_io();
};

#endif
