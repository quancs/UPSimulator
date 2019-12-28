# Publications
[1]	P. Guo, C. Quan, and L. Ye, “A Simulator for Cell-Like P System,” in Bio-inspired Computing: Theories and Applications, 2018, pp. 223–235.

[2]	P. Guo, C. Quan, and L. Ye, “UPSimulator: A general P system simulator,” Knowl.-Based Syst., vol. 170, pp. 20–25, Apr. 2019.


# UPSimulator
UPSimulator is a universal and high performance simulator for membrane computing. Now, we have supported most features of cell-like P system & tissue-like P system, and basic support for neural-like P system. Visit https://github.com/quancs/UPSimulator/releases to download the [latest version](https://github.com/quancs/UPSimulator/releases).

The principle of UPSimulator: **What you simulate in UPSimulator is what you use**. We are not going to make any assumptions about the models you want to simulate, because we will never know that what kind of new P system will be proposed in the future, and what kind of new concepts the new P system will have. If you want to simulate one kind of P system whose concepts ( conditions and results and tunnels ) have already been supported by UPSimulator, please just combine these conditions, results and tunnels to make it.

## Motivations
P system simulators are important tools for designing and verifying P systems. And, researchers have developed several P-system simulators such as P-Lingua (MeCoSim) and PMCGPU. But, with the development of membrane computing, many new P system models have been proposed which haven't gotten timely support. For example, cSNP systems, SNP-MC systems and tissue P systems with evolutional symport/antiport rules. Many reasons exist in this situation: the weak extensibility, too many works needed to support new models, and etc. 

To overcome this issue and provide supports for new P system models, we developed UPSimulator, which is flexible, user-friendly, high performance and high-extendable. One software (UPSimulator) and one language (UPLanguage) for everything! The key point of UPSimulator is to support models by supporting their underlying concepts. To do this, we researched and divided some common P system models into pieces: controllers, structures, objects/spikes, properties, rules(conditions and results) and tunnels. All the pieces are supported in UPSimulator. Moreover, each of these pieces can be freely combined and used. 

Usually, new P system models won't have too much differences when compared with old ones. We can support the new model by supporting the underlying concepts in the differences. As for the common concepts, they have been supported since we supported the old models. So, it's an easy work to support new models in UPSimulator. 


## Supported concepts
Promoter, Inhibitor, Probability, Rule Priority, Regular Expression, Thickness, Dissolution, Polarity, Division, Creation of Membrane, Symport/Antiport, Multiple Channels, Object/Anti-object, Spike/Anti-spike, delay

