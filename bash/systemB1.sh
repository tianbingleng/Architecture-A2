echo open fire controller
java systemB.FireController $1 &
echo open sprinkler controller
java systemB.SprinklerController $1 &
echo open fire console
java systemB.FireConsole $1


