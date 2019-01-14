// Event.cpp by Ryan Moriarty

#include <cstdio>
#include <cstdlib>
#include "Process.cpp"
#ifndef _EVENT_CPP
#define _EVENT_CPP

using namespace std;

enum trans { TRANS_TO_READY, TRANS_TO_RUN, TRANS_TO_BLOCK, TRANS_TO_DONE, TRANS_TO_PREEMPT};

class Event
{
public:
    int num;
    int evtTimeStamp;
    Process * evtProcess;
    trans transition;
};

struct compareEvents
{
    bool operator()(Event * const e1, Event * const e2)
    {
        if (e1->evtTimeStamp == e2->evtTimeStamp)
        {
            return e1->num > e2->num;
        }
        return e1->evtTimeStamp > e2->evtTimeStamp;
    }
};

#endif
