#Todos

##Doing

##Backlog

WEBRT IMPR can we gzip websocket?
WEBRT IMPR position transition of train with a nice trailing fade out
WEBRT FEAT sort stations by delayed to get them frontwards
WEBRT FEAT mousing over a station show the board. That certainly means an REST service, maybe akka-http
WEBRT BUG  positions & stations moves when panning the map. + stations come with a gliding transitions
KAFKA IMPR kafka station boards. Volme of data is too big for every few seconds. check first how much we reduce the transfer. Can we gzip messages?
WEBRT BUG  AW Snap bug when viewer get's up for some time - train position might be enough
MOCKFEED BUG kafka send error [ 'LeaderNotAvailable' ]

## Done

KAFKA IMPR kafka positions snapshot message should be tablified. Volme of data is too big for every few seconds. check first how much we reduce the transfer. Can we gzip messages?
WEBRT FEAT train circle evolves w/ zoom
WEBRT FEAT #delay is a an arc + size change with zoom
WEBRT IMPR train pos as dot, bigger, then category, then name (+dest?)