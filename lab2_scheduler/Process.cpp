// Process.cpp by Ryan Moriarty

#include <cstdio>
#include <cstdlib>
#ifndef _PROCESS_CPP
#define _PROCESS_CPP

using namespace std;

enum state {CREATED, RUNNING, READY, BLOCKED, DONE};

class Process
{
public:
    int id;
    int at;
    int tc;
    int tcRem;
    int cb;
    int cbRem;
    int io;
    int staticPrio;
    int dynamicPrio;
    int ft;
    int tt;
    int it;
    int cw;
    int stateTimeStamp;
    int timeInPrevState;
    state curState;
};

#endif
