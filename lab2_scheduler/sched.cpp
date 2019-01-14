// sched.cpp by Ryan Moriarty

#include <cstdio>
#include <cstdlib>
#include <queue>
#include <unistd.h>
#include "Event.cpp"
#include "Scheduler.cpp"
#include <iostream>
#include <iomanip>
#include <fstream>

using namespace std;

int randSize, ofs = 0, verbose = 0, currentTime = 0;
string stateTypes[] = {"CREATED", "RUNNG", "READY", "BLOCK"};
string transTypes[] = {"READY", "RUNNG", "BLOCK", "Done", "READY"};
vector <int> randVals;
Scheduler * schedulerAlgo;
priority_queue<Event *, vector<Event *>, compareEvents > eventQueue;
// container for summary information
struct summaryInfo
{
    int ft;
    double numProc;
    double cpuTotal;
    double ioTotal;
    double ttTotal;
    double cwTotal;
};
summaryInfo summary;

int myRandom(int burst)
{
    return 1 + (randVals[ofs++ % randSize] % burst);
}

Event * get_event()
{
    if (eventQueue.empty())
        return 0;
    return eventQueue.top();
}

int get_next_event_time()
{
    if (eventQueue.empty())
        return -1;
    return eventQueue.top()->evtTimeStamp;
}

void put_event(Process * P, trans transTo, int curTime)
{
    static int eventNum = 0;
    Event * E = new Event();
    E->num = eventNum++;
    E->evtProcess = P;
    E->transition = transTo;
    E->evtTimeStamp = curTime;
    eventQueue.push(E);
}

int runCpuBurst(Process * P)
{
    int cpuBurst;
    trans nextTrans = TRANS_TO_BLOCK;
    // if quantum was reached don't generate new cpu burst
    if (P->cbRem)
    {
        cpuBurst = P->cbRem;
        P->cbRem = 0;
    }
    else
        cpuBurst = myRandom(P->cb);
    if (P->tcRem <= cpuBurst)
    {
        //total cpu time reached, record finish info and trans to done
        cpuBurst = P->tcRem;
        P->timeInPrevState = cpuBurst;
        P->ft = cpuBurst + currentTime;
        P->tt = P->ft - P->at;
        nextTrans = TRANS_TO_DONE;
    }
    if (schedulerAlgo->quantum < cpuBurst)
    {
        //quantum reached, record cbRem and trans to preempt
        P->cbRem = cpuBurst - schedulerAlgo->quantum;
        cpuBurst = schedulerAlgo->quantum;
        nextTrans = TRANS_TO_PREEMPT;
    }
    if (verbose)
        printf(" cb=%d rem=%d prio=%d", cpuBurst + P->cbRem, P->tcRem, P->dynamicPrio);
    P->tcRem -= cpuBurst;
    summary.cpuTotal += cpuBurst;
    currentTime += cpuBurst;
    return nextTrans;
}

void runIoBurst(Process * P)
{
    static int ioStart = 0, ioEnd = 0;
    int ioBurst = myRandom(P->io);
    P->it += ioBurst;
    if (verbose)
        printf("  ib=%d rem=%d", ioBurst, P->tcRem);
    put_event(P, TRANS_TO_READY, currentTime + ioBurst);
    // No IO was running in this period, add ioBurst to summary ioTotal
    if (currentTime > ioEnd)
    {
        ioStart = currentTime;
        ioEnd = currentTime + ioBurst;
        summary.ioTotal += ioBurst;
    }
    // IO extends beyond currently running io, add diff to summary
    if (currentTime + ioBurst > ioEnd)
    {
        summary.ioTotal += currentTime + ioBurst - ioEnd;
        ioEnd = currentTime + ioBurst;
    }
}

void Simulation() {
    Event * evt;
    bool callScheduler = false;
    Process * currentRunningProc = NULL;
    while( (evt = get_event()) )
    {
        eventQueue.pop();
        Process * P = evt->evtProcess; // this is the process the event works on
        currentTime = evt->evtTimeStamp;
        evt->evtProcess->timeInPrevState = currentTime - P->stateTimeStamp;
        if (verbose)
        {
            if (evt->transition == TRANS_TO_DONE)
                printf("%d %d %d: Done",  currentTime, P->id, P->timeInPrevState);
            else
            {
                printf("%d %d %d: %s -> %s", currentTime, P->id, P->timeInPrevState,
                   stateTypes[P->curState].c_str(), transTypes[evt->transition].c_str());
            }
        }
        P->stateTimeStamp = currentTime;
        switch(evt->transition) // which state to transition to?
        {
            case TRANS_TO_READY:
                // must come from BLOCKED or from PREEMPTION
                // must add to run queue
                P->curState = READY;
                schedulerAlgo->add_process(P);
                // conditional on whether something is run
                callScheduler = true;
                break;
            case TRANS_TO_RUN:
                // create event for either preemption or blocking
                P->cw += P->timeInPrevState;
                P->curState = RUNNING;
                currentRunningProc = P;
                // which event to enqueue?
                switch(runCpuBurst(P))
                {
                    case TRANS_TO_BLOCK:
                        P->cbRem = 0;
                        P->dynamicPrio = P->staticPrio - 1;
                        put_event(P, TRANS_TO_BLOCK, currentTime);
                        break;
                    case TRANS_TO_PREEMPT:
                        put_event(P, TRANS_TO_PREEMPT, currentTime);
                        break;
                    case TRANS_TO_DONE:
                        put_event(P, TRANS_TO_DONE, currentTime);
                        break;
                }
                break;
            // if block, prement or done, currentRunningProc = NULL
            case TRANS_TO_BLOCK:
                //create an event for when process becomes READY again
                P->curState = BLOCKED;
                runIoBurst(P);
                currentRunningProc = NULL;
                callScheduler = true;
                break;
            case TRANS_TO_PREEMPT:
                // add to runqueue (no event is generated)
                P->curState = READY;
                if (verbose)
                    printf("  cb=%d rem=%d prio=%d", P->cbRem, P->tcRem,
                           P->dynamicPrio);
                P->dynamicPrio--;
                schedulerAlgo->add_process(P);
                currentRunningProc = NULL;
                callScheduler = true;
                break;
            case TRANS_TO_DONE:
                P->curState = DONE;
                currentRunningProc = NULL;
                callScheduler = true;
                break;
        }
        if (verbose) printf("\n");
        //remove current event object from Memory
        delete evt;
        evt = NULL;
        if(callScheduler)
        {
            if (get_next_event_time() == currentTime)
            {
                continue;//process next event from Event queue
            }
            callScheduler = false;
            if (currentRunningProc == NULL)
            {
                currentRunningProc = schedulerAlgo->get_next_process();
                if (currentRunningProc == NULL)
                {
                    continue;
                }
                // create event to make process runnable for same time.
                put_event(currentRunningProc, TRANS_TO_RUN, currentTime);
            }
        }
    }
    summary.ft = currentTime;
}

