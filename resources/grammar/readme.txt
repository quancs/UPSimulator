.\antlr4.bat UPLanguage.g4 -no-listener -visitor -package upsimulator.recognizer
.\antlr4.bat UPLanguage.g4 -no-listener -visitor -Xforce-atn  -package upsimulator.recognizer
javac UPLanguage*.java
.\grun.bat UPLanguage start -tree
CQUPSim parrt
^D


.\grun.bat UPLanguage start -gui
Membrane  Binary{
	Rule r1[i]= a[i]^2 -> a[i+1] notend;
}
^D

.\grun.bat UPLanguage start -gui
Membrane aC9 {
	Object a,b[31][4]^1;
	Rule r1[z]=a^21 -> a^1;
	Rule r2=a^2 b -> a;
}

Environment {
	Membrane aC9 son;

	Object a^3,b[3][4]^1;
	Rule r1[z]=a^2 -> a^1;
	Rule r2=a^2 -> a;
	Rule r3=a^2 -> a | @a;
	Rule r3=a^2 -> a | !a;
	Rule r3=a^2 -> m.delete m.p.aaa=1 | !a;
	Rule r3[z][j]=a[z+1][j]^2 -> a[z][j] {a}aC9:1 | z>j;
}
^D


Environment{
	Rule r3[z]=a[z+1]^2 -> a[z] | z>0;
}
