
if(true)then {1==1} else{ 3==3} ;

include includeFunction;

exportDef int mm =1000;

include includeMacro;

initial;
累加;
累加;
累加;

mm = mm + 1;
a = a + 1000000;
System.out.println(a);
return add(a,4) + sub(a,9);
