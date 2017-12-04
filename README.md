# AIAD-Traffic-Management

## Descrição

 * O objetivo deste trabalho é construir uma simulação cujo cenário é constituído pelas diversas ruas e estradas numa cidade. 

    **Check**
 
 * Os agentes correspondem aos automóveis em circulação. Os semáforos têm uma determinada temporização fixa.
 
    **Check**

 * Por seu lado, cada automóvel tem como objetivo chegar a um determinado ponto da cidade, e pode tomar decisões de acordo com a informação que lhe chega ao longo da viagem. Esta informação pode chegar a partir da perceção visual, de 
comunicação com outros automóveis, ou via rádio.

    * Implementação do Rádio **Check**
    * Algoritmo para shortest path **Check**
    * Comunicação com outros automóveis **Check**
    * Outros Behaviours ?
 
 * Devem ser testadas diferentes configurações, tendo em conta zonas ou direções de maior tráfego, populações de automóveis com diferentes módulos de decisão, agentes BDI, etc. 

    * 3 ou 2 Mapas: Grande e Pequeno
    * Diferentes Módulos de decisão (Alterar A*):
        * Por distância mínima **Check**
        * Por menos semáforos (Interceções com + de 1 via de entrada)
        
    * Agentes BDI:
        * Semáforos: Dar preferência a ruas nas interceções com mais carros em espera ?
        * Carros: ?
        
 * Deve ser equacionada a possibilidade de implementar agentes com aprendizagem por reforço, que aprendam quais os melhores trajetos para determinados destinos em determinadas horas do dia.