echo Starting ECS System
echo Starting Temperature Controller Console
java TemperatureController $1 &
echo Starting Humidity Sensor Console
java HumidityController $1 & 
echo Starting Temperature Sensor Console
java TemperatureSensor $1 &
echo Starting Humidity Sensor Console
java HumiditySensor $1 &
echo ECS Monitoring Console
java ECSConsole $1






