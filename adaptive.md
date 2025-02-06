# The coverage query of Transition



## 3.Preliminaries



<img src="adaptive.assets/image-20240407223604010.png" alt="image-20240407223604010" style="zoom:50%;" />

**Example 1** 图1展现了一个具体的例子，给定查询Transition $q$，范围$\theta$,Transitions集合$T = \left \{  t_{1},t_{2},t_{3},t_{4},t_{5},t_{6}\right \}$,其中$t_{2}$,$t_{5}$的一个端点在q的范围内，$t_{3}$两个端点都在q的范围内,其余的transition都不在q的范围内，对于$LCQ$查询，我们查询满足至少一个端点在q范围内的transitions,因此$\mathrm{Re}sult=\left \{ t_{2},t_{3},t_{5} \right \}$,对于$TCQ$查询，我们查询满足两个端点都在q范围内的transition，因此$\mathrm{Re}sult=\{t_{3}\}$

## 4. Methodology

![image-20240410164553923](adaptive.assets/image-20240410164553923.png)

我们定义了一个三叉树 AT-tree，其中非叶子节点包含查询$(q,\theta)$，Transition数组$T$的范围(起始和结尾)，然后叶子节点存储的是具体的Transition信息，具体地构建方法如下：

我们通过扫描整个Transition 数组 T 来评估第一个查询 $(q_{1},\theta_1)$，并且在计算结果时，执行crack操作：我们将Transition t ∈ T且$op_2(t,q,\theta)\ne null$放在数组的最前面，将Transition t ∈ T且$op_1(t,q,\theta)\ne null且op_2(t,q,\theta)=null$放在数组的中间，将Transition t ∈ T且$op_1(t,q,\theta)=null$放在数组的最后面。

当新的查询$(q_2,\theta_2)$到来的时候，我们首先判断$(q_1,\theta_1)$和$(q_2,\theta_2)$的关系：

(1)$(q_1,\theta_1)$和$(q_2,\theta_2)$不相交，ie., $ op_1(q_1,q_2,\theta1+\theta2) = null$ 

(2)$(q_1,\theta_1)$和$(q_2,\theta_2)$相交，且$q_1$完全在$q_2$里面 ie., $op_2(q_1,q_2,\theta2-\theta1) \ne null$ 

(3)$(q_1,\theta_1)$和$(q_2,\theta_2)$相交，且$q_2$完全在$q_1$里面 ie., $op_2(q_1,q_2,\theta1-\theta2) \ne null$ 

(4)$(q_1,\theta_1)$和$(q_2,\theta_2)$相交,但是互不包含

然后根据这些关系执行对不同对象的分裂操作

![image-20240410164604757](adaptive.assets/image-20240410164604757.png)

我们首先插入$(q_1,\theta1)$查询，此时由于先前没有查询到来，因此树还未建立，我们将$q_1$作为根节点，t为Transition，根据$op_2(t,q,\theta)\ne null$,$op_1(t,q,\theta)\ne null$,$op_1(t,q,\theta)=null$分为三类，即为左中右孩子节点，如图所示，左孩子节点包含$[t_0,t_1]$，中孩子节点包含$[t_2,t_4]$，右孩子节点包含$[t_5,t_7]$，当第二个查询$q_2$到来时候，假设$q_2$与$q_1$的range有相交，这时候继续将树进行分类，最后新分裂9个节点，其中$v_5$内没有Transition，意味着没有Transition同时满足$op_2(q_1,t,\theta_1) \ne null $且$op_2(q_2,t,\theta_2) \ne null$，根据树的结构，我们可以进行LCR和TCQ查询，LCQ查询结构是左中子节点对应的Transition,即$LCQ(q_2)=\{t_0,t_2,t_3,t_5,t_6\}$，TCQ查询的结果为左孩子节点存储的Transition，为$TCQ(q_2)=\{ t_2,t_5\}$，同理$LCQ(q_1)=\{t_0,t_1,t_2,t_3,t_4\}$,$TCQ(q_1)=\{t_0,t_1\}$.



**Theorem1** (基于三角不等式的Transition剪枝)   假设v是非叶子节点，且v.q和q的范围不相交，  $\theta$为q的范围阈值，令$BD(t,q)_{max} = BD(t,v.q)+BD(v.q,q)$, $BD(t,q)_{min} = BD(t,v.q)-BD(v.q,q)$

- 令$BD(t,q)_ = BD(t,q)_{min}$, 若$op_1(t,q,\theta)= null$，则可以剪枝t **(含义:最小距离大于阈值)**
- 令$BD(t,q)_ = BD(t,q)_{max}$, 若$op_1(t,q,\theta) \ne null$，则t为LCQ和TCQ查询结果 **（含义：最大距离部分小于阈值）**
- 令$BD(t,q)_ = BD(t,q)_{max}$, 若$op_2(t,q,\theta) \ne null$，则t为TCQ查询结果 **（含义：最大距离全部小于阈值）**







**Note:下面的引理的形式化证明都可以基于引理1形式化证明，或者通过画图也行。**



**Theorem2**(左子树的剪枝)  假设v是非叶子节点， $\theta$为q的范围阈值，$v.\theta$是v的范围阈值, 当搜索到左子树(v.left), 

- 若$op_1(v.q, q, \theta+v.\theta)=null$，则可以剪枝v的左子树。**(q和v.q是不相交关系，且t有两个点在v中，那么t必然不可能在v.q中,或者用引理1的三角不等式证明也行，这更加形式化一些)**
- 若$op_2(v,q,\theta-v.\theta) \ne null$，则v.left一定是TCQ和LCQ查询结果。**（v.q在q里面，且t有两个点在v中，那么t两个点一定在v.q里面）**



