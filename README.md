# PortSniff
A port sniffer with GUI developed by Java and JavaFX, Mac app packaging by Spring.

## Useage
Type the destination URL or IP address amd click Start button. 
Then wait for the result!

## Setting in applicaion
In this app you are able to set which ports for the target by common port 
(21, 23, 25, 80, 110, 139, 443, 1433, 1521, 3389, 8080) or all ports (1 ~ 65535) 
or spcific range.

You also able to set the communication timeout.  Timeout default set to 1 second and minimum 500ms, 
you can set it based on your network conditions to increase the efficiency. 

App also support multi-thread! Yeeewww! You can enable that in setting page. Tasks will be distributed 
on threads and run parallel, that could shrink running time dramatically. You need define the number of
thread you want run once you enable multi-thread.

**This app running in JVM (because it created by Java!). Thus, theoretically the number of thread has no upper bound. 
However, don't explode your computer! Set a reasonable number based on your hardware.**

## Download
Download the jar for cross-platform [here](https://github.com/TyraelDLee/PortSniff/releases/tag/v1.6).
<br>Download the macOS app [here](https://github.com/TyraelDLee/PortSniff/releases/tag/v1.6-mac).
<br><br>对目标服务器的分析：在做了 在做了。