int main(int argc, char ** argv)
{
    int procCount = 0, c = 0, quantum = 10000;
    char * schedType = NULL;
    vector <Process *> processList;
    vector <Process *> :: iterator iter;
    ifstream inFile;
    while ((c = getopt (argc, argv, "vs:")) != -1)
    {
        switch (c)
        {
            case 'v':
                verbose = 1;
                break;
            case 's':
                schedType = optarg;
                break;
        }
    }
    // initiallize scheduler based on optarg of S
    switch (schedType[0])
    {
        case 'F':
            schedulerAlgo = new FCFS();
            schedulerAlgo->name = (char *) "FCFS";
            schedulerAlgo->quantum = quantum;
            break;
        case 'L':
            schedulerAlgo = new LCFS();
            schedulerAlgo->name = (char *) "LCFS";
            schedulerAlgo->quantum = quantum;
            break;
        case 'S':
            schedulerAlgo = new SJF();
            schedulerAlgo->name = (char *) "SJF";
            schedulerAlgo->quantum = quantum;
            break;
    // record quantum for R and P
        case 'R':
            schedulerAlgo = new RR();
            schedulerAlgo->name = (char *) "RR";
            quantum = atoi(schedType + 1);
            schedulerAlgo->quantum = quantum;
            break;
        case 'P':
            schedulerAlgo = new PRIO();
            schedulerAlgo->name = (char *) "PRIO";
            quantum = atoi(schedType + 1);
            schedulerAlgo->quantum = quantum;
            ((PRIO *)schedulerAlgo)->activeQueue = 0;
            break;
    }
    // randfile is last arg, input file is 2nd to last arg
    char * randfile = argv[argc-1];
    inFile.open(randfile);
    if (!inFile)
    {
        printf("Unable to open file: %s\n", randfile);
        exit(1);
    }
    inFile >> randSize;
    for (int i = 0; i < randSize; i++)
    {
        int j;
        inFile >> j;
        randVals.push_back(j);
    }
    char * inputfile = argv[argc-2];
    inFile.close();
    inFile.clear();
    inFile.open(inputfile);
    if (!inFile)
    {
        printf("Unable to open file: %s\n", inputfile);
        exit(1);
    }
    Process * P;
    while ((P = new Process()) && inFile >> P->at >> P->tc >> P->cb >> P->io)
    {
        P->tcRem = P->tc;
        P->id = procCount++;
        P->staticPrio = myRandom(4);
        P->dynamicPrio = P->staticPrio - 1;
        P->curState = CREATED;
        P->stateTimeStamp = P->at;
        P->cbRem = 0;
        processList.push_back(P);
        put_event(P, TRANS_TO_READY, P->at);
    }
    summary.numProc = procCount;
    inFile.close();
    Simulation();
    // Print summary info to stdout
    if (schedType[0] == 'R' || schedType[0] == 'P')
        printf("%s %d\n", schedulerAlgo->name, quantum);
    else
        printf("%s\n", schedulerAlgo->name);
    for (iter = processList.begin(); iter != processList.end(); iter++)
    {
        printf("%04d: %4d %4d %4d %4d %1d | %5d %5d %5d %5d\n", (*iter)->id, (*iter)->at, (*iter)->tc, (*iter)->cb, (*iter)->io, (*iter)->staticPrio, (*iter)->ft, (*iter)->tt, (*iter)->it, (*iter)->cw);
        summary.cwTotal += (*iter)->cw;
        summary.ttTotal += (*iter)->tt;
    }
    printf("SUM: %d %.2lf %.2lf %.2lf %.2lf %.3lf\n", summary.ft, summary.cpuTotal*100/summary.ft, summary.ioTotal*100/summary.ft, summary.ttTotal/summary.numProc, summary.cwTotal/summary.numProc, summary.numProc*100/summary.ft);
}
