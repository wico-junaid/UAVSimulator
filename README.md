# UAVSimulator

Project to simulate the UAV messages based on Mavlink and UDP protocol.

## Running

1. Install JDK 11 and clone the repository.
2. Run 'mvn compile' and 'mvn clean install' to get the jar file.
3. Open the terminal or command line and switch to root and then target folder.
4. Run the following command,


```java -jar UAVSimulator-0.0.1-SNAPSHOT.jar Start_Latitude Start_Longitude End_Latitude End_Longitude Speed Altitude Change_Alt_per_second```

### Elaboration on parameters
In the above command, following are the elaboration of parameters:
1. Start_Latitude: Starting latitude in degrees
2. Start_Longitude: Starting longitude in degrees
3. End_Latitude: Ending latitude in degrees
4. End_Longitude: Ending longitude in degrees
5. Speed: Movement speed in meters per second
6. Altitude: Altitude above mean sea level 
7. Change_Alt_per_second: Change of altitude with time per seconds

#### Options to run the application
Two options to run this application,
1. Provide only the two coordinates, the start and end position and rest are all set with constants. Value of speed is 100, Altitude set at 10 and change of altitude is 5.
2. Provide every parameters with the manual specified input.