#Streaming the CFF train position

Reading the raw position from Kafka, we want to get the current position of all train and broadcast back some current location

## dev

A kafka broker must be up (consider ../devtools/cff_mock_feeder & docker-compose up)

    ./activator run
    
### Testing

    ./activator ~test
 