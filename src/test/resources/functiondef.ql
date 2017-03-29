function add(int a,int b){
  return a+b;
};

function sub(int a,int b){
  return a - b;
};

macro 累加 {qh = qh + 100;};

macro initial {
    exportDef int qh;
    exportAlias a qh;    
    a = 0;
};

