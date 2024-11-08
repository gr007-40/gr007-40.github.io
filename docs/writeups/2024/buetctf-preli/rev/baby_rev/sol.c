#include <stdio.h>

int main(){
  char obf [32];
  
  obf[0] = 'B';
  obf[1] = 'T';
  obf[2] = 'G';
  obf[3] = 'W';
  obf[4] = 'G';
  obf[5] = 'Q';
  obf[6] = '@';
  obf[7] = '|';
  obf[8] = 'b';
  obf[9] = '{';
  obf[10] = 'd';
  obf[11] = '}';
  obf[12] = 'j';
  obf[13] = 'K';
  obf[14] = ':';
  obf[15] = '@';
  obf[16] = 'W';
  obf[17] = '#';
  obf[18] = 'j';
  obf[19] = 'j';
  obf[20] = 'Q';
  obf[21] = '&';
  obf[22] = 'T';
  obf[23] = '\\';
  obf[24] = '*';
  obf[25] = 'r';
  obf[26] = 'J';
  obf[27] = 'R';
  obf[28] = 'K';
  obf[29] = 'p';
  obf[30] = 'm';
  obf[31] = 'b';
  for(int i = 0; i < 32; i++){
    printf("%c", obf[i] ^ i);
  }
}


