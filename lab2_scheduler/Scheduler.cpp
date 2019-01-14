// Scheduler.cpp by Ryan Moriarty

#include <cstdio>
#include <cstdlib>
#include <list>
#include <queue>
#include "Process.cpp"

using namespace std;

class Scheduler
{
public:
    char * name;
    int quantum;
    virtual
    void add_process(Process *p) {};
    virtual
    Process* get_next_process(){return NULL;};
};

class FCFS: public Scheduler
{
public:
    queue <Process *> runQueue;
    void add_process(Process *p)
    {
        runQueue.push(p);
    };
    Process* get_next_process()
    {
        if (runQueue.empty())
            return NULL;
        Process *p = runQueue.front();
        runQueue.pop();
        return p;
    }
};

class LCFS: public Scheduler
{
public:
     list <Process *> runQueue;
     void add_process(Process *p)
     {
         runQueue.push_front(p);
     };
     Process* get_next_process()
     {
         if (runQueue.empty())
             return NULL;
         Process *p = runQueue.front();
         runQueue.pop_front();
         return p;
     }
};

class SJF: public Scheduler
{
public:
    struct compareProcesses
    {
        bool operator()(Process * const p1, Process * const p2)
        {
            if (p1->tcRem == p2->tcRem)
            {
                if (p1->timeInPrevState == p2->timeInPrevState)
                    return p1->id > p2->id;
                return p1->timeInPrevState < p2->timeInPrevState;
            }
            return p1->tcRem > p2->tcRem;
        }
    };
    priority_queue<Process *, vector<Process *>, compareProcesses > runQueue;
    void add_process(Process *p)
    {
        runQueue.push(p);
    };
    Process* get_next_process()
    {
        if (runQueue.empty())
            return NULL;
        Process *p = runQueue.top();
        runQueue.pop();
        return p;
    }
};

class RR: public Scheduler
{
public:
    queue <Process *> runQueue;
    void add_process(Process *p)
    {
        p->dynamicPrio = p->staticPrio -1;
        runQueue.push(p);
    };
    Process* get_next_process()
    {
        if (runQueue.empty())
            return NULL;
        Process *p = runQueue.front();
        runQueue.pop();
        return p;
    }
};

class PRIO: public Scheduler
{
public:
    int activeQueue;
    queue <Process *> prio_queues[2][4];
    void add_process(Process *p)
    {
        if (p->dynamicPrio < 0)
        {
            p->dynamicPrio = p->staticPrio -1;
            prio_queues[abs(activeQueue - 1)][p->dynamicPrio].push(p);
        }
        else
            prio_queues[activeQueue][p->dynamicPrio].push(p);
    };
    Process* get_next_process()
    {
        for (int i = 3; i >=0; i--)
        {
            if (!prio_queues[activeQueue][i].empty())
            {
                Process *p = prio_queues[activeQueue][i].front();
                prio_queues[activeQueue][i].pop();
                return p;
            }
        }
        // switch queues
        activeQueue = abs(activeQueue - 1);
        for (int i = 3; i >=0; i--)
        {
            if (!prio_queues[activeQueue][i].empty())
            {
                Process *p = prio_queues[activeQueue][i].front();
                prio_queues[activeQueue][i].pop();
                return p;
            }
        }
        return NULL;
    }
};

