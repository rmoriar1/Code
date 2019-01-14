//  Pager.hpp Created by Ryan Moriarty

using namespace std;

#ifndef Pager_h
#define Pager_h

typedef struct frame_t
{
    int frame_number;
    int pid;
    int vpage;
    unsigned int age:32;
} frame_t;

class Pager
{
public:
    virtual
    int my_random(int modulo);
    virtual
    frame_t* select_victim_frame() = 0;
};

class FIFO: public Pager
{
public:
    int fifoidx;
    frame_t* select_victim_frame();
};

class Second_Chance: public Pager
{
public:
    int fifoidx;
    queue<frame_t*> frame_queue;
    frame_t* select_victim_frame();
};

class Random: public Pager
{
public:
    int my_random(int rand_size);
    frame_t* select_victim_frame();
};

class NRU: public Pager
{
public:
    int clock_cycle;
    int my_random(int pages_in_array);
    frame_t* select_victim_frame();
};

class Clock: public Pager
{
public:
    int index_of_oldest;
    frame_t* select_victim_frame();
};

class Aging: public Pager
{
public:
    frame_t* select_victim_frame();
};

#endif /* Pager_h */
