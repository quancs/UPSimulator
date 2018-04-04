# UPSimulator
UPSimulator is an univeral simulator for membrane computing.
Now, we have supported most features of cell-like P system in UPSimulator.
# Simple Usage
```
Membrane B{  
	Property a=1;  
	Rule r1= a -> b m.a=2;  
	Rule r2= b -> m.delete;  
}  
  
Membrane C extends B{  
	Rule r3= a -> ( a ,in bm.m.a==1 );  
	Rule r4= c -> ( c ,in bm.m.a==2 );  
}  
  
Environment{  
	Membrane C c;  
}  
```
