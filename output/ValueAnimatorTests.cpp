#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class ValueAnimatorTests : public testing::Test {
public:
long WAIT_TIME_OUT=5000  ;
ACE_EXPORTAnimator *  a1;
ACE_EXPORTAnimator *  a2;
long TOLERANCE=100  ;
long POLL_INTERVAL=100  ;
float A1_START_VALUE=0f  ;
float A1_END_VALUE=1f  ;
int A2_START_VALUE=100  ;
int A2_END_VALUE=200  ;
long DEFAULT_FRAME_INTERVAL=5  ;
long COMMIT_DELAY=3  ;
//以下组件没有匹配:
//ActivityTestRule<BasicAnimatorActivity>,ActivityTestRule
ActivityTestRule<BasicAnimatorActivity> *  mActivityRule=new ActivityTestRule(BasicAnimatorActivity.class  )  ;
MyUpdateListener{boolean wasRunning=false  ;long firstRunningFrameTime=-1  ;long lastUpdateTime=-1  ;float startFraction=0  ;void onAnimationUpdate(ACE_EXPORTAnimator *  animation) { lastUpdateTime=SystemClock->uptimeMillis()  ;if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; } }void reset() { wasRunning=false  ;firstRunningFrameTime=-1  ;lastUpdateTime=-1  ;startFraction=0  ; }}
MyListener{boolean startCalled=false  ;boolean cancelCalled=false  ;boolean endCalled=false  ;long startTime=-1  ;long endTime=-1  ;void onAnimationStart(ACE_EXPORTAnimator *  animation) { startCalled=true  ;startTime=SystemClock->uptimeMillis()  ; }void onAnimationEnd(ACE_EXPORTAnimator *  animation) { endCalled=true  ;endTime=SystemClock->uptimeMillis()  ; }void onAnimationCancel(ACE_EXPORTAnimator *  animation) { cancelCalled=true  ; }void onAnimationRepeat(ACE_EXPORTAnimator *  animation) { }}
MyPauseListener{boolean pauseCalled=false  ;boolean resumeCalled=false  ;void onAnimationPause(ACE_EXPORTAnimator *  animation) { pauseCalled=true  ; }void onAnimationResume(ACE_EXPORTAnimator *  animation) { resumeCalled=true  ; }}
MyFrameCallbackProvider{Handler *  mHandler=null  ;int MSG_FRAME=0  ;long mFrameDelay=DEFAULT_FRAME_INTERVAL;ArrayList<Choreographer.FrameCallback> *  mFrameCallbacks=new ArrayList()  ;AssertionError *  mAssertionError=null  ;LooperThread *  mThread=new LooperThread()  ;nullvoid postFrameCallback(Choreographer.FrameCallback *  callback) { mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); } }void postCommitCallback(Runnable *  runnable) { mHandler->postDelayed(runnable,COMMIT_DELAY); }long getFrameTime() { return SystemClock->uptimeMillis()  ; }long getFrameDelay() { return mFrameDelay; }void setFrameDelay(long delay) { mFrameDelay=delay;if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); } }LooperThread{void run() { Looper->prepare();mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;Looper->loop(); }}}
void setUp()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void tearDown()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void setUp(){a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );}
void tearDown(){a1=null  ;a2=null  ;}
}
#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class ValueAnimatorTests : public testing::Test {
public:
long WAIT_TIME_OUT=5000  ;
ACE_EXPORTAnimator *  a1;
ACE_EXPORTAnimator *  a2;
long TOLERANCE=100  ;
long POLL_INTERVAL=100  ;
float A1_START_VALUE=0f  ;
float A1_END_VALUE=1f  ;
int A2_START_VALUE=100  ;
int A2_END_VALUE=200  ;
long DEFAULT_FRAME_INTERVAL=5  ;
long COMMIT_DELAY=3  ;
//以下组件没有匹配:
//ActivityTestRule<BasicAnimatorActivity>,ActivityTestRule
ActivityTestRule<BasicAnimatorActivity> *  mActivityRule=new ActivityTestRule(BasicAnimatorActivity.class  )  ;
MyUpdateListener{boolean wasRunning=false  ;long firstRunningFrameTime=-1  ;long lastUpdateTime=-1  ;float startFraction=0  ;void onAnimationUpdate(ACE_EXPORTAnimator *  animation) { lastUpdateTime=SystemClock->uptimeMillis()  ;if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; } }void reset() { wasRunning=false  ;firstRunningFrameTime=-1  ;lastUpdateTime=-1  ;startFraction=0  ; }}
MyListener{boolean startCalled=false  ;boolean cancelCalled=false  ;boolean endCalled=false  ;long startTime=-1  ;long endTime=-1  ;void onAnimationStart(ACE_EXPORTAnimator *  animation) { startCalled=true  ;startTime=SystemClock->uptimeMillis()  ; }void onAnimationEnd(ACE_EXPORTAnimator *  animation) { endCalled=true  ;endTime=SystemClock->uptimeMillis()  ; }void onAnimationCancel(ACE_EXPORTAnimator *  animation) { cancelCalled=true  ; }void onAnimationRepeat(ACE_EXPORTAnimator *  animation) { }}
MyPauseListener{boolean pauseCalled=false  ;boolean resumeCalled=false  ;void onAnimationPause(ACE_EXPORTAnimator *  animation) { pauseCalled=true  ; }void onAnimationResume(ACE_EXPORTAnimator *  animation) { resumeCalled=true  ; }}
MyFrameCallbackProvider{Handler *  mHandler=null  ;int MSG_FRAME=0  ;long mFrameDelay=DEFAULT_FRAME_INTERVAL;ArrayList<Choreographer.FrameCallback> *  mFrameCallbacks=new ArrayList()  ;AssertionError *  mAssertionError=null  ;LooperThread *  mThread=new LooperThread()  ;nullvoid postFrameCallback(Choreographer.FrameCallback *  callback) { mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); } }void postCommitCallback(Runnable *  runnable) { mHandler->postDelayed(runnable,COMMIT_DELAY); }long getFrameTime() { return SystemClock->uptimeMillis()  ; }long getFrameDelay() { return mFrameDelay; }void setFrameDelay(long delay) { mFrameDelay=delay;if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); } }LooperThread{void run() { Looper->prepare();mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;Looper->loop(); }}}
void setUp()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void tearDown()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void setUp(){a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );}
void tearDown(){a1=null  ;a2=null  ;}
}
#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class ValueAnimatorTests : public testing::Test {
public:
long WAIT_TIME_OUT=5000  ;
ACE_EXPORTAnimator *  a1;
ACE_EXPORTAnimator *  a2;
long TOLERANCE=100  ;
long POLL_INTERVAL=100  ;
float A1_START_VALUE=0f  ;
float A1_END_VALUE=1f  ;
int A2_START_VALUE=100  ;
int A2_END_VALUE=200  ;
long DEFAULT_FRAME_INTERVAL=5  ;
long COMMIT_DELAY=3  ;
//以下组件没有匹配:
//ActivityTestRule<BasicAnimatorActivity>,ActivityTestRule
ActivityTestRule<BasicAnimatorActivity> *  mActivityRule=new ActivityTestRule(BasicAnimatorActivity.class  )  ;
MyUpdateListener{boolean wasRunning=false  ;long firstRunningFrameTime=-1  ;long lastUpdateTime=-1  ;float startFraction=0  ;void onAnimationUpdate(ACE_EXPORTAnimator *  animation) { lastUpdateTime=SystemClock->uptimeMillis()  ;if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; } }void reset() { wasRunning=false  ;firstRunningFrameTime=-1  ;lastUpdateTime=-1  ;startFraction=0  ; }}
MyListener{boolean startCalled=false  ;boolean cancelCalled=false  ;boolean endCalled=false  ;long startTime=-1  ;long endTime=-1  ;void onAnimationStart(ACE_EXPORTAnimator *  animation) { startCalled=true  ;startTime=SystemClock->uptimeMillis()  ; }void onAnimationEnd(ACE_EXPORTAnimator *  animation) { endCalled=true  ;endTime=SystemClock->uptimeMillis()  ; }void onAnimationCancel(ACE_EXPORTAnimator *  animation) { cancelCalled=true  ; }void onAnimationRepeat(ACE_EXPORTAnimator *  animation) { }}
MyPauseListener{boolean pauseCalled=false  ;boolean resumeCalled=false  ;void onAnimationPause(ACE_EXPORTAnimator *  animation) { pauseCalled=true  ; }void onAnimationResume(ACE_EXPORTAnimator *  animation) { resumeCalled=true  ; }}
MyFrameCallbackProvider{Handler *  mHandler=null  ;int MSG_FRAME=0  ;long mFrameDelay=DEFAULT_FRAME_INTERVAL;ArrayList<Choreographer.FrameCallback> *  mFrameCallbacks=new ArrayList()  ;AssertionError *  mAssertionError=null  ;LooperThread *  mThread=new LooperThread()  ;nullvoid postFrameCallback(Choreographer.FrameCallback *  callback) { mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); } }void postCommitCallback(Runnable *  runnable) { mHandler->postDelayed(runnable,COMMIT_DELAY); }long getFrameTime() { return SystemClock->uptimeMillis()  ; }long getFrameDelay() { return mFrameDelay; }void setFrameDelay(long delay) { mFrameDelay=delay;if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); } }LooperThread{void run() { Looper->prepare();mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;Looper->loop(); }}}
void setUp()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void tearDown()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void setUp(){a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );}
void tearDown(){a1=null  ;a2=null  ;}
}
#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class ValueAnimatorTests : public testing::Test {
public:
long WAIT_TIME_OUT=5000  ;
ACE_EXPORTAnimator *  a1;
ACE_EXPORTAnimator *  a2;
long TOLERANCE=100  ;
long POLL_INTERVAL=100  ;
float A1_START_VALUE=0f  ;
float A1_END_VALUE=1f  ;
int A2_START_VALUE=100  ;
int A2_END_VALUE=200  ;
long DEFAULT_FRAME_INTERVAL=5  ;
long COMMIT_DELAY=3  ;
//以下组件没有匹配:
//ActivityTestRule<BasicAnimatorActivity>,ActivityTestRule
ActivityTestRule<BasicAnimatorActivity> *  mActivityRule=new ActivityTestRule(BasicAnimatorActivity.class  )  ;
MyUpdateListener{boolean wasRunning=false  ;long firstRunningFrameTime=-1  ;long lastUpdateTime=-1  ;float startFraction=0  ;void onAnimationUpdate(ACE_EXPORTAnimator *  animation) { lastUpdateTime=SystemClock->uptimeMillis()  ;if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; } }void reset() { wasRunning=false  ;firstRunningFrameTime=-1  ;lastUpdateTime=-1  ;startFraction=0  ; }}
MyListener{boolean startCalled=false  ;boolean cancelCalled=false  ;boolean endCalled=false  ;long startTime=-1  ;long endTime=-1  ;void onAnimationStart(ACE_EXPORTAnimator *  animation) { startCalled=true  ;startTime=SystemClock->uptimeMillis()  ; }void onAnimationEnd(ACE_EXPORTAnimator *  animation) { endCalled=true  ;endTime=SystemClock->uptimeMillis()  ; }void onAnimationCancel(ACE_EXPORTAnimator *  animation) { cancelCalled=true  ; }void onAnimationRepeat(ACE_EXPORTAnimator *  animation) { }}
MyPauseListener{boolean pauseCalled=false  ;boolean resumeCalled=false  ;void onAnimationPause(ACE_EXPORTAnimator *  animation) { pauseCalled=true  ; }void onAnimationResume(ACE_EXPORTAnimator *  animation) { resumeCalled=true  ; }}
MyFrameCallbackProvider{Handler *  mHandler=null  ;int MSG_FRAME=0  ;long mFrameDelay=DEFAULT_FRAME_INTERVAL;ArrayList<Choreographer.FrameCallback> *  mFrameCallbacks=new ArrayList()  ;AssertionError *  mAssertionError=null  ;LooperThread *  mThread=new LooperThread()  ;nullvoid postFrameCallback(Choreographer.FrameCallback *  callback) { mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); } }void postCommitCallback(Runnable *  runnable) { mHandler->postDelayed(runnable,COMMIT_DELAY); }long getFrameTime() { return SystemClock->uptimeMillis()  ; }long getFrameDelay() { return mFrameDelay; }void setFrameDelay(long delay) { mFrameDelay=delay;if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); } }LooperThread{void run() { Looper->prepare();mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;Looper->loop(); }}}
void setUp()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void tearDown()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void setUp(){a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );}
void tearDown(){a1=null  ;a2=null  ;}
}
#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class ValueAnimatorTests : public testing::Test {
public:
long WAIT_TIME_OUT=5000  ;
ACE_EXPORTAnimator *  a1;
ACE_EXPORTAnimator *  a2;
long TOLERANCE=100  ;
long POLL_INTERVAL=100  ;
float A1_START_VALUE=0f  ;
float A1_END_VALUE=1f  ;
int A2_START_VALUE=100  ;
int A2_END_VALUE=200  ;
long DEFAULT_FRAME_INTERVAL=5  ;
long COMMIT_DELAY=3  ;
//以下组件没有匹配:
//ActivityTestRule<BasicAnimatorActivity>,ActivityTestRule
ActivityTestRule<BasicAnimatorActivity> *  mActivityRule=new ActivityTestRule(BasicAnimatorActivity.class  )  ;
MyUpdateListener{boolean wasRunning=false  ;long firstRunningFrameTime=-1  ;long lastUpdateTime=-1  ;float startFraction=0  ;void onAnimationUpdate(ACE_EXPORTAnimator *  animation) { lastUpdateTime=SystemClock->uptimeMillis()  ;if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; } }void reset() { wasRunning=false  ;firstRunningFrameTime=-1  ;lastUpdateTime=-1  ;startFraction=0  ; }}
MyListener{boolean startCalled=false  ;boolean cancelCalled=false  ;boolean endCalled=false  ;long startTime=-1  ;long endTime=-1  ;void onAnimationStart(ACE_EXPORTAnimator *  animation) { startCalled=true  ;startTime=SystemClock->uptimeMillis()  ; }void onAnimationEnd(ACE_EXPORTAnimator *  animation) { endCalled=true  ;endTime=SystemClock->uptimeMillis()  ; }void onAnimationCancel(ACE_EXPORTAnimator *  animation) { cancelCalled=true  ; }void onAnimationRepeat(ACE_EXPORTAnimator *  animation) { }}
MyPauseListener{boolean pauseCalled=false  ;boolean resumeCalled=false  ;void onAnimationPause(ACE_EXPORTAnimator *  animation) { pauseCalled=true  ; }void onAnimationResume(ACE_EXPORTAnimator *  animation) { resumeCalled=true  ; }}
MyFrameCallbackProvider{Handler *  mHandler=null  ;int MSG_FRAME=0  ;long mFrameDelay=DEFAULT_FRAME_INTERVAL;ArrayList<Choreographer.FrameCallback> *  mFrameCallbacks=new ArrayList()  ;AssertionError *  mAssertionError=null  ;LooperThread *  mThread=new LooperThread()  ;nullvoid postFrameCallback(Choreographer.FrameCallback *  callback) { mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); } }void postCommitCallback(Runnable *  runnable) { mHandler->postDelayed(runnable,COMMIT_DELAY); }long getFrameTime() { return SystemClock->uptimeMillis()  ; }long getFrameDelay() { return mFrameDelay; }void setFrameDelay(long delay) { mFrameDelay=delay;if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); } }LooperThread{void run() { Looper->prepare();mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;Looper->loop(); }}}
void setUp()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void tearDown()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void setUp(){a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );}
void tearDown(){a1=null  ;a2=null  ;}
}
#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class ValueAnimatorTests : public testing::Test {
public:
long WAIT_TIME_OUT=5000  ;
ACE_EXPORTAnimator *  a1;
ACE_EXPORTAnimator *  a2;
long TOLERANCE=100  ;
long POLL_INTERVAL=100  ;
float A1_START_VALUE=0f  ;
float A1_END_VALUE=1f  ;
int A2_START_VALUE=100  ;
int A2_END_VALUE=200  ;
long DEFAULT_FRAME_INTERVAL=5  ;
long COMMIT_DELAY=3  ;
//以下组件没有匹配:
//ActivityTestRule<BasicAnimatorActivity>,ActivityTestRule
ActivityTestRule<BasicAnimatorActivity> *  mActivityRule=new ActivityTestRule(BasicAnimatorActivity.class  )  ;
MyUpdateListener{boolean wasRunning=false  ;long firstRunningFrameTime=-1  ;long lastUpdateTime=-1  ;float startFraction=0  ;void onAnimationUpdate(ACE_EXPORTAnimator *  animation) { lastUpdateTime=SystemClock->uptimeMillis()  ;if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; } }void reset() { wasRunning=false  ;firstRunningFrameTime=-1  ;lastUpdateTime=-1  ;startFraction=0  ; }}
MyListener{boolean startCalled=false  ;boolean cancelCalled=false  ;boolean endCalled=false  ;long startTime=-1  ;long endTime=-1  ;void onAnimationStart(ACE_EXPORTAnimator *  animation) { startCalled=true  ;startTime=SystemClock->uptimeMillis()  ; }void onAnimationEnd(ACE_EXPORTAnimator *  animation) { endCalled=true  ;endTime=SystemClock->uptimeMillis()  ; }void onAnimationCancel(ACE_EXPORTAnimator *  animation) { cancelCalled=true  ; }void onAnimationRepeat(ACE_EXPORTAnimator *  animation) { }}
MyPauseListener{boolean pauseCalled=false  ;boolean resumeCalled=false  ;void onAnimationPause(ACE_EXPORTAnimator *  animation) { pauseCalled=true  ; }void onAnimationResume(ACE_EXPORTAnimator *  animation) { resumeCalled=true  ; }}
MyFrameCallbackProvider{Handler *  mHandler=null  ;int MSG_FRAME=0  ;long mFrameDelay=DEFAULT_FRAME_INTERVAL;ArrayList<Choreographer.FrameCallback> *  mFrameCallbacks=new ArrayList()  ;AssertionError *  mAssertionError=null  ;LooperThread *  mThread=new LooperThread()  ;nullvoid postFrameCallback(Choreographer.FrameCallback *  callback) { mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); } }void postCommitCallback(Runnable *  runnable) { mHandler->postDelayed(runnable,COMMIT_DELAY); }long getFrameTime() { return SystemClock->uptimeMillis()  ; }long getFrameDelay() { return mFrameDelay; }void setFrameDelay(long delay) { mFrameDelay=delay;if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); } }LooperThread{void run() { Looper->prepare();mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;Looper->loop(); }}}
void setUp()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void tearDown()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void setUp(){a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );}
void tearDown(){a1=null  ;a2=null  ;}
}
#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class ValueAnimatorTests : public testing::Test {
public:
long WAIT_TIME_OUT=5000  ;
ACE_EXPORTAnimator *  a1;
ACE_EXPORTAnimator *  a2;
long TOLERANCE=100  ;
long POLL_INTERVAL=100  ;
float A1_START_VALUE=0f  ;
float A1_END_VALUE=1f  ;
int A2_START_VALUE=100  ;
int A2_END_VALUE=200  ;
long DEFAULT_FRAME_INTERVAL=5  ;
long COMMIT_DELAY=3  ;
//以下组件没有匹配:
//ActivityTestRule<BasicAnimatorActivity>,ActivityTestRule
ActivityTestRule<BasicAnimatorActivity> *  mActivityRule=new ActivityTestRule(BasicAnimatorActivity.class  )  ;
MyUpdateListener{boolean wasRunning=false  ;long firstRunningFrameTime=-1  ;long lastUpdateTime=-1  ;float startFraction=0  ;void onAnimationUpdate(ACE_EXPORTAnimator *  animation) { lastUpdateTime=SystemClock->uptimeMillis()  ;if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; } }void reset() { wasRunning=false  ;firstRunningFrameTime=-1  ;lastUpdateTime=-1  ;startFraction=0  ; }}
MyListener{boolean startCalled=false  ;boolean cancelCalled=false  ;boolean endCalled=false  ;long startTime=-1  ;long endTime=-1  ;void onAnimationStart(ACE_EXPORTAnimator *  animation) { startCalled=true  ;startTime=SystemClock->uptimeMillis()  ; }void onAnimationEnd(ACE_EXPORTAnimator *  animation) { endCalled=true  ;endTime=SystemClock->uptimeMillis()  ; }void onAnimationCancel(ACE_EXPORTAnimator *  animation) { cancelCalled=true  ; }void onAnimationRepeat(ACE_EXPORTAnimator *  animation) { }}
MyPauseListener{boolean pauseCalled=false  ;boolean resumeCalled=false  ;void onAnimationPause(ACE_EXPORTAnimator *  animation) { pauseCalled=true  ; }void onAnimationResume(ACE_EXPORTAnimator *  animation) { resumeCalled=true  ; }}
MyFrameCallbackProvider{Handler *  mHandler=null  ;int MSG_FRAME=0  ;long mFrameDelay=DEFAULT_FRAME_INTERVAL;ArrayList<Choreographer.FrameCallback> *  mFrameCallbacks=new ArrayList()  ;AssertionError *  mAssertionError=null  ;LooperThread *  mThread=new LooperThread()  ;nullvoid postFrameCallback(Choreographer.FrameCallback *  callback) { mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); } }void postCommitCallback(Runnable *  runnable) { mHandler->postDelayed(runnable,COMMIT_DELAY); }long getFrameTime() { return SystemClock->uptimeMillis()  ; }long getFrameDelay() { return mFrameDelay; }void setFrameDelay(long delay) { mFrameDelay=delay;if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); } }LooperThread{void run() { Looper->prepare();mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;Looper->loop(); }}}
void setUp()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void tearDown()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void setUp(){a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );}
void tearDown(){a1=null  ;a2=null  ;}
}
#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class ValueAnimatorTests : public testing::Test {
public:
long WAIT_TIME_OUT=5000  ;
ACE_EXPORTAnimator *  a1;
ACE_EXPORTAnimator *  a2;
long TOLERANCE=100  ;
long POLL_INTERVAL=100  ;
float A1_START_VALUE=0f  ;
float A1_END_VALUE=1f  ;
int A2_START_VALUE=100  ;
int A2_END_VALUE=200  ;
long DEFAULT_FRAME_INTERVAL=5  ;
long COMMIT_DELAY=3  ;
//以下组件没有匹配:
//ActivityTestRule<BasicAnimatorActivity>,ActivityTestRule
ActivityTestRule<BasicAnimatorActivity> *  mActivityRule=new ActivityTestRule(BasicAnimatorActivity.class  )  ;
MyUpdateListener{boolean wasRunning=false  ;long firstRunningFrameTime=-1  ;long lastUpdateTime=-1  ;float startFraction=0  ;void onAnimationUpdate(ACE_EXPORTAnimator *  animation) { lastUpdateTime=SystemClock->uptimeMillis()  ;if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; } }void reset() { wasRunning=false  ;firstRunningFrameTime=-1  ;lastUpdateTime=-1  ;startFraction=0  ; }}
MyListener{boolean startCalled=false  ;boolean cancelCalled=false  ;boolean endCalled=false  ;long startTime=-1  ;long endTime=-1  ;void onAnimationStart(ACE_EXPORTAnimator *  animation) { startCalled=true  ;startTime=SystemClock->uptimeMillis()  ; }void onAnimationEnd(ACE_EXPORTAnimator *  animation) { endCalled=true  ;endTime=SystemClock->uptimeMillis()  ; }void onAnimationCancel(ACE_EXPORTAnimator *  animation) { cancelCalled=true  ; }void onAnimationRepeat(ACE_EXPORTAnimator *  animation) { }}
MyPauseListener{boolean pauseCalled=false  ;boolean resumeCalled=false  ;void onAnimationPause(ACE_EXPORTAnimator *  animation) { pauseCalled=true  ; }void onAnimationResume(ACE_EXPORTAnimator *  animation) { resumeCalled=true  ; }}
MyFrameCallbackProvider{Handler *  mHandler=null  ;int MSG_FRAME=0  ;long mFrameDelay=DEFAULT_FRAME_INTERVAL;ArrayList<Choreographer.FrameCallback> *  mFrameCallbacks=new ArrayList()  ;AssertionError *  mAssertionError=null  ;LooperThread *  mThread=new LooperThread()  ;nullvoid postFrameCallback(Choreographer.FrameCallback *  callback) { mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); } }void postCommitCallback(Runnable *  runnable) { mHandler->postDelayed(runnable,COMMIT_DELAY); }long getFrameTime() { return SystemClock->uptimeMillis()  ; }long getFrameDelay() { return mFrameDelay; }void setFrameDelay(long delay) { mFrameDelay=delay;if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); } }LooperThread{void run() { Looper->prepare();mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;Looper->loop(); }}}
void setUp()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void tearDown()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void setUp(){a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );}
void tearDown(){a1=null  ;a2=null  ;}
}
#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class ValueAnimatorTests : public testing::Test {
public:
long WAIT_TIME_OUT=5000  ;
ACE_EXPORTAnimator *  a1;
ACE_EXPORTAnimator *  a2;
long TOLERANCE=100  ;
long POLL_INTERVAL=100  ;
float A1_START_VALUE=0f  ;
float A1_END_VALUE=1f  ;
int A2_START_VALUE=100  ;
int A2_END_VALUE=200  ;
long DEFAULT_FRAME_INTERVAL=5  ;
long COMMIT_DELAY=3  ;
//以下组件没有匹配:
//ActivityTestRule<BasicAnimatorActivity>,ActivityTestRule
ActivityTestRule<BasicAnimatorActivity> *  mActivityRule=new ActivityTestRule(BasicAnimatorActivity.class  )  ;
MyUpdateListener{boolean wasRunning=false  ;long firstRunningFrameTime=-1  ;long lastUpdateTime=-1  ;float startFraction=0  ;void onAnimationUpdate(ACE_EXPORTAnimator *  animation) { lastUpdateTime=SystemClock->uptimeMillis()  ;if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; } }void reset() { wasRunning=false  ;firstRunningFrameTime=-1  ;lastUpdateTime=-1  ;startFraction=0  ; }}
MyListener{boolean startCalled=false  ;boolean cancelCalled=false  ;boolean endCalled=false  ;long startTime=-1  ;long endTime=-1  ;void onAnimationStart(ACE_EXPORTAnimator *  animation) { startCalled=true  ;startTime=SystemClock->uptimeMillis()  ; }void onAnimationEnd(ACE_EXPORTAnimator *  animation) { endCalled=true  ;endTime=SystemClock->uptimeMillis()  ; }void onAnimationCancel(ACE_EXPORTAnimator *  animation) { cancelCalled=true  ; }void onAnimationRepeat(ACE_EXPORTAnimator *  animation) { }}
MyPauseListener{boolean pauseCalled=false  ;boolean resumeCalled=false  ;void onAnimationPause(ACE_EXPORTAnimator *  animation) { pauseCalled=true  ; }void onAnimationResume(ACE_EXPORTAnimator *  animation) { resumeCalled=true  ; }}
MyFrameCallbackProvider{Handler *  mHandler=null  ;int MSG_FRAME=0  ;long mFrameDelay=DEFAULT_FRAME_INTERVAL;ArrayList<Choreographer.FrameCallback> *  mFrameCallbacks=new ArrayList()  ;AssertionError *  mAssertionError=null  ;LooperThread *  mThread=new LooperThread()  ;nullvoid postFrameCallback(Choreographer.FrameCallback *  callback) { mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); } }void postCommitCallback(Runnable *  runnable) { mHandler->postDelayed(runnable,COMMIT_DELAY); }long getFrameTime() { return SystemClock->uptimeMillis()  ; }long getFrameDelay() { return mFrameDelay; }void setFrameDelay(long delay) { mFrameDelay=delay;if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); } }LooperThread{void run() { Looper->prepare();mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;Looper->loop(); }}}
void setUp()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void tearDown()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void setUp(){a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );}
void tearDown(){a1=null  ;a2=null  ;}
}
#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class ValueAnimatorTests : public testing::Test {
public:
long WAIT_TIME_OUT=5000  ;
ACE_EXPORTAnimator *  a1;
ACE_EXPORTAnimator *  a2;
long TOLERANCE=100  ;
long POLL_INTERVAL=100  ;
float A1_START_VALUE=0f  ;
float A1_END_VALUE=1f  ;
int A2_START_VALUE=100  ;
int A2_END_VALUE=200  ;
long DEFAULT_FRAME_INTERVAL=5  ;
long COMMIT_DELAY=3  ;
//以下组件没有匹配:
//ActivityTestRule<BasicAnimatorActivity>,ActivityTestRule
ActivityTestRule<BasicAnimatorActivity> *  mActivityRule=new ActivityTestRule(BasicAnimatorActivity.class  )  ;
MyUpdateListener{boolean wasRunning=false  ;long firstRunningFrameTime=-1  ;long lastUpdateTime=-1  ;float startFraction=0  ;void onAnimationUpdate(ACE_EXPORTAnimator *  animation) { lastUpdateTime=SystemClock->uptimeMillis()  ;if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; } }void reset() { wasRunning=false  ;firstRunningFrameTime=-1  ;lastUpdateTime=-1  ;startFraction=0  ; }}
MyListener{boolean startCalled=false  ;boolean cancelCalled=false  ;boolean endCalled=false  ;long startTime=-1  ;long endTime=-1  ;void onAnimationStart(ACE_EXPORTAnimator *  animation) { startCalled=true  ;startTime=SystemClock->uptimeMillis()  ; }void onAnimationEnd(ACE_EXPORTAnimator *  animation) { endCalled=true  ;endTime=SystemClock->uptimeMillis()  ; }void onAnimationCancel(ACE_EXPORTAnimator *  animation) { cancelCalled=true  ; }void onAnimationRepeat(ACE_EXPORTAnimator *  animation) { }}
MyPauseListener{boolean pauseCalled=false  ;boolean resumeCalled=false  ;void onAnimationPause(ACE_EXPORTAnimator *  animation) { pauseCalled=true  ; }void onAnimationResume(ACE_EXPORTAnimator *  animation) { resumeCalled=true  ; }}
MyFrameCallbackProvider{Handler *  mHandler=null  ;int MSG_FRAME=0  ;long mFrameDelay=DEFAULT_FRAME_INTERVAL;ArrayList<Choreographer.FrameCallback> *  mFrameCallbacks=new ArrayList()  ;AssertionError *  mAssertionError=null  ;LooperThread *  mThread=new LooperThread()  ;nullvoid postFrameCallback(Choreographer.FrameCallback *  callback) { mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); } }void postCommitCallback(Runnable *  runnable) { mHandler->postDelayed(runnable,COMMIT_DELAY); }long getFrameTime() { return SystemClock->uptimeMillis()  ; }long getFrameDelay() { return mFrameDelay; }void setFrameDelay(long delay) { mFrameDelay=delay;if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); } }LooperThread{void run() { Looper->prepare();mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;Looper->loop(); }}}
void setUp()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void tearDown()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void setUp(){a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );}
void tearDown(){a1=null  ;a2=null  ;}
}
#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class ValueAnimatorTests : public testing::Test {
public:
long WAIT_TIME_OUT=5000  ;
ACE_EXPORTAnimator *  a1;
ACE_EXPORTAnimator *  a2;
long TOLERANCE=100  ;
long POLL_INTERVAL=100  ;
float A1_START_VALUE=0f  ;
float A1_END_VALUE=1f  ;
int A2_START_VALUE=100  ;
int A2_END_VALUE=200  ;
long DEFAULT_FRAME_INTERVAL=5  ;
long COMMIT_DELAY=3  ;
//以下组件没有匹配:
//ActivityTestRule<BasicAnimatorActivity>,ActivityTestRule
ActivityTestRule<BasicAnimatorActivity> *  mActivityRule=new ActivityTestRule(BasicAnimatorActivity.class  )  ;
MyUpdateListener{boolean wasRunning=false  ;long firstRunningFrameTime=-1  ;long lastUpdateTime=-1  ;float startFraction=0  ;void onAnimationUpdate(ACE_EXPORTAnimator *  animation) { lastUpdateTime=SystemClock->uptimeMillis()  ;if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; } }void reset() { wasRunning=false  ;firstRunningFrameTime=-1  ;lastUpdateTime=-1  ;startFraction=0  ; }}
MyListener{boolean startCalled=false  ;boolean cancelCalled=false  ;boolean endCalled=false  ;long startTime=-1  ;long endTime=-1  ;void onAnimationStart(ACE_EXPORTAnimator *  animation) { startCalled=true  ;startTime=SystemClock->uptimeMillis()  ; }void onAnimationEnd(ACE_EXPORTAnimator *  animation) { endCalled=true  ;endTime=SystemClock->uptimeMillis()  ; }void onAnimationCancel(ACE_EXPORTAnimator *  animation) { cancelCalled=true  ; }void onAnimationRepeat(ACE_EXPORTAnimator *  animation) { }}
MyPauseListener{boolean pauseCalled=false  ;boolean resumeCalled=false  ;void onAnimationPause(ACE_EXPORTAnimator *  animation) { pauseCalled=true  ; }void onAnimationResume(ACE_EXPORTAnimator *  animation) { resumeCalled=true  ; }}
MyFrameCallbackProvider{Handler *  mHandler=null  ;int MSG_FRAME=0  ;long mFrameDelay=DEFAULT_FRAME_INTERVAL;ArrayList<Choreographer.FrameCallback> *  mFrameCallbacks=new ArrayList()  ;AssertionError *  mAssertionError=null  ;LooperThread *  mThread=new LooperThread()  ;nullvoid postFrameCallback(Choreographer.FrameCallback *  callback) { mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); } }void postCommitCallback(Runnable *  runnable) { mHandler->postDelayed(runnable,COMMIT_DELAY); }long getFrameTime() { return SystemClock->uptimeMillis()  ; }long getFrameDelay() { return mFrameDelay; }void setFrameDelay(long delay) { mFrameDelay=delay;if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); } }LooperThread{void run() { Looper->prepare();mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;Looper->loop(); }}}
void setUp()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void tearDown()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void setUp(){a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );}
void tearDown(){a1=null  ;a2=null  ;}
}
#include "gtest/gtest.h"
using namespace testing;
using namespace testing::ext;
class ValueAnimatorTests : public testing::Test {
public:
long WAIT_TIME_OUT=5000  ;
ACE_EXPORTAnimator *  a1;
ACE_EXPORTAnimator *  a2;
long TOLERANCE=100  ;
long POLL_INTERVAL=100  ;
float A1_START_VALUE=0f  ;
float A1_END_VALUE=1f  ;
int A2_START_VALUE=100  ;
int A2_END_VALUE=200  ;
long DEFAULT_FRAME_INTERVAL=5  ;
long COMMIT_DELAY=3  ;
//以下组件没有匹配:
//ActivityTestRule<BasicAnimatorActivity>,ActivityTestRule
ActivityTestRule<BasicAnimatorActivity> *  mActivityRule=new ActivityTestRule(BasicAnimatorActivity.class  )  ;
MyUpdateListener{boolean wasRunning=false  ;long firstRunningFrameTime=-1  ;long lastUpdateTime=-1  ;float startFraction=0  ;void onAnimationUpdate(ACE_EXPORTAnimator *  animation) { lastUpdateTime=SystemClock->uptimeMillis()  ;if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; } }void reset() { wasRunning=false  ;firstRunningFrameTime=-1  ;lastUpdateTime=-1  ;startFraction=0  ; }}
MyListener{boolean startCalled=false  ;boolean cancelCalled=false  ;boolean endCalled=false  ;long startTime=-1  ;long endTime=-1  ;void onAnimationStart(ACE_EXPORTAnimator *  animation) { startCalled=true  ;startTime=SystemClock->uptimeMillis()  ; }void onAnimationEnd(ACE_EXPORTAnimator *  animation) { endCalled=true  ;endTime=SystemClock->uptimeMillis()  ; }void onAnimationCancel(ACE_EXPORTAnimator *  animation) { cancelCalled=true  ; }void onAnimationRepeat(ACE_EXPORTAnimator *  animation) { }}
MyPauseListener{boolean pauseCalled=false  ;boolean resumeCalled=false  ;void onAnimationPause(ACE_EXPORTAnimator *  animation) { pauseCalled=true  ; }void onAnimationResume(ACE_EXPORTAnimator *  animation) { resumeCalled=true  ; }}
MyFrameCallbackProvider{Handler *  mHandler=null  ;int MSG_FRAME=0  ;long mFrameDelay=DEFAULT_FRAME_INTERVAL;ArrayList<Choreographer.FrameCallback> *  mFrameCallbacks=new ArrayList()  ;AssertionError *  mAssertionError=null  ;LooperThread *  mThread=new LooperThread()  ;nullvoid postFrameCallback(Choreographer.FrameCallback *  callback) { mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); } }void postCommitCallback(Runnable *  runnable) { mHandler->postDelayed(runnable,COMMIT_DELAY); }long getFrameTime() { return SystemClock->uptimeMillis()  ; }long getFrameDelay() { return mFrameDelay; }void setFrameDelay(long delay) { mFrameDelay=delay;if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); } }LooperThread{void run() { Looper->prepare();mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;Looper->loop(); }}}
void setUp()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void tearDown()
{
lastUpdateTime=SystemClock->uptimeMillis()  ;
firstRunningFrameTime=lastUpdateTime;
startFraction=animation->getAnimatedFraction()  ;
wasRunning=animation->isRunning()  ;
if(animation->isRunning()   && !wasRunning){ firstRunningFrameTime=lastUpdateTime;startFraction=animation->getAnimatedFraction()  ;wasRunning=animation->isRunning()  ; }
wasRunning=false  ;
firstRunningFrameTime=-1  ;
lastUpdateTime=-1  ;
startFraction=0  ;
startCalled=true  ;
startTime=SystemClock->uptimeMillis()  ;
endCalled=true  ;
endTime=SystemClock->uptimeMillis()  ;
cancelCalled=true  ;
pauseCalled=true  ;
resumeCalled=true  ;
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
mFrameCallbacks->add(callback);
if(!mFrameCallbacks->contains(callback)  ){ mFrameCallbacks->add(callback); }
mHandler->postDelayed(runnable,COMMIT_DELAY);
return SystemClock->uptimeMillis()  ;
return mFrameDelay;
mFrameDelay=delay;
mHandler->removeMessages(MSG_FRAME);
mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay);
if(mFrameCallbacks->size()   != 0  ){ mHandler->removeMessages(MSG_FRAME);mHandler->sendEmptyMessageDelayed(MSG_FRAME,mFrameDelay); }
Looper->prepare();
mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  );
for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }
break;
break;
switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; }
{ switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } }
mHandler=new Handler(){void handleMessage(Message *  msg) { { switch(msg->what){caseMSG_FRAME: for(int i=0  ;i < mFrameCallbacks->size()  ;i++){ mFrameCallbacks->get(i)  -> doFrame(SystemClock->uptimeMillis()  ); }break; default: break; } } }}  ;
Looper->loop();
a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );
a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );
a1=null  ;
a2=null  ;
}
void setUp(){a1=ValueAnimator->ofFloat(A1_START_VALUE,A1_END_VALUE)  -> setDuration(300  );a2=ValueAnimator->ofInt(A2_START_VALUE,A2_END_VALUE)  -> setDuration(500  );}
void tearDown(){a1=null  ;a2=null  ;}
}
