# UPSimulator
UPSimulator is an universal simulator for membrane computing.  
Now, we have supported most features of cell-like P system in UPSimulator.
# Usage
### UPLanguage
To describe the complex rules in various types of P systems, we designed a new language called UPLanguage. The UPLanguage description string is the input of UPSimulator, and the grammar file of UPL is placed in resources/grammar/UPLanguage.g4.

### Simple Usage
The result of the case bellow is an empty membrane "Environment". The explanation of code follows a double slash.

```
Membrane TestMembraneClass{				//Define a membrane class
	Rule r1[i] = a b^2 c[i] -> d f;			//r1 has a dimension i
	Rule r2= d -> ;					//d will disappear 
	Rule r3= f -> m.dissolve;				//membrane will dissolve
}

Environment{						//Skin membrane or simulation environment
	Membrane TestMembraneClass a{			//Get a instance named 'a' of TestMembraneClass
		object a,b^2,c[1];				//initialize a's Object Multiset to {a,b,b,c[1]}
	}
}
```
### Skin Membrane or Simulation Environment
There is a special membrane named "Environment" in UPL. "Environment" can be treated as a skin membrane or an environment, and only the membranes or rules in "Environment" will be simulated.


### Membrane Class
As class in Java and C++, membrane class is encapsulation which have a certain function for some purposes. Membrane can contain properties, sub-membranes, objects, rules.

#### Create a Instance of one Membrane Class
Create an instance is very simple. Three ways to create instances are showed in following code, where a1 and a4 equal to A, and a2 have more objects than A, and a3 contains another instance a4 of A.  

```
Membrane A{

}

Environment{
	Membrane A a1;
	
	Membrane A a2{
		Object c;
	}
	
	Membrane A a3{
		Membrane A a4;
	}
}
```

#### Extend a Membrane Class
Membrane class can extends other membrane class's rules, objects, properties, sub-membranes through the following way.

```
Membrane B{
}  
  
Membrane C extends B{
}  
 
Environment{  
	Membrane C c;
}  
```

### Object
When declare a object, you need to specify its name and quantity (if more than one copy). And you can specify its dimension as well. Following is an example.

```
Object a,b^2,c[1]^1,d[2][3];
```

Code above can be written in a membrane class

```
Membrane A{
	Object a,b^2,c[1]^1;
}
```
or a membrane instance.

```
Environment{
	Membrane A a1{
		Object a,b^2,c[1]^1;
	}
}
```

### Rule
Rules have their name and dimensions (if needed). The dimension of rules can be used to restrict the relationship between objects.

``` 
Rule r1= a -> b;  
Rule r2= b -> ;  
Rule r2[i]= c[i] -> d[i] f[i+1] g[i%10];  
```
Also, they can be written in membrane classes or membrane instances. 

### Membrane Property
Membrane property is some property on the membrane, including polarity, thickness, and other properties. Membrane property can be used to restrict the execution of rules. And the execution of rules can change properties as well. In the case below, r1 will execute while property 'a' of B equals to 1, and r2 will change the property 'a' to 2.

```
Membrane B{  
	Property a=1;  
	Rule r1= a -> b | m.a==1;  
	Rule r2= b -> m.a=2;  
}  
```
Membrane properties can also be written in instances.

# Other Projects used in UPSimulator
## ANTLR
ANTLR is a powerful parser generator, used to recognize UPLanguage.

## JEval
JEval is the advanced library for high-performance, mathematical, boolean and functional expression parsing and evaluation, used to calculate the dimension relationship. 