**Theorem3**(中子树的剪枝)  假设v是非叶子节点，  $\theta$为q的范围阈值，$v.\theta$是v的范围阈值, 当搜索到中子树(v.middle), 

- 若$op_1(v.q,q,\theta+v.\theta)=null$或者$op_2(v.q,q,v.\theta-\theta) \ne null$，则v.middle一定不是TCQ查询结果。**(q和v.q是不相交关系或者q在v.q里面，且t有一个点在v中，那么t必然不可能两个点在q中）**
- 若$op_2(v,q,\theta-v.\theta) \ne null$，则v.middle一定是LCQ查询结果。**(v.q在q里面，且t有一个点在v中，则t必然一个点在q中)**



**Theorem4**(右子树的剪枝)  假设v是非叶子节点，  $\theta$为q的范围阈值，$v.\theta$是v的范围阈值, 当搜索到中子树(v.right), 

- 若$op_2(v,q,v.\theta-\theta) \ne null$，则v.right可以剪枝。**（q在v.q里面，且t两个点都不在v中，则t必然不可能有点在q中）**

  



**Theorem2**(左子树的剪枝)  假设v是非叶子节点，且v.q和q的范围不相交，  $\theta$为q的范围阈值，$v.\theta$是v的范围阈值, 当搜索到左子树(v.left), 令, $BD(t,q) = BD(t,q)_{min}$, 若$op_1(t,q,\theta)=null$，则可以剪枝v的左子树。

 

**Theorem3** (中子树的剪枝)  假设v是非叶子节点，且v.q和q的范围不相交，  $\theta$为q的范围阈值，$v.\theta$是v的范围阈值, 当搜索到中子树(v.middle), 令$BD(t,q)_{max} = BD(t,v.q)+BD(v.q,q)$, $BD(t,q)_ = BD(t,q)_{max}$, 若$op_2(t,q,\theta-v.\theta)\ne null$，则t一定是LCQ和TCQ查询结果。

- 令$BD(t,q)_ = BD(t,q)_{min}$, 若$op_1(t,q,\theta)= null$，则可以剪枝v的中子树
- 令$BD(t,q)_ = BD(t,q)_{max}$, 若$op_1(t,q,\theta) \ne null$，则t一定为LCQ结果
- 令$BD(t,q)_ = BD(t,q)_{max}$, 若$op_2(t,q,\theta) \ne null$，则t一定为LCQ结果



**Theorem4** (右子树的剪枝)  假设v是非叶子节点，且v.q和q的范围不相交，  $\theta$为q的范围阈值，$v.\theta$是v的范围阈值, 当搜索到右子树(v.right), 令$BD(t,q)_{max} = BD(t,v.q)+BD(v.q,q)$, $BD(t,q)_{min} = BD(t,v.q)-BD(v.q,q)$

- 令$BD(t,q)_ = BD(t,q)_{min}$, 若$op_1(t,q,\theta-v.\theta)\ne null$，则t一定是LCQ和TCQ查询结果。
- 令$BD(t,q)_ = BD(t,q)_{max}$, 若$op_2(t,q,v.\theta-\theta) \ne null$，则t可以被剪枝掉

 







**Theorem2 (**中子树的剪枝) 当搜索到中子树，对于给定Transition $A,B,C$, $\theta$为q的范围阈值，$v.\theta$是v的范围阈值,令$BD(A,C)_{max} = BD(A,B)+BD(B,C)$,$BD(A,C)_{max} = BD(A,C)$,若$op_2(A,C,\theta-v.\theta)\ne null$，则可以剪枝



**Theorem3** (右子树的剪枝) 当搜索到左子树，对于给定Transition $A,B,C$, $\theta$为q的范围阈值，$v.\theta$是v的范围阈值,令$BD(A,C)_{min} = BD(A,B)-BD(B,C)$,$BD(A,C)_{min} = BD(A,C)$,若$op_1(A,C,v.\theta+\theta)=null$，则可以剪枝



**Theorem 1** (Transition之间的三角不等式) 给定Transition $A,B,C$,$minDis(A,C)$可以使用$minDis(A,B),maxDis(A,B)$,$minDis(B,C),maxDis(B,C)$表示

$$maxDis(A,C) \leq \min \{ maxDis(A,B)) + minDis(B,C), maxdDis(B,C)) + minDis(A,B)\}$$

$$minDis(A,C) \ge\max \{ minDis(A,B)) - maxDis(B,C), minDis(B,C) - maxDis(A,B),0 \}$$



**Theorem 2** (基于最小距离的剪枝1) 假设v是非叶子节点，且v.q和q的范围不相交，对于Transition t, 若

​		$$\min \{ minDis(t,v.q) - maxDis(q,v.q), minDis(q,v.q) - maxDis(t,v.q) \} > \theta$$

则

​		t可以被剪枝掉，即t不可能为LCQ和TCQ查询结果

(a) 当搜索到v.right的时候，只需要判断$v.\theta - maxDis(q,v.q) > \theta$即可剪枝

(b) 当搜索到v.left，只需要判断$minDis(q,v.q) - v.\theta > \theta$即可剪枝



**Theorem 3** (基于最大距离的剪枝) 假设v是非叶子节点，且v.q和q的范围不相交，对于Transition t, 若

​		$$\min \{ maxDis(q,v.q) + minDis(v.q,t), maxDis(v.q,t) + minDi(q,v.q) \} < \theta$$

则

​		t一定是LCQ和TCQ查询结果

(a)当搜索到v.middle的时候，只需要判断$v.\theta+maxDis(q,v.q)<\theta$即可

(b)当搜索到v.right的时候，只需要判断$v.\theta+minDis(q,v.q)<\theta$即可









