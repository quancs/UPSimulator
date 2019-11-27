[in English](https://github.com/quancs/UPSimulator/blob/master/README-EN.md)

# 已发表论文
[1]	P. Guo, C. Quan, and L. Ye, “A Simulator for Cell-Like P System,” in Bio-inspired Computing: Theories and Applications, 2018, pp. 223–235.

[2]	P. Guo, C. Quan, and L. Ye, “UPSimulator: A general P system simulator,” Knowl.-Based Syst., vol. 170, pp. 20–25, Apr. 2019.

# UPSimulator
UPSimulator是一个通用的、高性能的P系统仿真工具。现在，UPSimulator已经支持类细胞P系统、类组织P系统、类神经P系统的绝大部分特性。 [点击此处下载最新版UPSimulator](https://github.com/quancs/UPSimulator/releases)

UPSimulator的设计原则是：没有假设即是最好的假设！ 我们不会对你要仿真的模型做任何假设/限制，因为我们不可能知道未来会有什么样的P系统被提出来，也不可能预测新的P系统会包含哪些特性。如果你想要仿真的P系统的概念（条件、结果、通道）已经被UPSimulator支持，那么该P系统就可以被UPSimulator所仿真。仿真的过程也很简单，只需要按照[语法规则](#uplanguage)将您的P系统所包含的条件、结果以及通道写进一个文本文件，导入UPSimulator即可。

## 支持的概念
促进剂（Promoter）、抑制剂（Inhibitor）、规则执行概率（Probability）、规则优先级（Rule Priority）、正则表达式（Regular Expression）、膜厚度（Thickness）、膜溶解（Dissolution）、膜极性（Polarity）、膜分裂（Division）、膜创建（Creation）、同向反向转运（Symport/ Antiport）、膜多通道（Multiple Channels）、正对象与反对象（object/Anti-object）/正脉冲与反脉冲（spike/Anti-spike）、规则延迟执行（delay）

# UPLanguage
为了描述各种P系统内复杂的规则，我们设计了一门语言UPLanguage。**UPLanguage的具体语法规范，可查阅[resources/grammar/UPLanguage.g4](https://github.com/quancs/UPSimulator/blob/master/resources/grammar/UPLanguage.g4)**.

# UPS使用方法

## 1. 启动UPS
UPS的运行环境为Java 1.8+。你可以双击UPSimulator.jar运行，或者在命令行使用如下命令运行

```
java -jar UPSimulator.jar
```
## 2. 下载实例或者书写实例
点击[此处](https://github.com/quancs/UPSimulator/tree/master/examples)下载实例，或者保存以下例子到文本文件（更多详细的用法参照[条件](#条件)以及[结果](#结果)）。在以下示例代码中，双斜杠被用来注释代码。

#### 类细胞P系统 Cell-like P Systems
```
Membrane A{// 膜类A
	//可以在此处定义对象、规则、属性
}

Environment{// 皮肤膜
	Object a^2,b,c[1][2][3],d[1];// 对象
	Rule r1= a^2 b -> c; //规则
	Rule r2[i][j][k]= c[i][j][k] -> ( d[i+1][j*3][k/4], in a );// 一类规则（带维度的规则）
	Rule r1[i]= d[i] -> ( d[i], in b[i]);// 目标膜也可以包含维度
	
	Membrane A a{// 膜类A的一个实例，名称a
		//额外的对象、规则、属性
		//此处可定义到邻居的通道
	}
	
	Membrane A b[1];// 膜类A的另外一个实例，名称b[1]
}
```
#### 类组织P系统 Tissue-like P Systems
类组织P系统和类细胞P系统的差异在于[膜状态](#膜属性或膜状态) 和 [网状膜结构](#通道)

```
Membrane A{// 膜类A
	Property status=1;//定义一个属性，名称为status，属性值为1
	Rule r1= <status=1> a -> <status=2> b;
}

Environment{// 皮肤膜
	Membrane A a1{
		Object a;
		Rule r2 = <status=2> b -> ( c, go a2 );
		Tunnel a2;
	}
	
	Membrane A a2{
		Object a;
		Rule r2 = <status=2> b -> ( c, go a1 );
		Tunnel a1;
	}
}
```
#### 类神经P系统 Neural-like P system
类神经P系统和类组织P系统的差异之处在于[正则表达式条件](#正则表达式条件)

```
Environment{
	Membrane a1{
		Object a;//脉冲
		Rule r1 = aa/ a -> ( a, go all );// 对应脉冲神经P系统规则: aa/a -> a
		Rule r2 = aaa/ a -> ( a, go all );// 对应脉冲神经P系统规则: aaa/a -> a
		Rule r3 = a/ a -> ( a, go all );// 对应脉冲神经P系统规则: a-> a
		Tunnel a2;
	}
	
	Membrane A a2{
		Object a;
		Rule r1 = a*/ a -> ( a, go all );
		Tunnel a1;
	}
}
```
**注意:** 如果您想要仿真脉冲神经P系统中隐藏正则表达式的规则**a -> a**，你必须手动写出其隐含的正则表达式，如rule **r3**。 如果没有正则表达式，则规则就会按照类细胞P系统的方式执行。

## 3.导入和仿真
启动完成之后，具体仿真步骤参照下图
![image](https://github.com/quancs/UPSimulator/raw/master/examples/How-to-use-UPSimulator.png)

具体步骤如下：
- 在**Environment**栏导入下载或保存的文件
- 选择需要仿真的**Environment**
- 点击**Initialize Environment**进行初始化
- 若初始化无误，则可以点击**Run To End**运行代码至P系统停机，或点击**Run One Step**运行代码一个时间拍
- 若初始化失败，你可以点击**Check Grammar**来检查是否存在语法错误（语法错误会被标红）

# UPL详细使用说明
## 1. 膜
在UPL里面可以使用各种类型的膜。若干个膜加上不同的通道，即可组成不同的膜结构（类细胞、类组成、类神经等）。
### 皮肤膜
在UPL里面，存在一个特殊的膜"Environment"。"Environment"是一个皮肤膜，只有被定义在皮肤膜内的膜和规则才会被仿真。

```
Environment{
	//Rules, Objects, Sub-membranes, Properties 
}
```
### 普通膜  
```
Environment{  
	Membrane d{
		//Rules, Objects, Sub-membranes, Properties, Tunnels
	}
	//Rules, Objects, Sub-membranes, Properties
}  
```

### 膜类
UPL是一门面向对象的语言，可以创建一个含有特殊功能的膜类。膜类可以包含properties, sub-membranes, objects, rules。

#### 实例化膜类
以下示例展示了如何创建一个膜类的实例，其中a1，a4和A是相等的，a2比A拥有更多的对象，a3比A多了一个子膜a4.

```
Membrane A{
	//Rules, Objects, Sub-membranes, Properties
}

Environment{
	Membrane A a[1];
	
	Membrane A a[2]{
		Object c;
		//Rules, Objects, Sub-membranes, Properties, Tunnels
	}
	
	Membrane A a[3]{
		Membrane A a4;
		//Rules, Objects, Sub-membranes, Properties, Tunnels
	}
}
```

#### 继承膜类
膜类可以继承其他膜类的rules, objects, properties, sub-membranes。在以下示例中，膜C继承了膜B。

```
Membrane A{
}

Membrane B{
	Membrane A a{
		//Rules, Objects, Sub-membranes, Properties, Tunnels
	}
	//Rules, Objects, Sub-membranes, Properties
}  
  
Membrane C extends B{
	//Rules, Objects, Sub-membranes, Properties
}  

Environment{  
	Membrane C c;
}  
```


## 2. 对象或者脉冲
脉冲在UPL里面的使用是和对象完全一致的。以下是例子

```
Object a,b^2,c[1]^1,d[2][3];
```

以上代码也可以被放到膜类里面：

```
Membrane A{
	Object a,b^2,c[1]^1;
}
```
或者实例里面：

```
Environment{
	Membrane A a1{
		Object a,b^2,c[1]^1;
	}
}
```

反对象和反脉冲以以下方式定义：

```
Object -a,-b^2,-c[1]^1,-d[2][3];
```
## 3. 膜属性
膜属性是在膜上的属性，例如 **polarity**, **thickness**, **status in tissue-like P system**。膜属性可以被用于限制规则的执行，规则也可以更改膜属性。以下例子中，r1会在p属性为1时执行，r2会将膜属性改为2。

```
Membrane B{  
	Property p=1;  
	Rule r1= <p=1> a -> b;  
	Rule r2= b -> <p=2>;  
}  
```
膜属性也可以定义在膜实例里面。  


## 4. 通道
通道位于两个膜之间，存在**方向性**，可以被用来转移对象：

```
Environment{
	Membrane a{
		Tunnel b,c;
	}
	
	Membrane b{
		Tunnel a;
	}
	
	Membrane c{
	}
}
```
**特殊通道** : 自己到自己的通道，以及父子膜之间的通道都是默认存在的、不可见的。 

## 5. 规则
规则由[条件](#条件)和[结果](#结果)组成，同时每个规则都可以包含多个维度：

```
Rule r1= a -> b;  
Rule r2= b -> ;  
Rule r2[i]= c[i] -> d[i] f[i+1] g[i%10];  
```

### 条件
#### 对象条件、脉冲条件、同向反向转运条件
```
Rule r= a -> ;//No result: a -> λ

Rule r1= a -> b;
Rule r2= a^2 -> b;
Rule r3[i]=a[i] -> b[i+1];

Rule r41[i]= out{a[i]} -> a[i] ;//从父膜转运物质进来 
Rule r42[i]= b[i] -> ( b[i], out);//转运物质到父膜 
Rule r43[i]= out{a[i]} b[i] -> a[i] ( b[i], out);//同向反向转运 

Rule r51[i]= in.m[i]{a[i]} -> a[i] ;//从子膜m[i]转运
Rule r52[i]= b[i] -> ( b[i], in m[i]);//转运到子膜m[i]
Rule r53[i]= in.m[i]{a[i]} b[i] -> a[i] ( b[i], in m[i]);//同向反向转运 

Rule r61[i]= go.m[i]{a[i]} -> a[i] ;//从邻居膜m[i]转运
Rule r62[i]= b[i] -> ( b[i], go m[i]);//转运到邻居膜m[i]
Rule r63[i]= go.m[i]{a[i]} b[i] -> a[i] ( b[i], go m[i]);//同向反向转运 
```
在上述代码中， **a** 、 **a[i]** 、 **a^2** 都是对象条件。

#### 正则表达式条件
```
Rule r1= a(aa)*/ a^2 -> a;
Rule r2= -a/ -a -> a;
```
在上述代码中， **a(aa)** 和 **-a** 都是正则表达式条件. **a** 和 **-a** 分别代表正反脉冲。

#### 属性条件或状态条件
```
Rule r= <status=1> a -> b;
Rule r[i]=<1> a[i] -> b[i+1];
```
在上述代码中， **<status=1>** 和 **<1>** 都是属性条件，且两者等价。

#### 抑制剂条件
```
Rule r= a -> b | !c;
Rule r[i]= a[i] -> b[i+1] | !c[i];
```
在上述代码中， **!c** 和 **!c[i]** 都是抑制剂条件。若对象c存在，则r的抑制剂条件不满足，规则不可能执行；反之，则r的促进剂条件满足，规则可能执行。

#### 促进剂条件
```
Rule r= a -> b | @c;
Rule r[i]= a[i] -> b[i+1] | @c[i];
```
在上述代码中， **@c** 和 **@c[i]** 都是促进剂条件。若对象c存在，则r的促进剂条件满足，规则可能执行；反之，则r的促进剂条件不满足，规则不可能执行。

#### 优先级条件
```
Rule r= a -> b ,1;
Rule r[i]= a[i] -> b[i+1] | @c[i] ,1;
```
在上述代码中， **,1** 是优先级条件。

#### 规则执行概率条件
```
Rule r= a -> b | probability=0.3;
```
在上述代码中， **probability=0.3** 是执行概率条件。规则r按照0.3的概率执行。

#### 布尔条件
```
Rule r[i][j]= a[i] b[j] -> c[i+j] | @d & i!=j & i+j!=10;
```
在上述代码中， **i!=j** 和 **i+j!=10** 都是布尔条件。

### 结果
#### 对象结果或脉冲结果
```
Rule r= a -> ;//No result: a -> λ

Rule r= a -> b;
Rule r= a -> ( b, out);
Rule r[i]=a[i] -> b[i+1];

Rule r= a -> ( b, in d);
Rule r= a -> ( b, in d & f & g);// in all of them
Rule r= a -> ( b, in d | f | g);// in one of them
Rule r= a -> ( b, in random);
Rule r= a -> ( b, in all);

Rule r= a -> ( b, go d);
Rule r= a -> ( b, go d & f & g);// go all of them
Rule r= a -> ( b, go d | f | g);// go one of them
Rule r= a -> ( b, go random);
Rule r= a -> ( b, go all);
```
在上述代码中， **b** 和 **b[i+1]** 都是对象结果或脉冲结果。在对象结果或脉冲结果中，大多数目标指令都是支持的。

#### 属性结果或状态结果
```
Rule r= a -> <2> b;
Rule r[i]= a[i] -> <status=2> b[i+1];
```
在上述代码中， **<2>** 和 **<status=2>** 都是属性结果。

#### 创建新膜结果
```
Rule r1= a ->  A:subm;
Rule r2= a ->  A:subm{ c };
Rule r3[i]=a[i] ->  A:subm[i];
Rule r4[i]=a[i] ->  A:subm[i]{ c[i] };
```
在上述代码中， **A:subm**、 **A:subm{ c }**、 **A:subm[i]** 以及 **A:subm[i]{ c[i] }** 都是创建新膜结果。**subm** 和 **subm[i]** 的区别在于新膜的名字不同，因为 **subm[i]** 包含有一个维度。 **A:subm** 和 **A:subm{ c }** 的区别在于： **A:subm{ c }** 有一个额外的对象 **c** ，而 **A:subm** 创建的膜等价于 **A**。

#### 膜溶解结果
```
Rule r= a -> dissolve;
Rule r= a -> dissolve(all);
```
在上述代码中， **dissolve** 和 **dissolve(all)** 都是膜溶解结果。区别在于，后者会溶解膜内的全部内容（包括子膜、对象等），而前者则溶解掉了膜，而内容则变成父膜的一部分。

#### 膜分裂结果
```
Rule r= a -> divide({ <property=1> b },{ <property=2> c^3 });
```
在上述代码中， **divide** 是膜分裂结果。膜分裂结果会将当前膜分裂成两个部分。分裂结果的内部可以加入属性结果以及对象结果。

#### 延迟结果
```
Rule r= a -> delay(b,2);
```
在上述代码中，**delay(b,2)** 是一个延迟结果，其中**b**是一个对象结果，**2**是延迟的时间拍数目。延迟结果里面可以嵌入对象结果、膜创建结果、膜溶解结果、以及膜分裂结果。

## 6. 支持的数学运算符号
维度内部可以使用数学表达式来表示维度间关系。数学表达式的解析和计算均使用[aviator](https://github.com/killme2008/aviator)框架。其所支持的运算符号可以到其官网查询 https://github.com/killme2008/aviator/

# UPS中使用的其他项目成果
## ANTLR
## aviator

# 作者
* Changsheng Quan, quancs@qq.com  or quancs@cqu.edu.cn
如果你需要任何使用或者自定义方面的帮助，或者你想要对UPS项目做出贡献，都可以联系我。