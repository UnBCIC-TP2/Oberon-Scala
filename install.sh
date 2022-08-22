#! /bin/bash

# Remove any previous version of sdkman
sudo rm -rf ~/.sdkman

# Update or install the basic requirements
sudo apt install bash zip unzip curl -y

# Get the SDKMAN (easiest way to get sdk and sbt)
curl -s "https://get.sdkman.io" | bash

# Calls a new terminal with source sdk commands (required to run sdk command)
bash -c "source /home/$USER/.sdkman/bin/sdkman-init.sh; 
sdk install java;
sdk install scala
sdk install sbt; 
sdk version;"

echo ""
echo "* SDKMAN IS A POWERFUL DEVELOPMENT KIT MANAGER *"
echo ""
echo "E.g.:"
echo "sdk install sbt"
echo "sdk current sbt"
echo "sdk list sbt"
echo ""
echo "[DONE] Requirements installed. Just run the project."

# Terminal update to run new sdk commands
bash