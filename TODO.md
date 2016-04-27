#Todos

##Doing
WEBFT FEAT a header/foor w/ some info
WEBFT FEAT add about (basic info), with scrolling

##Backlog
WEBFT gzip the XHR to lower amazon cost
ALL   FEAT deploy the full stack on amazon
WEBRT FEAT show only limited number of station when zoomed out
WEBRT FEAT filter on station
WEBRT FEAT add occupancy prognosis on the station board
WEBRT IMPR factorize StationBoard & TrainPosition maps. There's a lot in common...
WEBRT FEAT add back the timer on last data update
WEBRT FEAT position transition of train with a nice trailing fade out
KAFKA FEAT kafka station boards. Volme of data is too big for every few seconds. check first how much we reduce the transfer. Can we gzip messages?
FEED  BUG  kafka send error [ 'LeaderNotAvailable' ]
WEBRT BUG  AW Snap bug when viewer get's up for some time - train position might be enough

## Done
WEBFT FEAT add some source info on the watch (to avoid get killed in the first minute)
WEBFT IMPR max-height on station board list (I want ot avoid having the scroll bar appearing on the window, 'cause it's ugly)
WEBRT FEAT add the clock
WEBRT BUG  positions & stations moves when panning the map. + stations come with a gliding transitions
WEBRT BUG  dragging the map is ugly transparency
WEBRT FEAT mousing over a station show the board. That certainly means an REST service, maybe akka-http
WEBRT BUG  both station board & position add their own SVG to google map. this prevents mouse event to go through the lower layer
WEBRT FEAT the map adapt to screen size
WEBRT FEAT sort stations by little size pointed forwards
FEED  IMPR mock feeder must put actual date...
WEBRT IMPR train pos as dot, bigger, then category, then name (+dest?)
KAFKA IMPR kafka positions snapshot message should be tablified. Volme of data is too big for every few seconds. check first how much we reduce the transfer. Can we gzip messages?
WEBRT FEAT train circle evolves w/ zoom
WEBRT FEAT #delay is a an arc + size change with zoom
