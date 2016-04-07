#Todos

##Doing
WEBRT FEAT mousing over a station show the board. That certainly means an REST service, maybe akka-http

##Backlog
WEBRT FEAT add back the timer on last data update
WEBRT FEAT position transition of train with a nice trailing fade out
WEBRT BUG  positions & stations moves when panning the map. + stations come with a gliding transitions
KAFKA FEAT kafka station boards. Volme of data is too big for every few seconds. check first how much we reduce the transfer. Can we gzip messages?
FEED  BUG  kafka send error [ 'LeaderNotAvailable' ]
WEBRT FEAT can we gzip websocket?
WEBRT BUG  AW Snap bug when viewer get's up for some time - train position might be enough

## Done

WEBRT FEAT the map adapt to screen size
WEBRT FEAT sort stations by little size pointed forwards
FEED  IMPR mock feeder must put actual date...
WEBRT IMPR train pos as dot, bigger, then category, then name (+dest?)
KAFKA IMPR kafka positions snapshot message should be tablified. Volme of data is too big for every few seconds. check first how much we reduce the transfer. Can we gzip messages?
WEBRT FEAT train circle evolves w/ zoom
WEBRT FEAT #delay is a an arc + size change with zoom