# UPLanguage
To describe the complex rules in various types of P systems, we designed a new language called UPLanguage. **The detailed grammar of UPLanguage is placed at [resources/grammar/UPLanguage.g4](https://github.com/quancs/UPSimulator/blob/master/resources/grammar/UPLanguage.g4)**.

# How to use UPSimulator

## 1. Start UPSimualtor
Java 1.8+ is required to run UPSimulator.  
Run this software by using

```
java -jar UPSimulator.jar
```
or double click the UPSimulator.jar if you have linked jar file to JRE.

## 2. Download examples or write your own instances
Click [here](https://github.com/quancs/UPSimulator/tree/master/examples) to download examples, or save the examples below to files (for more detailed usage, please refer to [Conditions](#Conditions) and [Results](#Results)). In the following examples, double slash is used to make a comment of codes.

#### Cell-like P system
```
Membrane A{// Membrane Class A
	//objects and rules and properties can be defined here
}

Environment{// skin membrane
	Object a^2,b,c[1][2][3],d[1];// objects or spikes
	Rule r1= a^2 b -> c; //rule
	Rule r2[i][j][k]= c[i][j][k] -> ( d[i+1][j*3][k/4], in a );// rules with dimension for the evolution of a class of objects
	Rule r1[i]= d[i] -> ( d[i], in b[i]);// membrane target can also have dimensions in one rule
	
	Membrane A a{// an instance of Class A
		//other objects and rules and properties can be defined here
		//tunnels to neighbors can also be defined here
	}
	
	Membrane A b[1];// another instance of Class A
}
```
#### Tissue-like P system
The differences between cell-like and tissue-like P system are the [Status Property](#3.-membrane-property) and [Net Structure(tunnels)](#4.-tunnel)

```
Membrane A{// Membrane Class A
	Property status=1;
	Rule r1= <status=1> a -> <status=2> b;
}

Environment{// skin membrane
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
#### Neural-like P system
The differences between neural-like and tissue-like P system are the [Regular Expression Condition](#regular-expression-condition) and [Delayed Result](#delayed-result) 

```
Environment{
	Membrane a1{
		Object a;//Spike
		Rule r1 = aa/ a -> ( a, go all );// aa/a -> a
		Rule r2 = aaa/ a -> ( a, go all );// aaa/a -> a
		Rule r3 = a/ a -> ( a, go all );// this rule represents the rule: a-> a
		Tunnel a2;
	}
	
	Membrane A a2{
		Object a;
		Rule r1 = a*/ a -> ( a, go all );
		Tunnel a1;
	}
}
```
**Notice:** If you want to simulate a spiking rule like **a -> a**, which doesn't have a Regular Expression, you must write its hidden Regular Expression as showed in rule **r3**. If you don't write the hidden Regular Expression, the rule without a Regular Expression will be simulated in the way of cell-like P system.

## 3. Import and simulate
After starting UPSimulator, you can simulate your model by referring to the steps below:
![image](https://github.com/quancs/UPSimulator/raw/master/examples/How-to-use-UPSimulator.png)

Steps:
- Import your model in **Environment** column
- Select the **Environment** you want to simulate
- Click **Initialize Environment** to initialize
- If no errors, you can click **Run To End** to run your model till the stop of your model, or you can click **Run One Step** to run your model one more step
- If there are some errors in the initialization step, you can click **Check Grammar** to check if there are any problems in your code (errors will be marked in red)

# The detailed usage of UPLanguage

## 1. Membrane
In UPLanguage, you can use many types of membranes. You can contruct your own membrane structure by using different membranes and tunnels.


### Skin Membrane or Simulation Environment
There is a special membrane named "Environment" in UPL. "Environment" can be treated as a skin membrane or an environment, and only the membranes or rules in "Environment" will be simulated.

```
Environment{
	//Rules, Objects, Sub-membranes, Properties can be added here. 
}
```

### Simple membrane
```
Environment{  
	Membrane d{
		//Rules, Objects, Sub-membranes, Properties, Tunnels
	}
	//Rules, Objects, Sub-membranes, Properties
}  
```

### Membrane Class
UPLanguage is an object-oriented language. You can use it to define membrane classes with special functions. Membrane class can have properties, sub-membranes, objects, or rules.

#### Create an Instance of one Membrane Class
Create an instance is very simple. Three ways to create instances are showed in following code, where a1 and a4 equal to A, and a2 have more objects than A, and a3 contains another instance a4 of A.  

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

#### Extend a Membrane Class
Membrane class can extends other membrane class's rules, objects, properties, sub-membranes through the following way.

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

## 2. Object or Spike
Spike is implemented as an object in UPSimulator. Following is an example.

```
Object a,b^2,c[1]^1,d[2][3];
```

Code above can be written in a membrane class

```
Membrane A{
	Object a,b^2,c[1]^1;
}
```
or an instance.

```
Environment{
	Membrane A a1{
		Object a,b^2,c[1]^1;
	}
}
```
Anti-object or anti-spike can be represented as follows

```
Object -a,-b^2,-c[1]^1,-d[2][3];
```
## 3. Membrane Property
Membrane property is some property on the membrane, including **polarity**, **thickness**, **status in tissue-like P system**, and other properties. Membrane property can be used to restrict the execution of rules. And the execution of rules can change properties as well. In the case below, r1 will execute while property 'p' of B equals to 1, and r2 will change the property 'p' to 2.

```
Membrane B{  
	Property p=1;  
	Rule r1= <p=1> a -> b;  
	Rule r2= b -> <p=2>;  
}  
```
Membrane properties can also be written in instances.  


## 4. Tunnel
Tunnel is a path between two membranes, and it can be used to transfer objects. Tunnels can be defined in following way:

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
**Invisible Tunnels** : the tunnel from parent to child, the tunnel from child to parent, the tunnel from self to self. All the invisible tunnels are defined automatically.   

**Notice: Tunnel is a directed edge. If you want to transfer objects in two directions, you must define tunnel in both membranes.**


## 5. Rule
Rules are formed by [Conditions](#Conditions) and [Results](#Results), and each rule can have many dimensions：

```
Rule r1= a -> b;  
Rule r2= b -> ;  
Rule r2[i]= c[i] -> d[i] f[i+1] g[i%10];  
```
Also, they can be written in membrane classes or instances.   

### Conditions
#### Object Condition or Spike Condition or Symport/Antiport
```
Rule r= a -> ;//No result: a -> λ

Rule r1= a -> b;
Rule r2= a^2 -> b;
Rule r3[i]=a[i] -> b[i+1];

Rule r41[i]= out{a[i]} -> a[i] ;//symport from outter membrane 
Rule r42[i]= b[i] -> ( b[i], out);//antiport to outter membrane 
Rule r43[i]= out{a[i]} b[i] -> a[i] ( b[i], out);//symport and antiport 

Rule r51[i]= in.m[i]{a[i]} -> a[i] ;//symport from inner membrane named m[i] 
Rule r52[i]= b[i] -> ( b[i], in m[i]);//antiport to inner membrane named m[i]
Rule r53[i]= in.m[i]{a[i]} b[i] -> a[i] ( b[i], in m[i]);//symport and antiport 

Rule r61[i]= go.m[i]{a[i]} -> a[i] ;//symport from neighboring membrane named m[i]
Rule r62[i]= b[i] -> ( b[i], go m[i]);//antiport to neighboring membrane named m[i]
Rule r63[i]= go.m[i]{a[i]} b[i] -> a[i] ( b[i], go m[i]);//symport and antiport 
```
In the code above, **a** and **a[i]** are Object Conditions, and **a^2** means two copy of 'a'.

#### Regular Expression Condition
```
Rule r1= a(aa)*/ a^2 -> a;
Rule r2= -a/ -a -> a;
```
In the code above, **a(aa)** and **-a** are both Regular Expression Conditions. **a** and **-a** represent spike or anti-spike respectively.

#### Property Condition or Status Condition
```
Rule r= <status=1> a -> b;
Rule r[i]=<1> a[i] -> b[i+1];
```
In the code above, **<status=1>** and **<1>** are Property Conditions, and **<1>** is the same as **<status=1>**. Only status condition's name can be omitted.

#### Inhibitor Condition
```
Rule r= a -> b | !c;
Rule r[i]= a[i] -> b[i+1] | !c[i];
```
In the code above, **!c** and **!c[i]** are Inhibitor Conditions.

#### Promoter Condition
```
Rule r= a -> b | @c;
Rule r[i]= a[i] -> b[i+1] | @c[i];
```
In the code above, **@c** and **@c[i]** are Promoter Conditions.

#### Priority Condition
```
Rule r= a -> b ,1;
Rule r[i]= a[i] -> b[i+1] | @c[i] ,1;
```
In the code above, **,1** is a Priority Condition.

#### Probabilistic Condition
```
Rule r= a -> b | probability=0.3;
```
In the code above, **probability=0.3** is a Probabilistic Condition.

#### Boolean Condition
```
Rule r[i][j]= a[i] b[j] -> c[i+j] | @d & i!=j & i+j!=10;
```
In the code above, **i!=j** and **i+j!=10** are Boolean Conditions.

### Results
#### Object Result or Spike Result
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
In the code above, **b** and **b[i+1]** are Object Results or Spike Results. And most target instructions are supported. 

#### Property Result or Status Result
```
Rule r= a -> <2> b;
Rule r[i]=a[i] -> <status=2> b[i+1];
```
In the code above, **<2>** and **<status=2>** are Property Results.

#### Membrane Create Result
```
Rule r1= a ->  A:subm;
Rule r2= a ->  A:subm{ c };
Rule r3[i]=a[i] ->  A:subm[i];
Rule r4[i]=a[i] ->  A:subm[i]{ c[i] };
```
In the code above, **A:subm**, **A:subm{ c }**, **A:subm[i]** and **A:subm[i]{ c[i] }** are Membrane Create Results. The difference between **subm** and **subm[i]** is that the name of created membranes are different, where **subm[i]** has a dimension. The difference between **A:subm** and **A:subm{ c }** is that the created membrane of **A:subm{ c }** has one more object **c** and the created membrane of **A:subm** equals to Membrane Class **A**.  

#### Membrane Dissolve Result
```
Rule r= a -> dissolve;
Rule r= a -> dissolve(all);
```
In the code above, **dissolve** is MembraneDissolveResult and **dissolve(all)** is a MembraneDissolveAllResult. MembraneDissolveResult will dissolve current membrane and then all the content inside will be the content of parent membrane. MembraneDissolveAllResult will dissolve the current membrane and all the content inside.

#### Membrane Division Result
```
Rule r= a -> divide({ <property=1> b },{ <property=2> c^3 });
```
In the code above, **divide** is Membrane Division Result. Membrane Division Result will divide current membrane into two parts. Inside the division result, **Property Result**s and **Object Result**s can be added.

#### Delayed Result
```
Rule r= a -> delay(b,2);
```
In the code above, **delay(b,2)** is a delayed result. Delayed result will be delayed to be set. Object Result, Membrane Create Result, Dissolve Result and Division Result can be delayed.

## Mathematical Operators
The mathematical expression's parsing and evaluation is done by [aviator](https://github.com/killme2008/aviator). Please check the supported operators at its website https://github.com/killme2008/aviator/.

# Other Projects used in UPSimulator
## ANTLR
ANTLR is a powerful parser generator, used to recognize UPLanguage.

## aviator
aviator is an advanced library for high-performance, mathematical, boolean and functional expression parsing and evaluation, used to calculate the dimension relationship in UPSimulator. 

# Author
* Changsheng Quan, quancs@qq.com  or quancs@cqu.edu.cn  
If you need any help for usage or development, or you want to make contributions to this project, please contact me.

# Help Needed
Do you have a new P system need to simulate? Are you a software developer with java skills? Do you have expertise in building software? Are you an experienced lead technical writer? Do you like contributing to open source projects? Come and join us in helping UPSimulator to thrive and universal.
