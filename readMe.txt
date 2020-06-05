
Para compilação utilizei:
  javac -encoding utf8

versão do java "12.0.2" 2019-07-16

O projeto tem 2 partes principais, a leitura e pesquisa.
De inicio existe um menu que faz essas operaçoes caso queira testar por fora,
mas pode ocorrer tambem com adições e pesquisas dentro da main. No caso de qualquer
entrada que não seja do tipo de dado esperado o menu vai lançar uma exeção que
não será tratada.

A adição busca se os termos existem se no 1 arq, se existir adiciona a ID ao bloco
se não ele gera um novo bloco. O processo de andar nos blocos funciona de maneira
recursiva.

A busca é tida por uma string, os termos sao separados e pesquisados de maneira
independente. Após pegar as IDs de um termo é feita uma intersao dos valores dos
termos anteriores e do novo. A movimentações nos blocos de IDs tambem se da de
maneira recursiva nesse caso. O metodo retorna um Array com as IDs que possuem
todos termos.

Toda entrada de entrada passa por uma limpeza para ser usada internamente.
