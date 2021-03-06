# 实验报告

### 辛极 https://github.com/Ji-Xin/Tankwar

*   代码和实现的功能

    *   Game.java

        负责图形用户界面、游戏逻辑运算、开始暂停、统计得分等基础功能。这是整个项目的核心，其中一个重要的任务是解决碰撞问题：子弹和墙、子弹和坦克、坦克和墙等。有两个子类：

        *   Server.java

            负责服务器段

        *   Client.java

            负责客户端

        虽然名字不同，但是在用户看来，这两部分的界面和功能并无区别。在代码上，两者都有自己独立的计算逻辑（同步机制下面再细说），但是服务器还要负责画地图、让敌方坦克自动移动攻击等。

    *   Tank.java EnemyTank.javaWall.java Bullet.java

        这四个类都是图形界面里的元素，意义如名字。每个类都负责这些对象的绘图、移动等。

        Tank类包含移动、开火等功能。开火带音效。

        EnemyTank是Tank的子类，里面包含让敌方坦克每个一段时间就改变方向、开火等功能。

        Wall只是画墙。

        Bullet规定了子弹的位置和运动方向，一旦产生就会沿着运动方向自己运动。

*   界面设计

    *   主要的游戏区域在左方，右方是统计得分、显示基地血量的区域。上方有一个菜单，可以从中开始、暂停。

*   网络通信和同步

    *   用TCP中的socket。服务器和客户端都会有receiver和sender这两个对象来负责通信，通信全部通过字符串（因为传对象会比较慢）。
    *   游戏开始时，服务器画地图和敌方坦克，并把这些消息发给客户端。
    *   游戏中
        *   服务器和客户端，不管哪边有坦克运动、改方向、开火，都会发消息给对方，让对方也做出相应改变。
        *   为了解决网络波动，每2秒会把整个战场同步一遍，以服务器段为准。
        *   不管哪边接到暂停/恢复的命令，都第一时间发给对方，让对方也暂停。

*   游戏说明

    *   这里针对作业要求做了一个小小的变化：两方都是方向键移动、空格开火。因为如果是同一台机器测试，键盘动作只能被一个窗口接收；如果是两台机器测试，也没有必要做两套。
    *   坦克中枪即掉血，友方坦克和敌方普通坦克只有一血，敌方增强坦克有两血。血掉光了坦克就爆炸（带音效）。友方子弹会穿过自己的坦克，不造成伤害。
    *   同一方坦克相撞就会像撞墙一样卡住动不了，两方坦克相撞就都会爆炸。
    *   基地有两血，被敌方坦克击中一次掉一血。坦克撞基地就像撞墙，不会造成伤害，只是卡住动不了。
    *   己方坦克全部爆炸或者基地零血就输，敌方坦克全部爆炸就赢。